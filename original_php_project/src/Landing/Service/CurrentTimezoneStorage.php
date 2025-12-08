<?php

namespace App\Landing\Service;

class CurrentTimezoneStorage
{
    private bool $timezoneDetected = false;
    private string $currentTimezone = \App\Common\Service\TimezoneHelper::DEFAULT_TIMEZONE;

    public function getCurrentTimezone(): string
    {
        return $this->currentTimezone;
    }

    public function setCurrentTimezone(string $currentTimezone): void
    {
        $this->currentTimezone = $currentTimezone;
    }

    public function isTimezoneDetected(): bool
    {
        return $this->timezoneDetected;
    }

    public function setTimezoneDetected(bool $timezoneDetected): void
    {
        $this->timezoneDetected = $timezoneDetected;
    }
}
