<?php

declare(strict_types=1);

namespace App\User\Entity;

use Symfony\Component\Security\Core\User\UserInterface;

final class SecurityUser implements UserInterface
{
    private const ROLE_USER = 'ROLE_USER';

    private User $user;

    public function __construct(User $user)
    {
        $this->user = $user;
    }

    public function getRoles()
    {
        return [self::ROLE_USER];
    }

    public function getPassword()
    {
        return '';
    }

    public function getSalt()
    {
        return null;
    }

    public function getUsername(): string
    {
        return $this->user->getEmail();
    }
    public function getId(): int
    {
        return $this->user->getId();
    }

    public function eraseCredentials(): bool
    {
        return false;
    }

    public function getUser(): User
    {
        return $this->user;
    }
}
