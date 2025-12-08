<?php

declare(strict_types=1);

namespace App\Psiholog\Repository;

use App\Psiholog\Entity\SecurityPsiholog;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Security\Core\User\UserProviderInterface;

final class SecurityPsihologProviderRepository implements UserProviderInterface
{
    public function __construct(
        private PsihologRepository               $psihologRepository,
        private PsihologAutologinTokenRepository $autologinTokenRepository
    ) {
    }

    public function findByEmail(string $email): ?SecurityPsiholog
    {
        $user = $this->psihologRepository->findByEmail($email);

        if ($user) {
            return new SecurityPsiholog($user);
        }

        return null;
    }

    public function findByToken(string $token): ?SecurityPsiholog
    {
        $token = $this->autologinTokenRepository->findByToken($token);

        if (!$token) {
            return null;
        }

        return new SecurityPsiholog($token->getPsiholog());
    }

    public function loadUserByUsername($username): ?SecurityPsiholog
    {
        return $this->findByEmail($username);
    }

    public function refreshUser(UserInterface $user): ?UserInterface
    {
        return $this->findByEmail($user->getUsername());
    }

    public function supportsClass($class): bool
    {
        return $class === SecurityPsiholog::class;
    }
}
