<?php

namespace App\Landing\Controller;

use App\Common\Service\TimezoneHelper;
use App\Landing\EventSubscriber\TimezoneRetriever;
use App\Landing\Service\UserLocation;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Cookie;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/save-user-timezone",
    name: "save_user_timezone",
    methods: ["POST"]
)]
final class SaveUserTimezoneController extends AbstractController
{

    public function __construct(
        private UserLocation $userLocation
    )
    {
    }

    public function __invoke(Request $request): Response
    {
        $tz = $this->getTimezone($request);

        $response = new JsonResponse([]);

        $cookie = Cookie::create(TimezoneRetriever::TIMEZONE_COOKIE_NAME)
            ->withValue($tz)
            ->withExpires(strtotime('+10 year'))
            ->withDomain($request->getHost())
            ->withSecure(true);

        $response->headers->setCookie($cookie);

        return $response;
    }

    private function getTimezone(Request $request): string {
        $tz = trim((string) $request->request->get('tz'));
        try {
            new \DateTimeZone($tz);
            return $tz;
        } catch (\Throwable $e) {}

        $tz = $this->userLocation->getTimezone($request->getClientIp());
        try {
            new \DateTimeZone($tz);
            return $tz;
        } catch (\Throwable $e) {}

        return TimezoneHelper::DEFAULT_TIMEZONE;
    }
}