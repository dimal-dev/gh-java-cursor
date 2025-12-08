<?php

declare(strict_types=1);

namespace App\Billing\Entity;

use App\User\Entity\User;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'billing_user_wallet')]
#[ORM\Entity]
class UserWallet
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'user_id', type: 'integer')]
    private int $userId;

    #[ORM\OneToOne(targetEntity: '\App\User\Entity\User')]
    #[ORM\JoinColumn(name: 'user_id', referencedColumnName: 'id')]
    private User $user;

    #[ORM\Column(name: 'balance', type: 'integer')]
    private int $balance = 0;

    #[ORM\Column(name: 'currency', type: 'string')]
    private string $currency;

    public function getId(): int
    {
        return $this->id;
    }
    public function getUserId(): int
    {
        return $this->userId;
    }
    public function setUserId(int $userId): void
    {
        $this->userId = $userId;
    }
    public function getUser(): User
    {
        return $this->user;
    }
    public function setUser(User $user): void
    {
        $this->user = $user;
    }
    public function getBalance(): int
    {
        return $this->balance;
    }
    public function setBalance(int $balance): void
    {
        $this->balance = $balance;
    }
    public function getCurrency(): string
    {
        return $this->currency;
    }
    public function setCurrency(string $currency): void
    {
        $this->currency = $currency;
    }
}
