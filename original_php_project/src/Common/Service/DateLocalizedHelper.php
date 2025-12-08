<?php

namespace App\Common\Service;

use App\Common\Service\Language;
use Illuminate\Support\Facades\Lang;

class DateLocalizedHelper
{
    private const MONTH_NUMBER_TO_NAME_INCLINED = [
        Language::RU => [
            1 => 'Января',
            2 => 'Февраля',
            3 => 'Марта',
            4 => 'Апреля',
            5 => 'Мая',
            6 => 'Июня',
            7 => 'Июля',
            8 => 'Августа',
            9 => 'Сентября',
            10 => 'Октября',
            11 => 'Ноября',
            12 => 'Декабря',
        ],
        Language::UA => [
            1 => 'Січня',
            2 => 'Лютого',
            3 => 'Березня',
            4 => 'Квітня',
            5 => 'Мая',
            6 => 'Червень',
            7 => 'Липня',
            8 => 'Серпня',
            9 => 'Вересня',
            10 => 'Жовтня',
            11 => 'Листопада',
            12 => 'Грудня',
        ],
    ];

    private const WEEK_DAY_NUMBER_TO_NAME = [
        Language::RU => [
            1 => 'Понедельник',
            2 => 'Вторник',
            3 => 'Среда',
            4 => 'Четверг',
            5 => 'Пятница',
            6 => 'Суббота',
            7 => 'Воскресенье',
        ],
        Language::UA => [
            1 => 'Понеділок',
            2 => 'Вівторок',
            3 => 'Середа',
            4 => 'Четвер',
            5 => 'П\'ятниця',
            6 => 'Субота',
            7 => 'Неділя',
        ],
    ];

    private const WEEK_DAY_NUMBER_TO_SHORT_NAME = [
        Language::RU => [
            1 => 'Пн',
            2 => 'Вт',
            3 => 'Ср',
            4 => 'Чт',
            5 => 'Пт',
            6 => 'Сб',
            7 => 'Вс',
        ],
        Language::UA => [
            1 => 'Пн',
            2 => 'Вт',
            3 => 'Ср',
            4 => 'Чт',
            5 => 'Пт',
            6 => 'Сб',
            7 => 'Нд',
        ],
    ];

    public function getMonthNameByNumberInclined(int $month, string $language): string
    {
        return self::MONTH_NUMBER_TO_NAME_INCLINED[$language][$month];
    }

    public function getWeekDayNameByNumber(int $day, string $language): string
    {
        return self::WEEK_DAY_NUMBER_TO_NAME[$language][$day];
    }

    public function getWeekDayShortNameByNumber(int $day, string $language): string
    {
        return self::WEEK_DAY_NUMBER_TO_SHORT_NAME[$language][$day];
    }

    public function getDateTimeGoodLookingLabel(\DateTime $dateTime, string $language = Language::RU): string
    {
        [
            'time' => $time,
            'dayOfWeekFull' => $dayOfWeekFull,
            'dayOfMonth' => $dayOfMonth,
            'month' => $month,
        ] = $this->getDateTimeGoodLookingParts($dateTime, $language);

        return "{$dayOfWeekFull} на {$time}, {$dayOfMonth} {$month}";
    }

    public function getDateTimeGoodLookingParts(\DateTime $dateTime, string $language): array
    {
        $dayOfWeekNumber = $dateTime->format('N');
        $dayOfMonth = $dateTime->format('j');
        $dayOfWeekFull = $this->getWeekDayNameByNumber($dayOfWeekNumber, $language);
        $month = $this->getMonthNameByNumberInclined(
            $dateTime->format('n'),
            $language
        );
        $time = $dateTime->format('H:i');

        return ['time' => $time, 'dayOfWeekFull' => $dayOfWeekFull, 'dayOfMonth' => $dayOfMonth, 'month' => $month, 'year' => $dateTime->format('Y')];
    }
}
