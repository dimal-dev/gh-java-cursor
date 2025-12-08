<?php

namespace App\Landing\Controller;

use App\Billing\Entity\Currency;
use App\Common\Service\DateLocalizedHelper;
use App\Common\Service\TimezoneHelper;
use App\Landing\Service\CheckoutCreator;
use App\Landing\Service\CurrentTimezoneStorage;
use App\Landing\Service\PsihologProfileInfo;
use App\Landing\Service\RouteNames;
use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologPrice;
use App\Psiholog\Entity\PsihologProfile;
use App\Psiholog\Entity\PsihologSchedule;
use App\Psiholog\Repository\PsihologPriceRepository;
use App\Psiholog\Repository\PsihologProfileRepository;
use App\Psiholog\Repository\PsihologRepository;
use App\Psiholog\Repository\PsihologScheduleRepository;
use App\Psiholog\Repository\PsihologSettingsRepository;
use App\User\Entity\Promocode;
use App\User\Repository\PromocodeRepository;
use App\User\Repository\UserPromocodeRepository;
use App\User\Service\TimeHelper;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

#[Route(
    "/book-consultation/{id}",
    name: "book_consultation",
    methods: ["GET", "POST"]
)]
final class BookConsultationController extends AbstractController
{
    private Request $request;
    private Psiholog $psiholog;
    private PsihologProfile $psihologProfile;

    public function __construct(
        private PsihologProfileRepository $psihologProfileRepository,
        private PsihologRepository $psihologRepository,
        private PsihologPriceRepository $psihologPriceRepository,
        private TimeHelper $timeHelper,
        private PsihologScheduleRepository $psihologScheduleRepository,
        private CurrentTimezoneStorage $currentTimezoneStorage,
        private PsihologSettingsRepository $psihologSettingsRepository,
        private TimezoneHelper $timezoneHelper,
        private DateLocalizedHelper $dateLocalizedHelper,
        private PsihologProfileInfo $psihologProfileInfo,
        private CheckoutCreator $checkoutCreator,
        private UrlGeneratorInterface $urlGenerator,
        private PromocodeRepository $promocodeRepository,
        private UserPromocodeRepository $userPromocodeRepository,
    ) {
    }

    public function __invoke(int $id, Request $request): Response
    {
        $this->request = $request;
        $content = [];

        $this->psiholog = $this->psihologRepository->findById($id);
        if (!$this->psiholog || !$this->psiholog->isActive()) {
            throw new NotFoundHttpException();
        }

        $this->psihologProfile = $this->psihologProfileRepository->findByPsiholog($this->psiholog);
        if (!$this->psihologProfile) {
            throw new NotFoundHttpException();
        }

        $postResult = $this->handlePost();
        if ($postResult) {
            return $postResult;
        }

        $userTimezone = new \DateTimeZone($this->currentTimezoneStorage->getCurrentTimezone());
        $this->timeHelper->setTimezone($userTimezone);
        $offset = $userTimezone->getOffset(new \DateTime());

        $content['timezoneOffsetLabel'] = $this->timezoneHelper->getLabelForOffset($offset);
        $content['userTimezone'] = $userTimezone->getName();

        $slugPrice = $this->getSlugPrice($this->request);
        $prices = $this->getPrices($slugPrice);
        $schedule = $this->getSchedule($this->psiholog, isset($prices['priceCouple']));
        $pricesPlain = [];

        $hasSchedule = false;
        if (isset($prices['priceIndividual'])) {
            $pricesPlain['indiv']['price'] = $prices['priceIndividual'];
            $pricesPlain['indiv']['schedule'] = $schedule['indiv'];
            $hasSchedule |= count($schedule['indiv']) > 0;
        }
        if (isset($prices['priceCouple'])) {
            $pricesPlain['couple']['price'] = $prices['priceCouple'];
            $pricesPlain['couple']['schedule'] = $schedule['couple'];
            $hasSchedule |= count($schedule['couple']) > 0;
        }

        $content['prices'] = $prices;
        $content['pricesPlain'] = $pricesPlain;
        $content['hasSchedule'] = $hasSchedule;

        if (!$hasSchedule) {
            return $this->redirectToRoute(RouteNames::REQUEST_PSIHOLOG, [
                'id' => $this->psiholog->getId(),
            ]);
        }

        $content['psihologProfile'] = $this->psihologProfile;
        $content['psiholog'] = $this->psiholog;
        $content['psihologProfileInfo'] = $this->psihologProfileInfo->get($this->psiholog->getId());

        $requestConsultationLinkParams = ['id' => $this->psiholog->getId()];
        if ($slugPrice) {
            $requestConsultationLinkParams['tft'] = $slugPrice->getSlug();
        }
        $content['requestConsultationLink'] = $this->urlGenerator->generate(
            RouteNames::REQUEST_PSIHOLOG,
            $requestConsultationLinkParams
        );

        return $this->render('@landing/pages/book-consultation.html.twig', $content);
    }

