<?php

declare(strict_types=1);

namespace App\User\Repository;

use App\User\Entity\UserAutologinToken;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\Persistence\ObjectRepository;

final class UserAutologinTokenRepository
{
    private ObjectRepository $repository;

    public function __construct(EntityManagerInterface $em)
    {
        $this->repository = $em->getRepository(UserAutologinToken::class);
    }

    public function findByToken(string $token): ?UserAutologinToken
    {
        return $this->repository->findOneBy(['token' => $token]);
    }
}
