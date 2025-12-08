<?php

namespace App\Psiholog\Service\Twig;

use App\Common\Service\Language;
use App\Common\Service\DateLocalizedHelper;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

class Date extends AbstractExtension
{
    public function __construct(
        private DateLocalizedHelper $dateLocalizedHelper
    ) {
    }

    public function getFunctions()
    {
        return [
            new TwigFunction('p_timestampToDayMonthAndYear', function (int $timestamp) {
                $monthDay = date('d', $timestamp);
                $weekDay = date('N', $timestamp);
                $month = date('n', $timestamp);
                $year = date('Y', $timestamp);

                $dayLabel = $this->dateLocalizedHelper->getWeekDayNameByNumber($weekDay, Language::RU);
                $monthLabel = $this->dateLocalizedHelper->getMonthNameByNumberInclined($month, Language::RU);

                return [
                    'dateLabel' => "{$monthDay} {$monthLabel}, {$year}",
                    'dayLabel' => $dayLabel,
                ];
            }),
        ];
    }
}