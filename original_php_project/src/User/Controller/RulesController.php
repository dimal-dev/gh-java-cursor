<?php

namespace App\User\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Cookie;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/rules",
    name: "rules",
    methods: ["GET"]
)]
final class RulesController extends AbstractController
{
    public const COOKIE_NAME_RULES_WAS_READ = 'gh_rwrcn_';

    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        $response = $this->render('@user/pages/rules.html.twig', $content);

        $cookie = Cookie::create(self::COOKIE_NAME_RULES_WAS_READ)
            ->withValue((string) mt_rand(1232, 145341231))
            ->withExpires(time() + 10 * 365 * 86400)
            ->withDomain($request->getHost())
            ->withSecure(true);

        $response->headers->setCookie($cookie);

        return $response;
    }
}