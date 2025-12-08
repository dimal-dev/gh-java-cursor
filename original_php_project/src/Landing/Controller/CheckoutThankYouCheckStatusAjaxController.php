<?php

namespace App\Landing\Controller;

use App\Billing\Entity\Order;
use App\Billing\Repository\OrderRepository;
use App\Landing\Service\RouteNames;
use App\Psiholog\Repository\PsihologPriceRepository;
use App\User\Entity\SecurityUser;
use App\User\Repository\UserRepository;
use App\User\Service\Authenticator\UserAutoLoginAuthenticator;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Core\Authentication\Token\Storage\TokenStorageInterface;
use Symfony\Component\Security\Core\Authentication\Token\UsernamePasswordToken;
use Symfony\Component\Security\Core\User\UserCheckerInterface;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Security\Guard\GuardAuthenticatorHandler;
use Symfony\Component\Security\Http\RememberMe\RememberMeServicesInterface;
use Symfony\Component\Security\Http\Session\SessionAuthenticationStrategyInterface;

#[Route(
    "/checkout-thank-you-check-status",
    name: "checkout_thank_you_check_status_ajax",
    methods: ["GET"]
)]
final class CheckoutThankYouCheckStatusAjaxController extends AbstractController
{
    public const STATE_FAILED = 3;
    public const STATE_APPROVED = 4;

    private const SLUG_PARAM_NAME = 'invoice';

    public function __construct(
        private OrderRepository $orderRepository,
        private UrlGeneratorInterface $urlGenerator,
        private UserRepository $userRepository,
        private PsihologPriceRepository $psihologPriceRepository,
    ) {
    }

    public function __invoke(Request $request): Response
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

//        return new JsonResponse([
//            'state' => self::STATE_FAILED
//        ]);

//        return new JsonResponse([
//            'state' => self::STATE_APPROVED
//        ]);

        $invoice = $request->query->get(self::SLUG_PARAM_NAME);
        if (!$invoice) {
            return new JsonResponse([
                'state' => 1,
            ]);
        }

        $order = $this->orderRepository->findByCheckoutSlug($invoice);
        if (!$order) {
            return new JsonResponse([
                'state' => 2,
            ]);
        }

        if ($order->isFailed()) {
            return new JsonResponse([
                'state' => self::STATE_FAILED,
            ]);
        }

        if ($order->isApproved()) {
            $user = $order->getUser();
            $autologinToken = $user->getUserAutologinToken();

            $userConsultation = $order->getUserConsultation();
            $linkParams = [
                UserAutoLoginAuthenticator::TOKEN_PARAM_NAME => $autologinToken->getToken(),
            ];

            if (!$userConsultation) {
                $priceId = $order->getPsihologPriceId();
                $psihologPrice = $this->psihologPriceRepository->findById($priceId);
                $psiholog = $psihologPrice->getPsiholog();
                $linkParams['psihologId'] = $psiholog->getId();
            }

            $linkParams['isConsultationBooked'] = $userConsultation ? 1 : 0;

            return new JsonResponse([
                'state' => self::STATE_APPROVED,
                'l' => $this->urlGenerator->generate(\App\User\Service\RouteNames::AUTO_LOGIN, $linkParams),
            ]);
        }

        return new JsonResponse([
            'state' => 5,
        ]);
    }
}