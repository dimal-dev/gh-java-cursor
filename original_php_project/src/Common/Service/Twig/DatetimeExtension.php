<?php

namespace App\Common\Service\Twig;

use App\Common\Service\Language;
use App\Common\Service\DateLocalizedHelper;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

final class DatetimeExtension extends AbstractExtension
{
    public function __construct(
        private DateLocalizedHelper $dateLocalizedHelper
    ) {
    }


    public function getFunctions()
    {
        return [
            new TwigFunction('toGoodLookingDatetime', function (string $datetime, $language = Language::RU) {
                return $this->dateLocalizedHelper->getDateTimeGoodLookingLabel(new \DateTime($datetime), $language);
            }),
            new TwigFunction('toGoodLookingDatetimeParts', function (string $datetime, $language = Language::RU) {
                return $this->dateLocalizedHelper->getDateTimeGoodLookingParts(new \DateTime($datetime), $language);
            }),
        ];
    }
}