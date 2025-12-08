<?php

declare(strict_types=1);

namespace App\Staff\Entity;

use Symfony\Component\Security\Core\User\UserInterface;

final class SecurityUser implements UserInterface
{
    private const ROLE_STAFF_USER = 'ROLE_STAFF_USER';
    private const ROLE_STAFF_SUPERUSER = 'ROLE_STAFF_SUPERUSER';

    private const USER_ROLE_TO_SECURITY_ROLE = [
        User::ROLE_USER => self::ROLE_STAFF_USER,
        User::ROLE_SUPERUSER => self::ROLE_STAFF_SUPERUSER,
    ];

    private User $user;

    public function __construct(User $user)
    {
        $this->user = $user;
    }

    public function getRoles()
    {
        $roles = [];
        if (isset(self::USER_ROLE_TO_SECURITY_ROLE[$this->user->getRole()])) {
            $roles[] = self::USER_ROLE_TO_SECURITY_ROLE[$this->user->getRole()];
        }

        return $roles;
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

    public function getUserIdentifier(): string
    {
        return $this->getUsername();
    }

    public function eraseCredentials(): bool
    {
        return false;
    }
}
