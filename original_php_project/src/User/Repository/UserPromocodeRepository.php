<?php

declare(strict_types=1);

namespace App\User\Repository;

use App\User\Entity\Promocode;
use App\User\Entity\UserPromocode;
use Doctrine\ORM\EntityManagerInterface;

final class UserPromocodeRepository
{

    public function __construct(
        private EntityManagerInterface $em
    ) {
    }

    public function countNumberOfUses(string $email, int $promocodeId): int
    {
        $qb = $this->em->getConnection()->createQueryBuilder();
        $qb->select('COUNT(1)');
        $qb->from('user_promocode');

        $qb->andWhere('email = :email');
        $qb->setParameter('email', $email);

        $qb->andWhere('promocode_id = :promocodeId');
        $qb->setParameter('promocodeId', $promocodeId);

        $qb->andWhere('state = :state');
        $qb->setParameter('state', UserPromocode::STATE_USED);

        return (int) $qb->execute()->fetchOne();
    }
}
