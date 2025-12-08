<?php

namespace App\Landing\Controller;

use App\Billing\Entity\UserWallet;
use App\Billing\Repository\OrderPsihologScheduleRepository;
use App\Billing\Repository\OrderRepository;
use App\Billing\Repository\UserWalletRepository;
use App\Common\Service\Language;
use App\Common\Service\DateLocalizedHelper;
use App\Landing\Service\CurrentTimezoneStorage;
use App\Landing\Service\LocaleManager;
use App\Landing\Service\OrderCheckoutCreator;
use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologPrice;
use App\Psiholog\Entity\PsihologProfile;
use App\Psiholog\Entity\PsihologSchedule;
use App\Psiholog\Repository\PsihologPriceRepository;
use App\Psiholog\Repository\PsihologProfileRepository;
use App\Psiholog\Repository\PsihologScheduleRepository;
use App\Landing\Service\RouteNames;
use App\User\Entity\User;
use App\User\Service\ConsultationCreator;
use App\User\Service\TimeHelper;
use JetBrains\PhpStorm\Pure;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Core\Security;
use Symfony\Contracts\Translation\TranslatorInterface;

#[Route(
    "/checkout-old",
    name: "checkout_old",
    methods: ["GET", "POST"]
)]
final class CheckoutOldController extends AbstractController
{
    public const SLUG_PARAM_NAME = 'invoice';

    private Psiholog $psiholog;
    private PsihologProfile $psihologProfile;

    public function __construct(
        private string $wayForPayMerchantLogin,
        private string $wayForPayMerchantSecretKey,
        private UrlGeneratorInterface $urlGenerator,
        private PsihologScheduleRepository $psihologScheduleRepository,
        private PsihologPriceRepository $psihologPriceRepository,
        private OrderRepository $orderRepository,
        private PsihologProfileRepository $psihologProfileRepository,
        private OrderPsihologScheduleRepository $orderPsihologScheduleRepository,
        private DateLocalizedHelper $dateLocalizedHelper,
        private OrderCheckoutCreator $orderCheckoutCreator,
        private UserWalletRepository $userWalletRepository,
        private Security $security,
        private ConsultationCreator $consultationCreator,
        private TimeHelper $timeHelper,
        private CurrentTimezoneStorage $currentTimezoneStorage,
        private TranslatorInterface $translator
    ) {
    }

    public function __invoke(Request $request): Response
    {
        if ($request->isMethod('post')) {
            $scheduleId = (int) $request->request->get('time', null);
            $priceId = (int) $request->request->get('price', null);

            if (empty($scheduleId) || empty($priceId)) {
                return $this->redirectBack($request);
            }

            $schedule = $this->psihologScheduleRepository->findById($scheduleId);
            if (!$schedule) {
                return $this->redirectBack($request);
            }

            $price = $this->psihologPriceRepository->findById($priceId);
            if (!$price) {
                return $this->redirectBack($request);
            }

            if ($schedule->getPsiholog()->getId() !== $price->getPsiholog()->getId()) {
                return $this->redirectBack($request);
            }

            $userWallet = $this->getUserWalletIfCanPurchaseFromIt($price);

            if ($userWallet) {
                return $this->purchaseFromWallet($price, $schedule, $userWallet);
            }

            return $this->orderCheckoutCreator->create($request, $price, $schedule);
        }

        $slug = $request->query->get(self::SLUG_PARAM_NAME, '');
        if (empty($slug)) {
            return $this->redirectBack($request);
        }

        $order = $this->orderRepository->findByCheckoutSlug($slug);
        if (empty($order)) {
            return $this->redirectBack($request);
        }

        $priceId = $order->getPsihologPriceId();
        $price = $this->psihologPriceRepository->findById($priceId);
        $this->psiholog = $price->getPsiholog();
        $this->psihologProfile = $this->psihologProfileRepository->findByPsiholog($this->psiholog);

        $orderPsihologSchedule = $this->orderPsihologScheduleRepository->findByOrder($order);
        $schedule = $orderPsihologSchedule->getPsihologSchedule();

        $content = [];
        $content['psihologName'] = $this->getPsihologName();


        $userTimezone = new \DateTimeZone($this->currentTimezoneStorage->getCurrentTimezone());
        $this->timeHelper->setTimezone($userTimezone);
        $availableAt = $this->timeHelper->toUserTzDateTimeFromDateTime($schedule->getAvailableAt());
        $consultationTime = $this->dateLocalizedHelper->getDateTimeGoodLookingLabel(
            $availableAt,
            $request->getLocale()
        );
        $content['psihologProfile'] = $this->psihologProfile;
        $content['price'] = $price;

        $wayForPayParams = [];

        $wayForPayParams['merchantAccount'] = $this->wayForPayMerchantLogin;
        $wayForPayParams['merchantAuthType'] = 'SimpleSignature';
        $wayForPayParams['merchantDomainName'] = 'goodhelp.com.ua';
        $wayForPayParams['merchantTransactionType'] = 'SALE';
        $wayForPayParams['merchantTransactionSecureType'] = 'AUTO';
        $wayForPayParams['language'] = strtoupper($request->getLocale());
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
        $productName = $this->translator->trans('Консультация') . '. ' . $this->translator->trans($this->getPsihologName());
        $wayForPayParams['productName[]'] = $productName;
        $wayForPayParams['productPrice[]'] = $order->getPriceLarge();
        $wayForPayParams['productCount[]'] = 1;

        $wayForPayParams['merchantSignature'] = $this->makeSignature($wayForPayParams);

        $content['wayForPayParams'] = $wayForPayParams;

        $listItems = [];
        $listItems[] = $consultationTime;

        $consultationLabel = '';
        if ($price->isIndividual()) {
            $consultationLabel = $this->translator->trans('Индивидуальная консультация, длится 50 минут');
        } else if ($price->isCouple()) {
            $consultationLabel = $this->translator->trans('Парная/семейная консультация, длится 80 минут');
        }

        $listItems[] = $consultationLabel;
        $listItems[] = $this->translator->trans('Стоимость консультации') . ": {$price->getPrice()} грн";

        $content['listItems'] = $listItems;
        $content['_gaCheckoutData'] = $this->getGaCheckoutData($price);

        return $this->render('@landing/pages/checkout.html.twig', $content);
    }

