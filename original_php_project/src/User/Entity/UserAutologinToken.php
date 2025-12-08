<?php

declare(strict_types=1);

namespace App\User\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'user_autologin_token')]
#[ORM\Entity]
class UserAutologinToken
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;
    #[ORM\Column(name: 'token', type: 'string', length: 32, nullable: false)]
    private string $token;
    #[ORM\OneToOne(targetEntity: 'User', inversedBy: 'userAutologinToken')]
    #[ORM\JoinColumn(name: 'user_id', referencedColumnName: 'id')]
    private User $user;
    public function getId(): int
    {
        return $this->id;
    }
    public function getToken(): string
    {
        return $this->token;
    }
    public function setToken(string $token): void
    {
        $this->token = $token;
    }
    public function getUser(): User
    {
        return $this->user;
    }
    public function setUser(User $user): void
    {
        $this->user = $user;
    }
}
