<?php

namespace App\Psiholog\Controller;

use DateTime;
use DateTimeZone;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/payments-history-items-ajax",
    name: "payments_history_items_ajax",
    methods: ["GET"]
)]
final class PaymentsHistoryItemsAjaxController extends AbstractController
{

    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [
            [
                'id' => 2,
                'amount' => '5000',
                'date_created' => date('Y-m-d H:i:s', strtotime('-1 month')),
            ],
            [
                'id' => 1,
                'amount' => '7000',
                'date_created' => date('Y-m-d H:i:s', strtotime('-1 year')),
            ],
        ];

        return new JsonResponse($content);
    }
}