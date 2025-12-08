<?php

declare(strict_types=1);

namespace App\Staff\Repository;

use App\Staff\Entity\SecurityUser;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Security\Core\User\UserProviderInterface;

final class SecurityUserProviderRepository implements UserProviderInterface
{
    private UserRepository $userRepository;
    private UserAutologinTokenRepository $autologinTokenRepository;

    public function __construct(
        UserRepository $userRepository,
        UserAutologinTokenRepository $autologinTokenRepository
    ) {
        $this->userRepository = $userRepository;
        $this->autologinTokenRepository = $autologinTokenRepository;
    }

    public function findByEmail(string $email): ?SecurityUser
    {
        $user = $this->userRepository->findByEmail($email);

        if ($user) {
            return new SecurityUser($user);
        }

        return null;
    }

    public function findByToken(string $token): ?SecurityUser
    {
        $token = $this->autologinTokenRepository->findByToken($token);

        if (!$token) {
            return null;
        }

        return new SecurityUser($token->getUser());
    }

    public function loadUserByUsername($username): ?SecurityUser
    {
        return $this->findByEmail($username);
    }

    public function refreshUser(UserInterface $user): ?UserInterface
    {
        return $this->findByEmail($user->getUsername());
    }

    public function supportsClass($class): bool
    {
        return $class === SecurityUser::class;
    }
}