    private function getSchedule(Psiholog $psiholog, bool $calculateCouplePrices): array
    {
        $scheduleIndividual = [];
        $scheduleCouples = [];

        $psihologScheduleConvenient = $this->getUpcomingAvailableConsultationTimeListGrouped($psiholog);

        foreach ($psihologScheduleConvenient as $day => $timeList) {
            $dayTimestamp = strtotime($day);
            $dayOfWeekNumber = date('N', $dayTimestamp);

            $dayOfWeek = $this->dateLocalizedHelper->getWeekDayShortNameByNumber(
                $dayOfWeekNumber,
                $this->request->getLocale()
            );
            $dayOfWeekFull = mb_strtolower(
                $this->dateLocalizedHelper->getWeekDayNameByNumber(
                    $dayOfWeekNumber,
                    $this->request->getLocale()
                )
            );

            $monthLabel = mb_strtolower(
                $this->dateLocalizedHelper->getMonthNameByNumberInclined(
                    date('n', $dayTimestamp),
                    $this->request->getLocale()
                )
            );

            $dayConfig = [
                'day' => $dayOfWeek,
                'dayFull' => $dayOfWeekFull,
                'dayOfMonth' => date('j', $dayTimestamp),
                'month' => $monthLabel,
                'date' => $day,
            ];

            $individualTimeList = $this->filterTimeList($timeList, 2);
            if (!empty($individualTimeList)) {
                $scheduleIndividual[$day] = $dayConfig + [
                        'timeList' => $individualTimeList,
                    ];
            }
            if ($calculateCouplePrices) {
                $coupleTimeList = $this->filterTimeList($timeList, 3);
                if (!empty($coupleTimeList)) {
                    $scheduleCouples[$day] = $dayConfig + [
                            'timeList' => $coupleTimeList,
                        ];
                }
            }
        }

        return ['indiv' => $scheduleIndividual, 'couple' => $scheduleCouples];
    }

    private function getPriceIndividual(Psiholog $psiholog): ?PsihologPrice
    {
        return $this->psihologPriceRepository->findCurrent(
            $psiholog,
            Currency::UAH,
            PsihologPrice::TYPE_INDIVIDUAL,
        );
    }

    private function getPriceCouple(Psiholog $psiholog): ?PsihologPrice
    {
        return $this->psihologPriceRepository->findCurrent(
            $psiholog,
            Currency::UAH,
            PsihologPrice::TYPE_COUPLE,
        );
    }

    private function filterTimeList(array $availableTimeList, int $amount): array
    {
        $filtered = [];
        foreach ($availableTimeList as $secondsSinceMidnight => $timeInfo) {
            $secondsSinceMidnight1Next = $secondsSinceMidnight + 1800;
            if ($amount === 2) {
                if (isset($availableTimeList[$secondsSinceMidnight1Next])) {
                    $filtered[$secondsSinceMidnight] = $timeInfo;
                }
            }

            if ($amount === 3) {
                $secondsSinceMidnight2Next = $secondsSinceMidnight + 3600;
                if (isset($availableTimeList[$secondsSinceMidnight1Next], $availableTimeList[$secondsSinceMidnight2Next])) {
                    $filtered[$secondsSinceMidnight] = $timeInfo;
                }

            }
        }

        return $filtered;
    }

    private function getSlugPrice(Request $request): ?PsihologPrice
    {
        if (!$request->query->has('tft')) {
            return null;
        }
        $priceSlug = $request->query->get('tft');
        if (empty($priceSlug)) {
            return null;
        }

        return $this->psihologPriceRepository->findBySlug($priceSlug);
    }

