<?php

namespace App\Psiholog\Controller;

use DateTime;
use DateTimeZone;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/payments-history",
    name: "payments_history",
    methods: ["GET"]
)]
final class PaymentsHistoryController extends AbstractController
{

    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        return $this->render('@psiholog/pages/payments/payments-history.html.twig', $content);
    }
}