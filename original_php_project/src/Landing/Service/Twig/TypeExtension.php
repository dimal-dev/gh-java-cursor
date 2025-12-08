<?php

namespace App\Landing\Service\Twig;

use App\Landing\Service\CurrentTimezoneStorage;
use Symfony\Component\Security\Core\Security;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

class TypeExtension extends AbstractExtension
{
    public function __construct(
    )
    {
    }

    public function getFunctions()
    {
        return [
            new TwigFunction('isBool', fn ($variable) => is_bool($variable)),
        ];
    }
}