<?php

namespace App\Landing\Service;

use App\Common\Service\TimezoneHelper;
use GeoIp2\Database\Reader;

class UserLocation
{
    private const DEFAULT_TIMEZONE = 'Europe/Kiev';

    public function __construct(
        private Reader $geoipReader,
    ) {
    }

    public function getTimezone(string $ip): string
    {
        try {
            $city = $this->geoipReader->city('92.253.238.91');
            return $city->location->timeZone;
        } catch (\Throwable $e) {
        }

        return TimezoneHelper::DEFAULT_TIMEZONE;
    }
}
