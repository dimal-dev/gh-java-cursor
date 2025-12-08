<?php

namespace App\Psiholog\Service;

use App\Common\Service\DateLocalizedHelper;
use App\Common\Service\TimezoneHelper;
use App\Notification\Service\TelegramSender;
use App\Psiholog\Entity\PsihologSchedule;
use App\Psiholog\Repository\PsihologProfileRepository;
use App\Psiholog\Repository\PsihologSettingsRepository;
use App\Psiholog\Repository\PsihologUserNotesRepository;
use App\User\Entity\User;
use App\User\Entity\UserRequestPsiholog;
use Symfony\Contracts\Translation\TranslatorInterface;

class TelegramNotifier
{
    private const CONSULTATION_TYPE_LIST = [
        'individual' => 'Личка',
        'couple' => 'Парная',
        'teenager' => 'Для подростка',
    ];

    private const ADMINS_TELEGRAM_CHAT_ID = '5299193501';

    public function __construct(
        private TelegramSender $telegramSender,
        private TimezoneHelper $timezoneHelper,
        private PsihologSettingsRepository $psihologSettingsRepository,
        private DateLocalizedHelper $dateLocalizedHelper,
        private PsihologUserNotesRepository $psihologUserNotesRepository,
        private PsihologProfileRepository $psihologProfileRepository,
        private AutologinableLinksCreator $autologinableLinksCreator,
        private string $adminTelegramNotificationsEnabled,
        private TranslatorInterface $translator,
    ) {
    }

    public function notifyConsultationBooked(User $user, PsihologSchedule $psihologSchedule): bool
    {
        $psiholog = $psihologSchedule->getPsiholog();

        $psihologSettings = $this->psihologSettingsRepository->findByPsiholog($psiholog);
        if (!$psihologSettings->getTelegramChatId()) {
            return false;
        }
        $psihologTimezone = new \DateTimeZone($psihologSettings->getTimezone());

        $startsAt = $this->timezoneHelper->toTzDateTimeFromDatetime(
            $psihologSchedule->getAvailableAt(),
            $psihologTimezone
        );

        $chatLink = $this->autologinableLinksCreator->create($psiholog, RouteNames::CHAT, [
            'userId' => $user->getId(),
        ]);

        $notes = $this->psihologUserNotesRepository->findByPsihologAndUserId($psiholog, $user->getId());

        $userName = $user->getFullName();
        $psihologUserLabel = $notes?->getName();
        if (!empty($psihologUserLabel)) {
            if (!empty($userName)) {
                $userName .= ' / ';
            }

            $userName .= $psihologUserLabel;
        }

        $msg = "
К вам записались ({$userName}) на консультацию: {$this->dateLocalizedHelper->getDateTimeGoodLookingLabel($startsAt)}
<a href=\"{$chatLink}\">Открыть чат с этим пользователем</a>
";
        return $this->telegramSender->send($psihologSettings->getTelegramChatId(), $msg);
    }

    public function notifyAdminsConsultationBooked(User $user, PsihologSchedule $psihologSchedule): bool
    {
        if (!$this->adminTelegramNotificationsEnabled) {
            return true;
        }

        $psihologProfile = $this->psihologProfileRepository->findByPsiholog($psihologSchedule->getPsiholog());
        $startsAt = $psihologSchedule->getAvailableAt();

        $msg = "
К психологу {$psihologProfile->getFullName()} записались на консультацию: {$this->dateLocalizedHelper->getDateTimeGoodLookingLabel($startsAt)}
";
        return $this->telegramSender->send(self::ADMINS_TELEGRAM_CHAT_ID, $msg);
    }

    public function notifyConsultationCancelledByUser(PsihologSchedule $psihologSchedule, bool $inTime): bool
    {
        if (!$this->adminTelegramNotificationsEnabled) {
            return true;
        }

        $psihologProfile = $this->psihologProfileRepository->findByPsiholog($psihologSchedule->getPsiholog());

        $startsAt = $psihologSchedule->getAvailableAt();

        $msg = "
У психолога {$psihologProfile->getFullName()} пользователь отменил консультацию: {$this->dateLocalizedHelper->getDateTimeGoodLookingLabel($startsAt)}
";
        if (!$inTime) {
            $msg .= " !НЕ ВОВРЕМЯ! У юзера деньги сняли";
        }
        return $this->telegramSender->send(self::ADMINS_TELEGRAM_CHAT_ID, $msg);
    }

