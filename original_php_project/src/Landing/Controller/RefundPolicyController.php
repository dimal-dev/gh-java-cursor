<?php

namespace App\Landing\Controller;

use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/refund-policy",
    name: "refund_policy",
    methods: ["GET", "POST"]
)]
final class RefundPolicyController extends AbstractController
{
    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        return $this->render('@landing/pages/refund-policy.html.twig', $content);
    }
}