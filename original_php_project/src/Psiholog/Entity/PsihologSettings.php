<?php

declare(strict_types=1);

namespace App\Psiholog\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'psiholog_settings')]
#[ORM\Entity]
class PsihologSettings
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'timezone', type: 'string')]
    private string $timezone;

    #[ORM\Column(name: 'telegram_chat_id', type: 'string', nullable: true)]
    private ?string $telegramChatId;

    #[ORM\Column(name: 'schedule_time_cap', type: 'string')]
    private string $scheduleTimeCap = '+3 hour';

    #[ORM\OneToOne(targetEntity: 'Psiholog', cascade: ['persist'])]
    private Psiholog $psiholog;

    public function getId(): int
    {
        return $this->id;
    }

    public function getTimezone(): string
    {
        return $this->timezone;
    }

    public function setTimezone(string $timezone): void
    {
        $this->timezone = $timezone;
    }

    public function getPsiholog(): Psiholog
    {
        return $this->psiholog;
    }

    public function setPsiholog(Psiholog $psiholog): void
    {
        $this->psiholog = $psiholog;
    }

    public function getTelegramChatId(): ?string
    {
        return $this->telegramChatId;
    }

    public function setTelegramChatId(?string $telegramChatId): void
    {
        $this->telegramChatId = $telegramChatId;
    }

    public function getScheduleTimeCap(): string
    {
        return $this->scheduleTimeCap;
    }
}
