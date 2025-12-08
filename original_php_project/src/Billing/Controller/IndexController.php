<?php

namespace App\Billing\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/",
    name: "index",
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

        return new Response(__METHOD__);
    }
}