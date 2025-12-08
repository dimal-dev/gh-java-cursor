<?php

namespace App\Landing\Controller;

use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/privacy",
    name: "privacy",
    methods: ["GET"]
)]
final class PrivacyController extends AbstractController
{
    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        return $this->render('@landing/pages/privacy.html.twig', $content);
    }
}