<?php

declare(strict_types=1);

namespace App\User\Entity;

use App\Psiholog\Entity\Psiholog;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'user_promocode')]
#[ORM\Entity]
class UserPromocode
{
    public const STATE_APPLIED = 1;
    public const STATE_USED = 2;

    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'promocode_id', type: 'integer', nullable: true)]
    private int $promocodeId;

    #[ORM\Column(name: 'user_id', type: 'integer', nullable: true)]
    private ?int $userId;

    #[ORM\Column(name: 'email', type: 'string', length: 500)]
    private string $email;

    #[ORM\Column(name: 'state', type: 'integer', length: 255)]
    private int $state = self::STATE_APPLIED;

    #[ORM\Column(name: 'applied_at', type: 'datetime')]
    private ?\DateTime $appliedAt;

    #[ORM\Column(name: 'used_at', type: 'datetime')]
    private ?\DateTime $usedAt;

    #[ORM\OneToOne(targetEntity: 'App\User\Entity\Promocode')]
    #[ORM\JoinColumn(name: 'promocode_id', referencedColumnName: 'id')]
    private Promocode $promocode;

    public function getId(): int
    {
        return $this->id;
    }

    public function getPromocodeId(): int
    {
        return $this->promocodeId;
    }

    public function setPromocodeId(int $promocodeId): void
    {
        $this->promocodeId = $promocodeId;
    }

    public function getUserId(): ?int
    {
        return $this->userId;
    }

    public function setUserId(?int $userId): void
    {
        $this->userId = $userId;
    }

    public function getEmail(): string
    {
        return $this->email;
    }

    public function setEmail(string $email): void
    {
        $this->email = $email;
    }

    public function getState(): int
    {
        return $this->state;
    }

    public function setState(int $state): void
    {
        $this->state = $state;
    }

    public function getAppliedAt(): ?\DateTime
    {
        return $this->appliedAt;
    }

    public function setAppliedAt(?\DateTime $appliedAt): void
    {
        $this->appliedAt = $appliedAt;
    }

    public function getUsedAt(): ?\DateTime
    {
        return $this->usedAt;
    }

    public function setUsedAt(?\DateTime $usedAt): void
    {
        $this->usedAt = $usedAt;
    }

    public function getPromocode(): Promocode
    {
        return $this->promocode;
    }

    public function setPromocode(Promocode $promocode): void
    {
        $this->promocode = $promocode;
    }

    public function isUsed(): bool
    {
        return $this->state === self::STATE_USED;
    }
}
