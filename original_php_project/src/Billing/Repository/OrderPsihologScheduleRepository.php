<?php

namespace App\Billing\Repository;

use App\Billing\Entity\Order;
use App\Billing\Entity\OrderPsihologSchedule;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;

class OrderPsihologScheduleRepository
{
    private EntityRepository $repository;

    public function __construct(
        EntityManagerInterface $em
    ) {
        $this->repository = $em->getRepository(OrderPsihologSchedule::class);
    }

    public function findByOrder(Order $order): ?OrderPsihologSchedule
    {
        return $this->repository->findOneBy([
            'order' => $order
        ]);
    }
}