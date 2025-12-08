<?php

namespace App\Common\Service;

class ConsultationAllowedCancelTimeChecker
{
    public const MINIMUM_DELTA = 1 * 24 * 60 * 60;

    public function isAllowed(\DateTime $consultationTime): bool
    {
        $nowTs = time();
        $consultationTs = $consultationTime->getTimestamp();

        return ($consultationTs - $nowTs) >= self::MINIMUM_DELTA;
    }
}
