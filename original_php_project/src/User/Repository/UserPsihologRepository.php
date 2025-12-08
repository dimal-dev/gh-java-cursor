<?php

namespace App\User\Repository;

use Doctrine\ORM\EntityManagerInterface;

class UserPsihologRepository
{
    public function __construct(
        private EntityManagerInterface $em
    ) {
    }

    public function isUserBelongsToPsiholog(int $userId, int $psihologId): bool
    {
        $qb = $this->em->getConnection()->createQueryBuilder();
        $qb->select('1');
        $qb->from('user_psiholog');

        $qb->andWhere('user_id = :userId');
        $qb->setParameter('userId', $userId);

        $qb->andWhere('psiholog_id = :psihologId');
        $qb->setParameter('psihologId', $psihologId);

        $qb->setMaxResults(1);

        return (bool) $qb->execute()->fetchOne();
    }
}
