<?php

namespace App\Billing\Controller\Webhook;

use App\Billing\Entity\Order;
use App\Billing\Entity\OrderLog;
use App\Billing\Entity\OrderPsihologSchedule;
use App\Billing\Entity\UserWallet;
use App\Billing\Entity\UserWalletOperation;
use App\Billing\Repository\CheckoutRepository;
use App\Billing\Repository\OrderPsihologScheduleRepository;
use App\Billing\Repository\OrderRepository;
use App\Billing\Repository\UserWalletManager;
use App\Billing\Repository\UserWalletRepository;
use App\Billing\Service\WayForPaySignatureMaker;
use App\Common\Service\GaClient;
use App\Common\Service\Twig\TimezoneExtension;
use App\Psiholog\Entity\PsihologPrice;
use App\Psiholog\Repository\PsihologPriceRepository;
use App\User\Entity\User;
use App\User\Repository\UserRepository;
use App\User\Service\ConsultationCreator;
use App\User\Service\UserCreator;
use Doctrine\ORM\EntityManagerInterface;
use Ramsey\Uuid\Uuid;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use function Sentry\captureException;

#[Route(
    "/webhook",
    name: "webhook_index",
    methods: ["POST"]
)]
final class IndexController extends AbstractController
{
    private const REQUEST_SIGNATURE_FIELDS = [
        'merchantAccount',
        'orderReference',
        'amount',
        'currency',
        'authCode',
        'cardPan',
        'transactionStatus',
        'reasonCode',
    ];

    private const RESPONSE_SIGNATURE_FIELDS = [
        'orderReference',
        'status',
        'time',
    ];

    public function __construct(
        private EntityManagerInterface $em,
        private WayForPaySignatureMaker $signatureMaker,
        private OrderRepository $orderRepository,
        private UserRepository $userRepository,
        private UserWalletRepository $userWalletRepository,
        private PsihologPriceRepository $psihologPriceRepository,
        private UserWalletManager $userWalletManager,
        private OrderPsihologScheduleRepository $orderPsihologScheduleRepository,
        private UserCreator $userCreator,
        private ConsultationCreator $consultationCreator,
        private GaClient $gaClient,
        private CheckoutRepository $checkoutRepository,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $body = json_decode($request->getContent(), true);
        $this->log($body);
        $responseContent = $this->process($body);

        return new JsonResponse($responseContent);
    }

    private function log(array $body): void
    {
        $log = new OrderLog();
        $log->setContent($body);
        $this->em->persist($log);
        $this->em->flush();
    }

    private function process(array $body): array
    {
        if (!$this->isValid($body)) {
            return [];
        }

        $this->processValidOrder($body);

        return $this->makeResponseContent($body);
    }

    private function isValid(array $body): bool
    {
        // ahtung: @warning: REVIEWER PLEASE NOTIFY ME THE MOMENT YOU SEE THIS TEXT
        // ahtung: THE CODE BELOW IS DANGEROUS AND WASN'T INTENDED TO BE RELEASED
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
//        return true;
        $signHere = $this->signatureMaker->make($body, self::REQUEST_SIGNATURE_FIELDS);

        return $signHere === $body['merchantSignature'];
    }

    private function makeResponseContent(array $body): array
    {
        $content = [];
        $content['orderReference'] = $body['orderReference'];
        $content['status'] = 'accept';
        $content['time'] = time();
        $content['signature'] = $this->signatureMaker->make($content, self::RESPONSE_SIGNATURE_FIELDS);

        return $content;
    }

    private function processValidOrder(array $body): void
    {
        $transactionStatus = $body['transactionStatus'];

        $checkoutSlug = $body['orderReference'];
        $order = $this->orderRepository->findByCheckoutSlug($checkoutSlug);

        if ($transactionStatus === 'Approved') {
            if ($order->isApproved()) {
                return;
            }

            $order->setState($order::STATE_APPROVED);
            $order->setApprovedStateAt(new \DateTime());
            $this->setOrderInfo($body, $order);

            $this->createAndSetupUserAndSendGa($body, $order);

            $this->markUserPromocodeUsed($order);
        } elseif ($transactionStatus === 'Pending') {
            if ($order->isPending()) {
                return;
            }

            $order->setState($order::STATE_PENDING);
            $order->setPendingStateAt(new \DateTime());
            $this->setOrderInfo($body, $order);
        }
        $this->em->flush();
    }

    private function setOrderInfo(array $body, Order $order): void
    {
        $params = [
            'email' => 'setEmail',
            'phone' => 'setPhone',
            'cardPan' => 'setCardPan',
            'cardType' => 'setCardType',
            'issuerBankCountry' => 'setIssuerBankCountry',
            'issuerBankName' => 'setIssuerBankName',
            'reason' => 'setReason',
            'reasonCode' => 'setReasonCode',
            'fee' => 'setFee',
            'paymentSystem' => 'setPaymentSystem',
            'clientName' => 'setClientName',
        ];
        foreach ($params as $paramName => $setter) {
            $value = $body[$paramName] ?? null;
            if ($value) {
                $order->$setter($value);
            }
        }

//        [
//            "merchantAccount" => "goodhelp_com_ua",
//            "orderReference" => "c8290dac0c4b40d2a105dc0da3e4b31e",
//            "merchantSignature" => "8fb8a2c6f6786d75b5bd97a3eba9bda2",
//            "amount" => 1,
//            "currency" => "UAH",
//            "authCode" => "625690",
//            "email" => "dartful.s@gmail.com",
//            "phone" => "380675046256",
//            "createdDate" => 1651587027,
//            "processingDate" => 1651587178,
//            "cardPan" => "53****0623",
//            "cardType" => "MasterCard",
//            "issuerBankCountry" => "Ukraine",
//            "issuerBankName" => "MONObank",
//            "recToken" => "",
//            "transactionStatus" => "Approved",
//            "reason" => "Ok",
//            "reasonCode" => 1100,
//            "fee" => 0.02,
//            "paymentSystem" => "googlePay",
//            "acquirerBankName" => "WayForPay",
//            "cardProduct" => "debit",
//            "clientName" => "Дима"
//        ];
    }

