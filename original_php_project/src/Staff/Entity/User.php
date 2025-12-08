<?php

declare(strict_types=1);

namespace App\Staff\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'staff_user')]
#[ORM\Entity]
class User
{
    public const ROLE_USER = 1;
    public const ROLE_SUPERUSER = 100;
    public const ROLE_LIST = [
        self::ROLE_USER,
        self::ROLE_SUPERUSER,
    ];
    public const STATE_ACTIVE = 1;
    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;
    #[ORM\Column(name: 'email', type: 'string', length: 255)]
    private string $email;
    #[ORM\Column(name: 'role', type: 'integer')]
    private int $role;
    #[ORM\Column(name: 'state', type: 'integer')]
    private int $state = self::STATE_ACTIVE;
    #[ORM\OneToOne(targetEntity: 'UserAutologinToken', mappedBy: 'user', cascade: ['persist'])]
    private UserAutologinToken $userAutologinToken;
    public function getId(): int
    {
        return $this->id;
    }
    public function setEmail(string $email): void
    {
        $this->email = $email;
    }
    public function setRole(int $role): void
    {
        $this->role = $role;
    }
    public function setUserAutologinToken(UserAutologinToken $userAutologinToken): void
    {
        $this->userAutologinToken = $userAutologinToken;
    }
    public function getUserAutologinToken(): UserAutologinToken
    {
        return $this->userAutologinToken;
    }
    public function getEmail(): string
    {
        return $this->email;
    }
    public function getRole(): int
    {
        return $this->role;
    }
    public function getState(): int
    {
        return $this->state;
    }
    public function setState(int $state): void
    {
        $this->state = $state;
    }
}