    public function notifyConsultationCancelledByPsiholog(PsihologSchedule $psihologSchedule, bool $inTime): bool
    {
        if (!$this->adminTelegramNotificationsEnabled) {
            return true;
        }

        $psihologProfile = $this->psihologProfileRepository->findByPsiholog($psihologSchedule->getPsiholog());

        $startsAt = $psihologSchedule->getAvailableAt();

        $msg = "";
        if (!$inTime) {
            $msg .= "Внимание! Со штрафом. За меньше чем 24 часа. ";
        }

        $msg .= "
Психолог {$psihologProfile->getFullName()} отменил консультацию: {$this->dateLocalizedHelper->getDateTimeGoodLookingLabel($startsAt)}
";
        return $this->telegramSender->send(self::ADMINS_TELEGRAM_CHAT_ID, $msg);
    }

    public function notifyConsultationCancelled(PsihologSchedule $psihologSchedule): bool
    {
        $psiholog = $psihologSchedule->getPsiholog();

        $psihologSettings = $this->psihologSettingsRepository->findByPsiholog($psiholog);
        if (!$psihologSettings->getTelegramChatId()) {
            return false;
        }
        $psihologTimezone = new \DateTimeZone($psihologSettings->getTimezone());

        $startsAt = $this->timezoneHelper->toTzDateTimeFromDatetime(
            $psihologSchedule->getAvailableAt(),
            $psihologTimezone
        );

        $msg = "
У вас отменили консультацию: {$this->dateLocalizedHelper->getDateTimeGoodLookingLabel($startsAt)}
";
        return $this->telegramSender->send($psihologSettings->getTelegramChatId(), $msg);
    }



    public function notifyAdminsPsihologRequested(UserRequestPsiholog $userRequestPsiholog, ?bool $duplicated = false): bool
    {
        if (!$this->adminTelegramNotificationsEnabled) {
            return true;
        }

        $typeLabel = self::CONSULTATION_TYPE_LIST[$userRequestPsiholog->getConsultationType()] ?? "Неизвестный тип ({$userRequestPsiholog->getConsultationType()})";

        $sex = ['female' => 'Женщина', 'male' => 'Мужчина', 'both' => 'Женщина или мужчина'][$userRequestPsiholog->getSex()];

        $lgbtq = $userRequestPsiholog->getLgbtq() ? 'Да' : 'Нет';

        $psihologProfile = null;
        if ($userRequestPsiholog->getPsihologId()) {
            $psihologProfile = $this->psihologProfileRepository->findByPsihologId($userRequestPsiholog->getPsihologId());
        }

        $problemsList = explode(', ', $userRequestPsiholog->getProblem());
        $problemsListTranslated = array_map(fn($el) => $this->translator->trans($el), $problemsList);
        $problemsTranslated = implode(', ', $problemsListTranslated);

        $subsequentRequest = "";
        if ($duplicated) {
            $subsequentRequest = "
            (ВЖЕ АНКЕТУ ВІДПРАВЛЯВ ЦЕЙ КЛІЄНТ ДО ЦЬОГО ПСИХОЛОГА РАНІШЕ)
            ";
        }

        $promocode = '';
        if (!empty($userRequestPsiholog->getPromocode())) {
            $promocode = "
            
            ПРОМОКОД ПРИМЕНИЛ!
            {$userRequestPsiholog->getPromocode()}
            
            ";
        }

        if ($psihologProfile) {
            $msg =
                "
{$subsequentRequest}

((( К КОНКРЕТНОМУ ПСИХОЛОГУ )))
{$psihologProfile->getFullName()}

Телефон 
{$userRequestPsiholog->getPhone()}

Email 
{$userRequestPsiholog->getEmail()}

Имя
{$userRequestPsiholog->getName()}

Как лучше связаться
{$userRequestPsiholog->getChannel()}

Тип консультации
{$typeLabel}

Стоимость консультации
{$userRequestPsiholog->getPrice()} ₴
{$promocode}
Проблема к психологу
{$problemsTranslated}
";
        } else {
            $msg =
                "
!!! НУЖНО ПОДОБРАТЬ ПСИХОЛОГА !!!
Желаемый пол психолога
{$sex}

Телефон 
{$userRequestPsiholog->getPhone()}

Email 
{$userRequestPsiholog->getEmail()}

Имя
{$userRequestPsiholog->getName()}

Как лучше связаться
{$userRequestPsiholog->getChannel()}

Тип консультации
{$typeLabel}

Опыт с LGBTQ+
{$lgbtq}

Стоимость консультации
{$userRequestPsiholog->getPrice()} ₴
{$promocode}
Проблема к психологу
{$problemsTranslated}
";
        }

        return $this->telegramSender->send(self::ADMINS_TELEGRAM_CHAT_ID, $msg);
    }
}
