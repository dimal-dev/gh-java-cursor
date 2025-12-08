<?php

declare(strict_types=1);

namespace App\Staff\Service;

use App\Staff\Entity\User;
use App\Staff\Entity\UserAutologinToken;
use Doctrine\ORM\EntityManagerInterface;
use Ramsey\Uuid\Uuid;

final class UserAutologinTokenCreator
{
    private const ATTEMPTS_COUNT = 10;

    private EntityManagerInterface $em;

    public function __construct(
        EntityManagerInterface $em
    )
    {
        $this->em = $em;
    }

    public function createForUser(User $user): UserAutologinToken
    {
        $remainingAttempts = self::ATTEMPTS_COUNT;
        while ($remainingAttempts-- > 0) {
            try {
                return $this->tryCreateToken($user);
            } catch (\Exception $e) {
            }
        }

        throw new \RuntimeException("Failed to create a token");
    }

    private function tryCreateToken(User $user): UserAutologinToken
    {
        $token = Uuid::uuid4()->getHex();

        $userAutologinToken = new UserAutologinToken();
        $userAutologinToken->setToken($token);
        $userAutologinToken->setUser($user);
        $user->setUserAutologinToken($userAutologinToken);

        $this->em->persist($userAutologinToken);
        $this->em->flush();

        return $userAutologinToken;
    }
}
