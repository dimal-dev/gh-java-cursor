<?php

declare(strict_types=1);

namespace App\User\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'promocode')]
#[ORM\Entity]
class Promocode
{
    public const STATE_ACTIVE = 1;
    public const STATE_INACTIVE = 2;

    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'name', type: 'string', length: 500)]
    private string $name;

    #[ORM\Column(name: 'state', type: 'integer', length: 255)]
    private int $state = self::STATE_ACTIVE;

    #[ORM\Column(name: 'discount_percent', type: 'integer', length: 255)]
    private int $discountPercent;

    #[ORM\Column(name: 'max_use_number', type: 'integer', length: 255)]
    private ?int $maxUseNumber = 1;

    #[ORM\Column(name: 'expire_at', type: 'datetime')]
    private ?\DateTime $expireAt;

    public function getId(): int
    {
        return $this->id;
    }

    public function getName(): string
    {
        return $this->name;
    }

    public function setName(string $name): void
    {
        $this->name = $name;
    }

    public function getState(): int
    {
        return $this->state;
    }

    public function setState(int $state): void
    {
        $this->state = $state;
    }

    public function getDiscountPercent(): int
    {
        return $this->discountPercent;
    }

    public function setDiscountPercent(int $discountPercent): void
    {
        $this->discountPercent = $discountPercent;
    }

    public function getMaxUseNumber(): ?int
    {
        return $this->maxUseNumber;
    }

    public function setMaxUseNumber(?int $maxUseNumber): void
    {
        $this->maxUseNumber = $maxUseNumber;
    }

    public function getExpireAt(): ?\DateTime
    {
        return $this->expireAt;
    }

    public function setExpireAt(?\DateTime $expireAt): void
    {
        $this->expireAt = $expireAt;
    }
}
