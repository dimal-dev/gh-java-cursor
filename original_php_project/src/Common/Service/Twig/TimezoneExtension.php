<?php

namespace App\Common\Service\Twig;

use App\Common\Service\TimezoneHelper;
use DateTime;
use DateTimeZone;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

final class TimezoneExtension extends AbstractExtension
{
    public const DEFAULT_TIMEZONE = 'UTC';

    public function __construct(
        private TimezoneHelper $timezoneHelper
    ) {
    }


    public function getFunctions()
    {
        return [
            new TwigFunction('getTimezoneList', function () {
                return $this->getTimezoneList();
            }),
        ];
    }

    public function getTimezoneList(): array
    {
        $now = new DateTime();
        $timezoneList = DateTimeZone::listIdentifiers(
            DateTimeZone::ALL
        );
        $timezoneToOffset = [];
        foreach ($timezoneList as $item) {
            $timezone = new DateTimeZone($item);
            $timezoneToOffset[$item] = $timezone->getOffset($now);
        }

        asort($timezoneToOffset);

        $timezoneToLabel = [];
        foreach ($timezoneToOffset as $timezone => $offset) {
            $timezoneToLabel[$timezone] = $this->timezoneHelper->getLabelForOffset($offset, $timezone);
        }

        return $timezoneToLabel;
    }
}