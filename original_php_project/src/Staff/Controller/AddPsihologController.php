<?php

namespace App\Staff\Controller;

use App\Staff\Service\RouteNames;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/add-psiholog",
    name: "add_psiholog",
    methods: ["GET", "POST"]
)]
final class AddPsihologController extends AbstractController
{
    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        if ($request->isMethod('post')) {
            return $this->redirectToRoute(RouteNames::THERAPIST_SETTINGS, [
                'id' => mt_rand(1, 99999),
            ]);
        }

        return $this->render('@staff/pages/add-psiholog.html.twig', $content);
    }
}