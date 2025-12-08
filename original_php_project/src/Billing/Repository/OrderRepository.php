<?php

namespace App\Billing\Repository;

use App\Billing\Entity\Order;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;

class OrderRepository
{
    private EntityRepository $repository;

    public function __construct(
        private EntityManagerInterface $em
    ) {
        $this->repository = $em->getRepository(Order::class);
    }

    public function findByCheckoutSlug(string $checkoutSlug): ?Order
    {
        return $this->repository->findOneBy([
            'checkoutSlug' => $checkoutSlug
        ]);
    }

    public function findUserLatestBoughtPsihologId(int $userId): ?int
    {
        $qb = $this->em->getConnection()->createQueryBuilder();
        $qb->select('pp.psiholog_id');
        $qb->from('billing_order', 'bo');
        $qb->innerJoin('bo', 'psiholog_price', 'pp', 'bo.psiholog_price_id = pp.id');

        $qb->andWhere('bo.user_id = :userId');
        $qb->setParameter('userId', $userId);

        $qb->andWhere('bo.state = :state');
        $qb->setParameter('state', Order::STATE_APPROVED);

        $qb->orderBy('bo.id', 'DESC');
        $qb->setMaxResults(1);

        $foundRow = $qb->execute()->fetchOne();

        return empty($foundRow) ? null : (int) $foundRow;
    }
}