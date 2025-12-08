<?php

declare(strict_types=1);

namespace App\Psiholog\Service;

use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologAutologinToken;
use Doctrine\ORM\EntityManagerInterface;
use Ramsey\Uuid\Uuid;

final class PsihologAutologinTokenCreator
{
    private const ATTEMPTS_COUNT = 10;

    public function __construct(
        private EntityManagerInterface $em
    )
    {
    }

    public function createForPsiholog(Psiholog $psiholog): PsihologAutologinToken
    {
        $remainingAttempts = self::ATTEMPTS_COUNT;
        while ($remainingAttempts-- > 0) {
            try {
                return $this->tryCreateToken($psiholog);
            } catch (\Exception $e) {
            }
        }

        throw new \RuntimeException("Failed to create a token");
    }

    private function tryCreateToken(Psiholog $user): PsihologAutologinToken
    {
        $token = Uuid::uuid4()->getHex();

        $userAutologinToken = new PsihologAutologinToken();
        $userAutologinToken->setToken($token);
        $userAutologinToken->setPsiholog($user);
        $user->setPsihologAutologinToken($userAutologinToken);

        $this->em->persist($userAutologinToken);
        $this->em->flush();

        return $userAutologinToken;
    }
}
