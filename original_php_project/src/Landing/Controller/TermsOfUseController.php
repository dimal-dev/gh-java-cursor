<?php

namespace App\Landing\Controller;

use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/terms-of-use",
    name: "terms_of_use",
    methods: ["GET", "POST"]
)]
final class TermsOfUseController extends AbstractController
{
    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        return $this->render('@landing/pages/terms-of-use.html.twig', $content);
    }
}