<?php

namespace App\Common\Service\Twig;

use App\Common\Service\Pluralizer;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

final class PluralizerExtension extends AbstractExtension
{
    public function getFunctions()
    {
        return [
            new TwigFunction('pluralize', function (int $amount, string $one, string $few, string $many) {
                return Pluralizer::p($amount, $one, $few, $many);
            }),
        ];
    }
}