<?php

declare(strict_types=1);

namespace App\Psiholog\Repository;

use App\Psiholog\Entity\Psiholog;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\Persistence\ObjectRepository;

final class PsihologRepository
{
    private ObjectRepository $repository;

    public function __construct(EntityManagerInterface $em)
    {
        $this->repository = $em->getRepository(Psiholog::class);
    }

    public function findById(int $id): ?Psiholog
    {
        return $this->repository->find($id);
    }

    public function findByEmail(string $email): ?Psiholog
    {
        return $this->repository->findOneBy(['email' => $email]);
    }
}
