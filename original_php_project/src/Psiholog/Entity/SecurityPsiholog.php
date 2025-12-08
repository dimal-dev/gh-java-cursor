<?php

declare(strict_types=1);

namespace App\Psiholog\Entity;

use Symfony\Component\Security\Core\User\UserInterface;

final class SecurityPsiholog implements UserInterface
{
    private const ROLE_PSIHOLOG = 'ROLE_PSIHOLOG';
    private const ROLE_TEST_PSIHOLOG = 'ROLE_TEST_PSIHOLOG';

    private const THERAPIST_ROLE_TO_SECURITY_ROLE = [
        Psiholog::ROLE_PSIHOLOG => self::ROLE_PSIHOLOG,
        Psiholog::ROLE_TEST_PSIHOLOG => self::ROLE_TEST_PSIHOLOG,
    ];

    public function __construct(private Psiholog $psiholog)
    {
    }

    public function getRoles()
    {
        $roles = [];
        if (isset(self::THERAPIST_ROLE_TO_SECURITY_ROLE[$this->psiholog->getRole()])) {
            $roles[] = self::THERAPIST_ROLE_TO_SECURITY_ROLE[$this->psiholog->getRole()];
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
        return $this->psiholog->getEmail();
    }

    public function getId(): int
    {
        return $this->psiholog->getId();
    }

    public function getUserIdentifier(): string
    {
        return $this->getUsername();
    }

    public function eraseCredentials(): bool
    {
        return false;
    }

    public function getPsiholog(): Psiholog
    {
        return $this->psiholog;
    }
}
