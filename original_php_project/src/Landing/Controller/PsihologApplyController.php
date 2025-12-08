<?php

namespace App\Landing\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/psyhologam",
    name: "psiholog_apply",
    methods: ["GET"]
)]
final class PsihologApplyController extends AbstractController
{

    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        return $this->render('@landing/pages/psiholog-apply.html.twig', $content);
    }
}