<?php

namespace App\Staff\Controller;

use DateTime;
use DateTimeZone;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/payouts-list-ajax",
    name: "payouts_list_ajax",
    methods: ["GET"]
)]
final class PayoutsListAjaxController extends AbstractController
{

    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $id = 500;

        $period = date('Y-m-d') . " - " . date('Y-m-d', strtotime('+2 week'));

        $content = [
            [
                'name' => 'Данил Хлыщь',
                'payout_amount' => 9500,
                'id' => $id++,
                'userId' => $id,
                'period' => $period,
                'state' => 1,
                'date_created' => date('Y-m-d', strtotime('+1 month')),
            ],
            [
                'name' => 'Марфа Угрофъевна',
                'payout_amount' => 9500,
                'id' => $id++,
                'userId' => $id,
                'period' => $period,
                'state' => 1,
                'date_created' => date('Y-m-d', strtotime('+2 day')),
            ],
            [
                'name' => 'Систала Брабадагох',
                'payout_amount' => 9500,
                'id' => $id++,
                'userId' => $id,
                'period' => $period,
                'state' => 1,
                'date_created' => date('Y-m-d', strtotime('+2 year')),
            ],
        ];

        return new JsonResponse($content);
    }
}