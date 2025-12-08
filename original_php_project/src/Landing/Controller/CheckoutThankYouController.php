<?php

namespace App\Landing\Controller;

use App\Billing\Repository\OrderRepository;
use App\Landing\Service\RouteNames;
use App\Psiholog\Repository\PsihologPriceRepository;
use App\Psiholog\Repository\PsihologProfileRepository;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\Routing\Annotation\Route;
use function Sentry\captureException;

#[Route(
    "/checkout-thank-you",
    name: "checkout_thank_you",
    methods: ["GET", "POST"]
)]
final class CheckoutThankYouController extends AbstractController
{
    private const SLUG_PARAM_NAME = 'invoice';

    public function __construct(
        private OrderRepository $orderRepository,
        private PsihologPriceRepository $psihologPriceRepository,
        private PsihologProfileRepository $psihologProfileRepository,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        if ($request->isMethod('post')) {
            $orderReference = $request->request->get('orderReference');
            if ($orderReference) {
                return $this->redirectToRoute(RouteNames::CHECKOUT_THANK_YOU, [
                    self::SLUG_PARAM_NAME => $orderReference,
                ]);
            } else {
                return $this->redirectToRoute(RouteNames::MAIN_PAGE);
            }
        }

        $invoice = $request->query->get(self::SLUG_PARAM_NAME );
        if (!$invoice) {
            return $this->redirectToRoute(RouteNames::MAIN_PAGE);
        }

        $order = $this->orderRepository->findByCheckoutSlug($invoice);
        if (!$order) {
            throw new NotFoundHttpException("Order {$invoice} not found");
        }

        $content['invoice'] = $order->getCheckoutSlug();
        $content['price'] = $order->getPriceLarge();
        $content['psihologName'] = '';

        try {
            $priceId = $order->getPsihologPriceId();
            $price = $this->psihologPriceRepository->findById($priceId);
            $psihologProfile = $this->psihologProfileRepository->findByPsihologId($price->getPsihologId());
            $content['psihologName'] = $psihologProfile->getFullName();
        } catch (\Throwable $e) {
            captureException($e);
        }

        return $this->render('@landing/pages/checkout-thank-you.html.twig', $content);
    }
}