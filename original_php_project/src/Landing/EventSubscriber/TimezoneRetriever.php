<?php

declare(strict_types=1);

namespace App\Landing\EventSubscriber;

use App\Landing\Service\CurrentTimezoneStorage;
use App\User\Entity\SecurityUser;
use App\User\Entity\User;
use Symfony\Component\EventDispatcher\EventSubscriberInterface;
use Symfony\Component\HttpKernel\Event\RequestEvent;
use Symfony\Component\HttpKernel\KernelEvents;
use Symfony\Component\Security\Core\Security;

class TimezoneRetriever implements EventSubscriberInterface
{
    public const TIMEZONE_COOKIE_NAME = '_cutz_';

    public function __construct(
        private Security $security,
        private CurrentTimezoneStorage $currentTimezoneStorage,
    ) {
    }

    public function onKernelRequest(RequestEvent $event): void
    {
        $request = $event->getRequest();
        if (!$request->attributes->get('is_landing_route')) {
            return;
        }

        $user = $this->security->getUser();
        if ($user instanceof SecurityUser) {
            /** @var User $user */
            $user = $user->getUser();
            $this->currentTimezoneStorage->setCurrentTimezone($user->getTimezone());
            $this->currentTimezoneStorage->setTimezoneDetected(true);
            return;
        }

        if ($request->cookies->has(self::TIMEZONE_COOKIE_NAME)) {
            $timezone = trim((string) $request->cookies->get(self::TIMEZONE_COOKIE_NAME));
            try {
                new \DateTimeZone($timezone);
                $this->currentTimezoneStorage->setCurrentTimezone($timezone);
                $this->currentTimezoneStorage->setTimezoneDetected(true);
            } catch (\Throwable $e) {
            }
            return;
        }

    }

    private function isUserAuthenticated(): bool
    {
        try {
            return $this->security->isGranted('IS_AUTHENTICATED_FULLY');
        } catch (\Exception $e) {
            return false;
        }
    }

    public static function getSubscribedEvents()
    {
        return [
            KernelEvents::REQUEST => [['onKernelRequest']],
        ];
    }
}