    #[Pure] private function makeSignature(array $content): string
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

    private function redirectBack(Request $request): \Symfony\Component\HttpFoundation\RedirectResponse
    {
        $defaultUrl = $this->urlGenerator->generate(RouteNames::MAIN_PAGE);
        $redirectUrl = $request->server->get('HTTP_REFERER', $defaultUrl);

        return $this->redirect($redirectUrl);
    }

    #[Pure] private function getPsihologName(): string
    {
        return "{$this->psihologProfile->getFirstName()} {$this->psihologProfile->getLastName()}";
    }

    private function getUserWalletIfCanPurchaseFromIt(PsihologPrice $price): ?UserWallet
    {
        if (!$this->security->isGranted('IS_AUTHENTICATED_FULLY')) {
            return null;
        }
        /** @var User $user */
        $user = $this->security->getUser()->getUser();
        $userWallet = $this->userWalletRepository->findByUser($user);
        if (!$userWallet) {
            return null;
        }

        if ($userWallet->getBalance() < $price->getPriceInCents()) {
            return null;
        }

        return $userWallet;
    }

    private function purchaseFromWallet(
        PsihologPrice $price,
        PsihologSchedule $schedule,
        UserWallet $userWallet
    ): RedirectResponse {
        $userConsultation = $this->consultationCreator->setupConsultation($price, $schedule, $userWallet);
        if ($userConsultation) {
            return $this->redirectToRoute(\App\User\Service\RouteNames::DASHBOARD, [
                'show-successfully-booked-consultation-message' => 1,
            ]);
        }

        $url = $this->urlGenerator->generate(RouteNames::PSIHOLOG_PROFILE, [
            'id' => $schedule->getPsiholog()->getId(),
            'failedToBook' => 1,
        ]);
        $url .= '#failed-to-book';

        return $this->redirect($url);
    }

    private function getGaCheckoutData(PsihologPrice $price): string
    {
        $name = '';
        if ($price->isIndividual()) {
            $name = "Индивидуальная консультация";
        } else if ($price->isCouple()) {
            $name = "Парная консультация";
        }

        $item = [
            'id' => $price->getId(),
            'item_name' => $name,
            'price' => $price->getPrice(),
            'currency' => $price->getCurrency(),
        ];

        $data = [
            'currency' => $price->getCurrency(),
            'price' => $price->getPrice(),
            'item' => [$item],
        ];

        return json_encode($data);
    }
}