<?php

namespace App\Billing\Repository;

use App\Billing\Entity\Checkout;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;

class CheckoutRepository
{
    private EntityRepository $repository;

    public function __construct(
        private EntityManagerInterface $em
    ) {
        $this->repository = $em->getRepository(Checkout::class);
    }

    public function findBySlug(string $slug): ?Checkout
    {
        return $this->repository->findOneBy([
            'slug' => $slug
        ]);
    }

    public function findById(string $id): ?Checkout
    {
        return $this->repository->findOneBy([
            'id' => $id
        ]);
    }
}