    private function createAndSetupUserAndSendGa(array $body, Order $order): void
    {
        /*
         * todo: подумать мне в будущем
            //вот тут не знаю как лучше поступить
            //можно цену и валюту брать из данных, которые провайдер прислал, но что если
            //они расходятся с теми, которые в psiholog_price

            //на сейчас, чтобы не усложнять, я допущу что $price и $schedule всегда верны и никогда не расходятся
            //что при нормальной работе системы и должно быть
            //и оставлю решение этой проблемы себе в будущем

            //еще я допущу что $price и $schedule всегда верные и всегда есть
            //еще я допущу что валюта из $price всегда совпадает с валютой в $userWallet
            //в будущем они могут отличаться, но это корнер кейсы, которые сейчас не суть важны
            //поэтому я оставлю решение этой проблемы тоже себе в будущем
        */
        $price = $this->getPrice($order);

        $user = $this->findOrCreateUser($order);

        $order->setUser($user);
        $userWallet = $this->getUserWallet($user, $price);
        $this->addMoneyToWallet($price, $order, $userWallet);
        $this->em->flush();

        $this->setupConsultation($user, $price, $order, $userWallet);

        try {
            $name = '';
            if ($price->isIndividual()) {
                $name = "Индивидуальная консультация";
            } else {
                if ($price->isCouple()) {
                    $name = "Парная консультация";
                }
            }

//            $this->gaClient->sendPurchaseUA(
//                $order->getGaClientId(),
//                $order->getId(),
//                $order->getCurrency(),
//                $order->getPriceLarge(),
//                $name,
//                $price->getId(),
//            );
        } catch (\Exception $e) {
            captureException($e);
        }
    }

    private function createFakeEmail(): string
    {
        return User::FAKE_USER_EMAIL_PART . Uuid::uuid4()->getHex() . '@gmail.fake';

    }

    private function getPrice(Order $order): PsihologPrice
    {
        $priceId = $order->getPsihologPriceId();

        return $this->psihologPriceRepository->findById($priceId);
    }

    private function getUserWallet(User $user, PsihologPrice $price): ?UserWallet
    {
        $userWallet = $this->userWalletRepository->findByUser($user);
        if (!$userWallet) {
            $userWallet = new UserWallet();
            $userWallet->setUser($user);
            $userWallet->setCurrency($price->getCurrency());
            $this->em->persist($userWallet);
        }

        return $userWallet;
    }

    private function addMoneyToWallet(PsihologPrice $price, Order $order, ?UserWallet $userWallet): void
    {
        $userWalletOperation = new UserWalletOperation();
        $userWalletOperation->setCurrency($price->getCurrency());
        $userWalletOperation->setAmount($price->getPriceInCents());
        $userWalletOperation->setType(UserWalletOperation::TYPE_ADD);
        $userWalletOperation->setReasonType(UserWalletOperation::REASON_PURCHASE);
        $userWalletOperation->setReasonId($order->getId());
        $userWalletOperation->setUserWallet($userWallet);
        $this->em->persist($userWalletOperation);

        $this->userWalletManager->applyOperation($userWallet, $userWalletOperation);
    }

    private function setupConsultation(
        User $user,
        PsihologPrice $price,
        Order $order,
        UserWallet $userWallet
    ): void {
        $orderPsihologSchedule = $this->orderPsihologScheduleRepository->findByOrder($order);
        $psihologSchedule = $orderPsihologSchedule->getPsihologSchedule();

        $userConsultation = $this->consultationCreator->setupConsultation(
            $price,
            $psihologSchedule,
            $userWallet
        );

        if ($userConsultation) {
            $order->setUserConsultation($userConsultation);
            $this->em->flush();
        }
    }

    private function findOrCreateUser(Order $order): User
    {
        [$email, $isEmailReal] = $this->getEmail($order);
        $user = $this->userRepository->findByEmail($email);

        if (!$user) {
            $timezone = $order->getTimezone() ?: TimezoneExtension::DEFAULT_TIMEZONE;
            $user = $this->userCreator->create($email, $timezone, $isEmailReal, $order->getClientName());
            $user->setLocale($order->getLocale());
        }

        return $user;
    }

    private function getEmail(Order $order): array
    {
        $email = $order->getEmail();
        $isEmailReal = true;
        if (empty($email)) {
            $email = $this->createFakeEmail();
            $isEmailReal = false;
        }

        return [$email, $isEmailReal];
    }

    private function markUserPromocodeUsed(?Order $order): void
    {
        try {
            $checkout = $this->checkoutRepository->findById($order->getCheckoutId());
            $userPromocode = $checkout->getUserPromocode();
            if ($userPromocode && !$userPromocode->isUsed()) {
                $userPromocode->setState($userPromocode::STATE_USED);
                $userPromocode->setUsedAt(new \DateTime());
                $this->em->flush();
            }
        } catch (\Throwable $e) {
            captureException($e);
        }
    }
}