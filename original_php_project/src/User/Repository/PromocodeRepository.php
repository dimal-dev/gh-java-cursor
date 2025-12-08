<?php

declare(strict_types=1);

namespace App\User\Repository;

use App\User\Entity\Promocode;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\Persistence\ObjectRepository;

final class PromocodeRepository
{
    private ObjectRepository $repository;

    public function __construct(EntityManagerInterface $em)
    {
        $this->repository = $em->getRepository(Promocode::class);
    }

    public function findByName(string $name): ?Promocode
    {
        return $this->repository->findOneBy(['name' => $name]);
    }

    public function findById(int $id): ?Promocode
    {
        return $this->repository->find($id);
    }
}
