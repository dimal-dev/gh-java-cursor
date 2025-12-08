<?php

declare(strict_types=1);

namespace App\Billing\Entity;

use App\User\Entity\User;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'billing_user_wallet_operation')]
#[ORM\Entity]
class UserWalletOperation
{
    public const TYPE_ADD = 1;
    public const TYPE_SUBTRACT = 2;
    public const REASON_PURCHASE = 1;
    public const REASON_CANCELLED_CONSULTATION = 2;
    public const REASON_CREATED_CONSULTATION = 3;
    public const REASON_CANCELLED_CONSULTATION_NOT_IN_TIME_BY_PSIHOLOG = 4;

    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\ManyToOne(targetEntity: 'UserWallet')]
    #[ORM\JoinColumn(name: 'buw_id', referencedColumnName: 'id')]
    private UserWallet $userWallet;

    #[ORM\Column(name: 'amount', type: 'integer')]
    private int $amount;

    #[ORM\Column(name: 'currency', type: 'string')]
    private string $currency;

    #[ORM\Column(name: 'type', type: 'integer')]
    private int $type;

    #[ORM\Column(name: 'reason_type', type: 'integer')]
    private int $reasonType;

    #[ORM\Column(name: 'reason_id', type: 'integer')]
    private int $reasonId;

    public function getId(): int
    {
        return $this->id;
    }
    public function getUserWallet(): UserWallet
    {
        return $this->userWallet;
    }
    public function setUserWallet(UserWallet $userWallet): void
    {
        $this->userWallet = $userWallet;
    }
    public function getAmount(): int
    {
        return $this->amount;
    }
    public function setAmount(int $amount): void
    {
        $this->amount = $amount;
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
    public function getReasonType(): int
    {
        return $this->reasonType;
    }
    public function setReasonType(int $reasonType): void
    {
        $this->reasonType = $reasonType;
    }
    public function getReasonId(): int
    {
        return $this->reasonId;
    }
    public function setReasonId(int $reasonId): void
    {
        $this->reasonId = $reasonId;
    }
    public function isAdd(): bool
    {
        return $this->type === self::TYPE_ADD;
    }
    public function isSubtract(): bool
    {
        return $this->type === self::TYPE_SUBTRACT;
    }
}
