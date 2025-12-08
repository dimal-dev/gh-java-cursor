<?php

namespace App\Psiholog\Controller;

use App\Common\Service\ArrayIndexer;
use App\Psiholog\Entity\PsihologSchedule;
use App\Psiholog\Repository\PsihologScheduleRepository;
use App\Psiholog\Repository\PsihologSettingsRepository;
use App\Psiholog\Service\CurrentPsihologRetriever;
use App\Psiholog\Service\TimeHelper;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/schedule-settings-week",
    name: "schedule_settings_week",
    methods: ["GET"]
)]
final class ScheduleSettingsWeekController extends AbstractController
{
    public const STATE_UNUSED = 1;
    public const STATE_AVAILABLE = 2;
    public const STATE_BOOKED = 3;
    public const STATE_DONE = 4;
    public const STATE_PASSED = 15;

    public function __construct(
        private PsihologScheduleRepository $psihologScheduleRepository,
        private CurrentPsihologRetriever $currentPsihologRetriever,
        private PsihologSettingsRepository $psihologSettingsRepository,
        private ArrayIndexer $arrayIndexer,
        private TimeHelper $timeHelper,
    ) {
    }

    private function convertToPsihologTimezone(array $array): array
    {
        foreach ($array as $key => $item) {
            $item['available_at_timestamp_utc'] = strtotime($item['available_at']);
            $item['available_at'] = $this->timeHelper->toPsihologTz($item['available_at']);
            $array[$key] = $item;
        }

        return $array;
    }

    private function getTimeList(int $mondayTimestamp): array
    {
        $psiholog = $this->currentPsihologRetriever->get();

        $mondayDateUtc = $this->timeHelper->toUtcTz(date('Y-m-d H:i:s', $mondayTimestamp));

        $bookedDates = $this->psihologScheduleRepository->findFromTime($psiholog->getId(), $mondayDateUtc);
        $bookedDates = $this->convertToPsihologTimezone($bookedDates);
        $bookedDatesIndexed = $this->arrayIndexer->byKeyUnique($bookedDates, 'available_at');
        unset($bookedDates);

        $nowDatetime = $this->timeHelper->toPsihologTz(date('Y-m-d H:i:s'));

        $timeConfig = [];
        foreach (range(0, 24 * 2 - 1) as $halfAnHour) {
            $timeRowTimes = [];
            foreach (range(0, 6) as $day) {
                $timeUnitTimestamp = $mondayTimestamp + 86400 * $day + $halfAnHour * 1800;
                $timeUnitDatetime = date('Y-m-d H:i:s', $timeUnitTimestamp);

                $timeUnitConfig = [
                    'state' => $timeUnitDatetime > $nowDatetime ? self::STATE_UNUSED : self::STATE_PASSED,
                    'timestamp' => $timeUnitTimestamp,
                    'time' => date('H:i', $timeUnitTimestamp),
                    'datetime' => $timeUnitDatetime,
                ];

                $bookedDate = $bookedDatesIndexed[$timeUnitDatetime] ?? null;
                if ($bookedDate) {
                    if ($bookedDate['available_at'] < $nowDatetime) {
                        $timeUnitConfig['state'] = self::STATE_PASSED;
                    } else if ($bookedDate['state'] == PsihologSchedule::STATE_AVAILABLE) {
                        $timeUnitConfig['state'] = self::STATE_AVAILABLE;
                    } else if ($bookedDate['state'] == PsihologSchedule::STATE_BOOKED) {
                        $timeUnitConfig['state'] = self::STATE_BOOKED;
                        $timeUnitConfig['available_at_timestamp_utc'] = $bookedDate['available_at_timestamp_utc'];
                    } else if ($bookedDate['state'] == PsihologSchedule::STATE_DONE) {
                        $timeUnitConfig['state'] = self::STATE_DONE;
                    }
                }

                $timeRowTimes[] = $timeUnitConfig;
            }
            $timeConfig[] = $timeRowTimes;
        }

        return $timeConfig;
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        $weekDayFirst = $request->query->get('weekDayFirst');

        $monday = strtotime($weekDayFirst);

        $content['timeList'] = $this->getTimeList($monday);
        $content['dayList'] = $this->getDayList($monday);

        return $this->render('@psiholog/pages/schedule-settings-week.html.twig', $content);
    }

    private function getDayList(int $mondayTimestamp): array
    {
        $dayList = [];
        foreach (range(0, 6) as $day) {
            $dayList[] = $mondayTimestamp + 86400 * $day;
        }

        return $dayList;
    }
}