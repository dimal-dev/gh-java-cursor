<?php

declare(strict_types=1);

namespace App\Landing\Repository;

use App\Psiholog\Entity\Psiholog;
use App\User\Entity\UserRequestPsiholog;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\Persistence\ObjectRepository;

final class UserRequestPsihologRepository
{
    private ObjectRepository $repository;

    public function __construct(EntityManagerInterface $em)
    {
        $this->repository = $em->getRepository(UserRequestPsiholog::class);
    }

    public function findById(int $id): ?UserRequestPsiholog
    {
        return $this->repository->find($id);
    }
}
