<?php

namespace App\Psiholog\Service;

use App\Common\Service\TimezoneHelper;
use App\Psiholog\Repository\PsihologSettingsRepository;

class TimeHelper
{
    private ?\DateTimeZone $psihologTimezone = null;

    public function __construct(
        private CurrentPsihologRetriever $currentPsihologRetriever,
        private PsihologSettingsRepository $psihologSettingsRepository,
        private TimezoneHelper $timezoneHelper,
    ) {
    }

    public function toPsihologTz(string $datetime): string
    {
        return $this->toPsihologTzDatetime($datetime)->format('Y-m-d H:i:s');
    }

    public function toPsihologTzDateTime(string $datetime): \DateTime
    {
        $this->setupPsihologTimezone();

        return $this->timezoneHelper->toTzDateTime($datetime, $this->psihologTimezone);
    }

    public function toUtcTz(string $datetime): string
    {
        return $this->toUtcTzDateTime($datetime)->format('Y-m-d H:i:s');
    }

    public function toUtcTzDateTime(string $datetime): \DateTime
    {
        $this->setupPsihologTimezone();

        return $this->timezoneHelper->toUtcTzDateTime($datetime, $this->psihologTimezone);
    }

    public function toPsihologTzDateTimeFull(\DateTime $datetime): \DateTime
    {
        $this->setupPsihologTimezone();

        $date = clone $datetime;
        $date->setTimezone($this->psihologTimezone);

        return $date;
    }

    public function toUtcTzDateTimeFull(\DateTime $datetime): \DateTime
    {
        $this->setupPsihologTimezone();

        $date = new \DateTime($datetime->format('Y-m-d H:i:s'), $this->psihologTimezone);
        $date->setTimezone($this->utcTimezone);

        return $date;
    }

    private function setupPsihologTimezone(): void
    {
        if ($this->psihologTimezone === null) {
            $psiholog = $this->currentPsihologRetriever->get();
            $psihologSettings = $this->psihologSettingsRepository->findByPsiholog($psiholog);
            $this->psihologTimezone = new \DateTimeZone($psihologSettings->getTimezone());
        }
    }
}