    private function getPrices(?PsihologPrice $slugPrice): array
    {
        $prices = [];
        if ($slugPrice) {
            if ($slugPrice->isIndividual()) {
                $prices['priceIndividual'] = $slugPrice;
                $prices['priceCouple'] = $this->getPriceCouple($this->psiholog);
            } else {
                if ($slugPrice->isCouple()) {
                    $prices['priceIndividual'] = $this->getPriceIndividual($this->psiholog);
                    $prices['priceCouple'] = $slugPrice;
                } else {
                    $prices['priceIndividual'] = $this->getPriceIndividual($this->psiholog);
                    $prices['priceCouple'] = $this->getPriceCouple($this->psiholog);
                }
            }
        } else {
            $prices['priceIndividual'] = $this->getPriceIndividual($this->psiholog);
            $prices['priceCouple'] = $this->getPriceCouple($this->psiholog);
        }

        return array_filter($prices);
    }

    private function getUpcomingAvailableConsultationTimeListGrouped(Psiholog $psiholog): array
    {
        $settings = $this->psihologSettingsRepository->findByPsiholog($psiholog);

        $nowUtc = date('Y-m-d H:i:s', strtotime($settings->getScheduleTimeCap()));
        $psihologScheduleList = $this->psihologScheduleRepository->findFromTime(
            $psiholog->getId(),
            $nowUtc,
            PsihologSchedule::STATE_AVAILABLE
        );

        $psihologScheduleConvenient = [];
        foreach ($psihologScheduleList as $item) {
            $availableAtUserTz = $this->timeHelper->toUserTz($item['available_at']);
            $timestamp = strtotime($availableAtUserTz);

            $date = date('Y-m-d', $timestamp);
            $time = date('H:i', $timestamp);
            $secondsSinceMidnight = $timestamp % 86400;
            $psihologScheduleConvenient[$date][$secondsSinceMidnight] = [
                'secondsSinceMidnight' => $secondsSinceMidnight,
                'time' => $time,
                'scheduleId' => $item['id'],
            ];
        }

        return $psihologScheduleConvenient;
    }

    private function handlePost(): ?Response
    {
        if (!$this->request->isMethod('post')) {
            return null;
        }

        $scheduleId = (int) $this->request->request->get('time');
        $schedule = $this->psihologScheduleRepository->findById($scheduleId);
        if (!$scheduleId || ($schedule->getPsihologId() !== $this->psiholog->getId())) {
            return null;
        }

        $priceId = (int) $this->request->request->get('price');
        $price = $this->psihologPriceRepository->findById($priceId);
        if (!$price || ($price->getPsihologId() !== $this->psiholog->getId())) {
            return null;
        }

        $authType = $this->request->request->get('auth-type');
        $name = null;
        $phone = null;
        if ($authType === 'new') {
            $name = $this->request->request->get('new--name');
            $email = $this->request->request->get('new--email');
            $phone = $this->request->request->get('new--phone');
        } elseif ($authType === 'existing') {
            $email = $this->request->request->get('existing--email');
        } else {
            return null;
        }

        $promocode = $this->getPromocode($email);

        $checkout = $this->checkoutCreator->create(
            $this->request,
            $price,
            $schedule,
            $promocode,
            $authType,
            $email,
            $name,
            $phone
        );

        return $this->redirectToRoute(RouteNames::CHECKOUT, [
            CheckoutController::SLUG_PARAM_NAME => $checkout->getSlug(),
        ]);
    }

    private function getPromocode(string $email): ?Promocode
    {
        $promocodeId = (int) $this->request->request->get('applied-promocode');
        if (!$promocodeId) {
            return null;
        }
        $promocode = $this->promocodeRepository->findById($promocodeId);

        if (!$promocode) {
            return null;
        }

        if ($promocode->getMaxUseNumber() < 1) {
            return $promocode;
        }

        $existingNumberOfUses = $this->userPromocodeRepository->countNumberOfUses($email, $promocode->getId());
        if ($existingNumberOfUses >= $promocode->getMaxUseNumber()) {
            return null;
        }

        return $promocode;
    }
}