<?php

declare(strict_types=1);

namespace App\Psiholog\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'psiholog_schedule')]
#[ORM\Entity]
class PsihologSchedule
{
    public const STATE_AVAILABLE = 1;
    public const STATE_BOOKED = 2;
    public const STATE_UNAVAILABLE = 3;
    public const STATE_DONE = 4;
    public const STATE_FAILED = 5;
    public const STATE_EXPIRED = 6;

    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'state', type: 'integer')]
    private int $state = self::STATE_AVAILABLE;

    #[ORM\Column(name: 'psiholog_id', type: 'integer')]
    private int $psihologId;

    #[ORM\Column(name: 'available_at', type: 'datetime')]
    private \DateTime $availableAt;

    #[ORM\OneToOne(targetEntity: 'Psiholog', cascade: ['persist'])]
    private Psiholog $psiholog;

    public function getId(): int
    {
        return $this->id;
    }

    public function getAvailableAt(): \DateTime
    {
        return $this->availableAt;
    }

    public function setAvailableAt(\DateTime $availableAt): void
    {
        $this->availableAt = $availableAt;
    }

    public function getPsiholog(): Psiholog
    {
        return $this->psiholog;
    }

    public function setPsiholog(Psiholog $psiholog): void
    {
        $this->psiholog = $psiholog;
    }

    public function getState(): int
    {
        return $this->state;
    }

    public function setState(int $state): void
    {
        $this->state = $state;
    }

    public function isAvailable(): bool
    {
        return $this->state === self::STATE_AVAILABLE;
    }

    public function isUnAvailable(): bool
    {
        return $this->state === self::STATE_UNAVAILABLE;
    }

    public function isBooked(): bool
    {
        return $this->state === self::STATE_BOOKED;
    }

    public function getPsihologId(): int
    {
        return $this->psihologId;
    }
}
