<?php

namespace App\Psiholog\Controller;

use App\Common\Service\Language;
use App\Common\Service\DateLocalizedHelper;
use DateTime;
use DateTimeZone;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/schedule-settings",
    name: "schedule_settings",
    methods: ["GET"]
)]
final class ScheduleSettingsController extends AbstractController
{

    public function __construct(
        private DateLocalizedHelper $dateLocalizedHelper,
    ) {
    }

    private function formatWeekDate(int $timestamp): string
    {
        $day = date('d', $timestamp);
        $monthLabel = $this->dateLocalizedHelper->getMonthNameByNumberInclined(date('n', $timestamp), Language::RU);

        return "{$day} {$monthLabel}";
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        $monday = strtotime('monday this week');
        $sunday = strtotime('sunday this week');

        $content['zeroWeek'] = [
            "monday" => $monday,
            "sunday" => $sunday,
            "startDate" => $this->formatWeekDate($monday),
            "endDate" => $this->formatWeekDate($sunday),
            "startDateFull" => date('Y-m-d H:i:s', $monday),
            "endDateFull" => date('Y-m-d H:i:s', $monday),
        ];

        $weekInSeconds = 7 * 86400;

        $content['oneWeek'] = [
            "monday" => $monday + 1 * $weekInSeconds,
            "sunday" => $sunday + 1 * $weekInSeconds,
            "startDate" => $this->formatWeekDate($monday + 1 * $weekInSeconds),
            "endDate" => $this->formatWeekDate($sunday + 1 * $weekInSeconds),
            "startDateFull" => date('Y-m-d H:i:s', $monday + 1 * $weekInSeconds),
            "endDateFull" => date('Y-m-d H:i:s', $monday + 1 * $weekInSeconds),
        ];

        $content['twoWeek'] = [
            "monday" => $monday + 2 * $weekInSeconds,
            "sunday" => $sunday + 2 * $weekInSeconds,
            "startDate" => $this->formatWeekDate($monday + 2 * $weekInSeconds),
            "endDate" => $this->formatWeekDate($sunday + 2 * $weekInSeconds),
            "startDateFull" => date('Y-m-d H:i:s', $monday + 2 * $weekInSeconds),
            "endDateFull" => date('Y-m-d H:i:s', $monday + 2 * $weekInSeconds),
        ];

        $content['threeWeek'] = [
            "monday" => $monday + 3 * $weekInSeconds,
            "sunday" => $sunday + 3 * $weekInSeconds,
            "startDate" => $this->formatWeekDate($monday + 3 * $weekInSeconds),
            "endDate" => $this->formatWeekDate($sunday + 3 * $weekInSeconds),
            "startDateFull" => date('Y-m-d H:i:s', $monday + 3 * $weekInSeconds),
            "endDateFull" => date('Y-m-d H:i:s', $monday + 3 * $weekInSeconds),
        ];

        return $this->render('@psiholog/pages/schedule-settings.html.twig', $content);
    }
}