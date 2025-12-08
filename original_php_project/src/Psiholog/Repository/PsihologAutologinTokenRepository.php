<?php

declare(strict_types=1);

namespace App\Psiholog\Repository;

use App\Psiholog\Entity\PsihologAutologinToken;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\Persistence\ObjectRepository;

final class PsihologAutologinTokenRepository
{
    private ObjectRepository $repository;

    public function __construct(EntityManagerInterface $em)
    {
        $this->repository = $em->getRepository(PsihologAutologinToken::class);
    }

    public function findByToken(string $token): ?PsihologAutologinToken
    {
        return $this->repository->findOneBy(['token' => $token]);
    }
}
