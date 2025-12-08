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
    "/clients-list-ajax",
    name: "clients_list_ajax",
    methods: ["GET"]
)]
final class ClientsListAjaxController extends AbstractController
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
                'next_session_at' => date('Y-m-d H:i:s', strtotime('+1 month')),
            ],
            [
                'name' => 'Марфа Угрофъевна',
                'sessions_amount' => 666,
                'id' => $id++,
                'userId' => $id,
                'next_session_at' => date('Y-m-d H:i:s', strtotime('+2 day')),
            ],
            [
                'name' => 'Систала Брабадагох',
                'sessions_amount' => 1,
                'id' => $id++,
                'userId' => $id,
                'next_session_at' => date('Y-m-d H:i:s', strtotime('+2 year')),
            ],
        ];

        return new JsonResponse($content);
    }
}