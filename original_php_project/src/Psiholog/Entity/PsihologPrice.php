<?php

declare(strict_types=1);

namespace App\Psiholog\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'psiholog_price')]
#[ORM\Entity]
class PsihologPrice
{
    public const STATE_CURRENT = 1;
    public const STATE_PAST = 2;
    public const STATE_UNLISTED = 3;

    public const TYPE_INDIVIDUAL = 1;
    public const TYPE_COUPLE = 2;

    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'price', type: 'integer')]
    private int $price;

    #[ORM\Column(name: 'slug', type: 'string')]
    private ?string $slug = null;

    #[ORM\Column(name: 'currency', type: 'string')]
    private string $currency;

    #[ORM\Column(name: 'type', type: 'integer')]
    private int $type = self::TYPE_INDIVIDUAL;

    #[ORM\Column(name: 'state', type: 'integer')]
    private int $state = self::STATE_CURRENT;

    #[ORM\Column(name: 'pay_rate_percent', type: 'integer')]
    private int $payRatePercent;

    #[ORM\Column(name: 'psiholog_id', type: 'integer')]
    private int $psihologId;

    #[ORM\OneToOne(targetEntity: 'Psiholog', cascade: ['persist'])]
    private Psiholog $psiholog;

    public function getId(): int
    {
        return $this->id;
    }

    public function getPrice(): int
    {
        return $this->price;
    }

    public function getPriceInCents(): int
    {
        return $this->price * 100;
    }

    public function setPrice(int $price): void
    {
        $this->price = $price;
    }

    public function getCurrency(): string
    {
        return $this->currency;
    }

    public function setCurrency(string $currency): void
    {
        $this->currency = $currency;
    }

    public function getType(): int
    {
        return $this->type;
    }

    public function setType(int $type): void
    {
        $this->type = $type;
    }

    public function getState(): int
    {
        return $this->state;
    }

    public function setState(int $state): void
    {
        $this->state = $state;
    }

    public function getPayRatePercent(): int
    {
        return $this->payRatePercent;
    }

    public function setPayRatePercent(int $payRatePercent): void
    {
        $this->payRatePercent = $payRatePercent;
    }

    public function getPsiholog(): Psiholog
    {
        return $this->psiholog;
    }

    public function setPsiholog(Psiholog $psiholog): void
    {
        $this->psiholog = $psiholog;
    }

    public function isIndividual(): bool
    {
        return $this->type === self::TYPE_INDIVIDUAL;
    }

    public function isCouple(): bool
    {
        return $this->type === self::TYPE_COUPLE;
    }

    public function getSlug(): ?string
    {
        return $this->slug;
    }

    public function setSlug(?string $slug): void
    {
        $this->slug = $slug;
    }

    public function getPsihologId(): int
    {
        return $this->psihologId;
    }

    public function getTypeLabel(): string
    {
        if ($this->type === self::TYPE_INDIVIDUAL) {
            return 'Individual';
        }
        if ($this->type === self::TYPE_COUPLE) {
            return 'Couple';
        }

        throw new \UnexpectedValueException("Unknown type {$this->type}");
    }

    public function getTypeDuration(): int
    {
        if ($this->type === self::TYPE_INDIVIDUAL) {
            return 50;
        }
        if ($this->type === self::TYPE_COUPLE) {
            return 80;
        }

        throw new \UnexpectedValueException("Unknown type {$this->type}");
    }
}
