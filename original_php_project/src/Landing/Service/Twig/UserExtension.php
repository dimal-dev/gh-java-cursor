<?php

namespace App\Landing\Service\Twig;

use App\Landing\Service\CurrentTimezoneStorage;
use Symfony\Component\Security\Core\Security;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

class UserExtension extends AbstractExtension
{
    public function __construct(
        private Security $security,
        private CurrentTimezoneStorage $currentTimezoneStorage
    )
    {
    }

    public function getFunctions()
    {
        return [
            new TwigFunction('isUserAuthenticated', function () {
                try {
                    return $this->security->isGranted('IS_AUTHENTICATED_FULLY');
                } catch (\Exception $e) {
                    return false;
                }
            }),
            new TwigFunction('isTimezoneDetected', function () {
                return $this->currentTimezoneStorage->isTimezoneDetected();
            }),
        ];
    }
}