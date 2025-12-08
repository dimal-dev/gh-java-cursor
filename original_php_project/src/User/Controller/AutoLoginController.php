<?php

namespace App\User\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/al",
    name: "auto_login",
    methods: ["GET"]
)]
final class AutoLoginController extends AbstractController
{
    public function __invoke(Request $request): Response
    {
        return new Response('OK');
    }
}