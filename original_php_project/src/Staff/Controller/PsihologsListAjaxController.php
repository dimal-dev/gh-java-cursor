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
    "/psihologs-list-ajax",
    name: "psihologs_list_ajax",
    methods: ["GET"]
)]
final class PsihologsListAjaxController extends AbstractController
{

    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $id = 500;
        $content = [
            [
                'name' => 'Данил Хлыщь',
                'sessions_amount' => 50,
                'id' => $id++,
                'userId' => $id,
                'price' => 1500,
                'commission' => 30,
                'date_created' => date('Y-m-d', strtotime('+1 month')),
            ],
            [
                'name' => 'Марфа Угрофъевна',
                'sessions_amount' => 666,
                'id' => $id++,
                'userId' => $id,
                'price' => 2300,
                'commission' => 30,
                'date_created' => date('Y-m-d', strtotime('+2 day')),
            ],
            [
                'name' => 'Систала Брабадагох',
                'sessions_amount' => 1,
                'id' => $id++,
                'userId' => $id,
                'price' => 500,
                'commission' => 30,
                'date_created' => date('Y-m-d', strtotime('+2 year')),
            ],
        ];

        return new JsonResponse($content);
    }
}