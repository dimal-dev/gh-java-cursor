<?php

namespace App\Landing\Controller;

use App\Billing\Entity\Checkout;
use App\Billing\Entity\Order;
use App\Billing\Repository\CheckoutRepository;
use App\Billing\Repository\OrderPsihologScheduleRepository;
use App\Billing\Repository\OrderRepository;
use App\Billing\Repository\UserWalletRepository;
use App\Common\Service\DateLocalizedHelper;
use App\Common\Service\TimezoneHelper;
use App\Landing\Service\CurrentTimezoneStorage;
use App\Landing\Service\OrderCheckoutCreator;
use App\Landing\Service\PsihologProfileInfo;
use App\Landing\Service\RouteNames;
use App\Psiholog\Entity\PsihologSchedule;
use App\Psiholog\Repository\PsihologPriceRepository;
use App\Psiholog\Repository\PsihologProfileRepository;
use App\Psiholog\Repository\PsihologScheduleRepository;
use App\User\Service\ConsultationCreator;
use App\User\Service\TimeHelper;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Core\Security;
use Symfony\Contracts\Translation\TranslatorInterface;

#[Route(
    "/checkout",
    name: "checkout",
    methods: ["GET"]
)]
final class CheckoutController extends AbstractController
{
    public const SLUG_PARAM_NAME = 'invoice';
    private Request $request;

    public function __construct(
        private CheckoutRepository $checkoutRepository,
        private PsihologPriceRepository $psihologPriceRepository,
        private TimeHelper $timeHelper,
        private PsihologScheduleRepository $psihologScheduleRepository,
        private CurrentTimezoneStorage $currentTimezoneStorage,
        private DateLocalizedHelper $dateLocalizedHelper,
        private PsihologProfileInfo $psihologProfileInfo,
        private PsihologProfileRepository $psihologProfileRepository,
        private string $wayForPayMerchantLogin,
        private string $wayForPayMerchantSecretKey,
        private UrlGeneratorInterface $urlGenerator,
        private TranslatorInterface $translator,
        private OrderCheckoutCreator $orderCheckoutCreator,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $this->request = $request;

        $content = [];

        $slug = (string) $request->query->get(self::SLUG_PARAM_NAME, '');

        $checkout = $this->checkoutRepository->findBySlug($slug);

        if (!$checkout) {
            throw new NotFoundHttpException();
        }

        $price = $this->psihologPriceRepository->findById($checkout->getPsihologPriceId());
        $schedule = $this->psihologScheduleRepository->findById($checkout->getPsihologScheduleId());

        $content['checkout'] = $checkout;
        $content['price'] = $price;
        $content['schedule'] = $schedule;
        $content['psihologProfileInfo'] = $this->psihologProfileInfo->get($price->getPsihologId());
        $content['psihologProfile'] = $this->psihologProfileRepository->findByPsiholog($price->getPsiholog());
        $content['promocode'] = $checkout->getUserPromocode()?->getPromocode();

        $content['dateInfo'] = $this->getDateInfo($schedule);

        $order = $this->orderCheckoutCreator->create($request, $price, $schedule, $checkout);
        $content['wayForPayParams'] = $this->getWayForPayParams($order, $content['psihologProfile']);
        $content['order'] = $order;


        return $this->render('@landing/pages/checkout.html.twig', $content);
    }

    private function getDateInfo(?PsihologSchedule $schedule): array
    {
        $userTimezone = new \DateTimeZone($this->currentTimezoneStorage->getCurrentTimezone());
        $this->timeHelper->setTimezone($userTimezone);
        $availableAtUserTz = $this->timeHelper->toUserTzDateTimeFromDateTime($schedule->getAvailableAt());
        $dayTimestamp = $availableAtUserTz->getTimestamp();
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

        $dateInfo = [
            'day' => $dayOfWeek,
            'dayFull' => $dayOfWeekFull,
            'dayOfMonth' => date('j', $dayTimestamp),
            'month' => $monthLabel,
            'time' => $availableAtUserTz->format('H:i'),
        ];

        return $dateInfo;
    }

    private function makeSignature(array $content): string
    {
        $paramsFrom = [
            $content['merchantAccount'],
            $content['merchantDomainName'],
            $content['orderReference'],
            $content['orderDate'],
            $content['amount'],
            $content['currency'],
            $content['productName[]'],
            $content['productCount[]'],
            $content['productPrice[]'],
        ];

        $params = implode(';', $paramsFrom);

        return $this->sign($params);
    }

    private function sign(string $params): string
    {
        return hash_hmac("md5", $params, $this->wayForPayMerchantSecretKey);
    }

    private function getWayForPayParams(Order $order, $psihologProfile): array
    {
        $wayForPayParams = [];

        $wayForPayParams['merchantAccount'] = $this->wayForPayMerchantLogin;
        $wayForPayParams['merchantAuthType'] = 'SimpleSignature';
        $wayForPayParams['merchantDomainName'] = 'goodhelp.com.ua';
        $wayForPayParams['merchantTransactionType'] = 'SALE';
        $wayForPayParams['merchantTransactionSecureType'] = 'AUTO';
        $wayForPayParams['language'] = strtoupper($this->request->getLocale());
        $wayForPayParams['returnUrl'] = $this->urlGenerator->generate(
            RouteNames::CHECKOUT_THANK_YOU,
            [],
            UrlGeneratorInterface::ABSOLUTE_URL
        );
        $wayForPayParams['serviceUrl'] = $this->urlGenerator->generate(
            \App\Billing\Service\RouteNames::WEBHOOK,
            [],
            UrlGeneratorInterface::ABSOLUTE_URL
        );
        $wayForPayParams['orderReference'] = $order->getCheckoutSlug();
        $wayForPayParams['orderDate'] = $order->getDateCreated()->getTimestamp();
        $wayForPayParams['amount'] = $order->getPriceLarge();
        $wayForPayParams['currency'] = $order->getCurrency();
        $productName = $this->translator->trans('Консультация') . '. ' . $this->translator->trans(
                $psihologProfile->getFullName()
            );
        $wayForPayParams['productName[]'] = $productName;
        $wayForPayParams['productPrice[]'] = $order->getPriceLarge();
        $wayForPayParams['productCount[]'] = 1;

        $wayForPayParams['merchantSignature'] = $this->makeSignature($wayForPayParams);

        return $wayForPayParams;
    }
}