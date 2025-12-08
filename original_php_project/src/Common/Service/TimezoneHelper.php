<?php

namespace App\Common\Service;

class TimezoneHelper
{
    public const DEFAULT_TIMEZONE  = 'Europe/Kiev';

    private \DateTimeZone $utcTimezone;

    public function __construct()
    {
        $this->utcTimezone = new \DateTimeZone('UTC');
    }

    public function toTzDateTime(string $datetime, \DateTimeZone $tz): \DateTime
    {
        $date = new \DateTime($datetime);
        $date->setTimezone($tz);

        return $date;
    }

    public function toTzDateTimeFromDatetime(\DateTime $datetime, \DateTimeZone $tz): \DateTime
    {
        $date = clone $datetime;
        $date->setTimezone($tz);

        return $date;
    }

    public function toUtcTzDateTime(string $datetime, \DateTimeZone $fromTz): \DateTime
    {
        $date = new \DateTime($datetime, $fromTz);
        $date->setTimezone($this->utcTimezone);

        return $date;
    }

    public function getLabelForOffset(int $offset, ?string $timezoneName = null): string
    {
        [$sign, $hours, $minutes] = $this->offsetToHoursAndMinutesAndSign($offset);
        $offsetLabel = "{$sign}{$hours}:{$minutes}";


        $label = "(UTC$offsetLabel)";
        if ($timezoneName) {
            $label .= " {$timezoneName}";
        }

        return $label;
    }

    private function offsetToHoursAndMinutesAndSign(int $offset): array
    {
        $minutes = $offset % 3600;
        $minutes = $minutes > 0 ? $minutes / 60 : '00';

        $hours = abs((int) ($offset / 3600));
        $hours = $hours < 10 ? "0{$hours}" : $hours;

        $sign = $offset >= 0 ? '+' : '-';

        return [$sign, $hours, $minutes];
    }
}