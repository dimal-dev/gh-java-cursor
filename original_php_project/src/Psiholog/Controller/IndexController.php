<?php

namespace App\Psiholog\Controller;

use App\Psiholog\Service\RouteNames;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/",
    name: "dashboard",
    methods: ["GET"]
)]
final class IndexController extends AbstractController
{

    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        return $this->redirectToRoute(RouteNames::SCHEDULE);

        return $this->render('@psiholog/pages/index.html.twig', $content);
    }
}