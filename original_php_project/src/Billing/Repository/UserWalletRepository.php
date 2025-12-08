<?php

namespace App\Billing\Repository;

use App\Billing\Entity\Order;
use App\Billing\Entity\UserWallet;
use App\User\Entity\User;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;

class UserWalletRepository
{
    private EntityRepository $repository;

    public function __construct(
        EntityManagerInterface $em
    ) {
        $this->repository = $em->getRepository(UserWallet::class);
    }

    public function findByUser(User $user): ?UserWallet
    {
        return $this->repository->findOneBy([
            'user' => $user
        ]);
    }
}