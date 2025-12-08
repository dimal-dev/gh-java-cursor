<?php

namespace App\User\Service;

use App\Common\Service\TimezoneHelper;
use App\Psiholog\Repository\PsihologSettingsRepository;

class TimeHelper
{
    private ?\DateTimeZone $userTimezone = null;

    public function __construct(
        private TimezoneHelper $timezoneHelper,
    ) {
    }

    public function setTimezone(\DateTimeZone $timezone): void
    {
        $this->userTimezone = $timezone;
    }

    public function toUserTz(string $datetime): string
    {
        return $this->toUserTzDatetime($datetime)->format('Y-m-d H:i:s');
    }

    public function toUserTzDateTime(string $datetime): \DateTime
    {
        return $this->timezoneHelper->toTzDateTime($datetime, $this->getTimezone());
    }

    public function toUserTzDateTimeFromDateTime(\DateTime $datetime): \DateTime
    {
        return $this->timezoneHelper->toTzDateTimeFromDatetime($datetime, $this->getTimezone());
    }

    public function toUtcTz(string $datetime): string
    {
        return $this->toUtcTzDateTime($datetime)->format('Y-m-d H:i:s');
    }

    public function toUtcTzDateTime(string $datetime): \DateTime
    {
        return $this->timezoneHelper->toUtcTzDateTime($datetime, $this->getTimezone());
    }

    private function getTimezone(): \DateTimeZone
    {
        return $this->userTimezone;
    }
}
