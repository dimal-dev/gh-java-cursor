<?php

declare(strict_types=1);

namespace App\Staff\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/logout",
    name: "logout",
    methods: ["GET"]
)]
final class LogoutController extends AbstractController
{
    public function __invoke()
    {
        throw new \Exception('This method can be blank - it will be intercepted by the logout key on your firewall');
    }
}
