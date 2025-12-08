<?php

declare(strict_types=1);

namespace App\Staff\Repository;

use Doctrine\ORM\EntityManagerInterface;

final class PsihologRepository
{
    public function __construct(
        private EntityManagerInterface $em
    )
    {
    }

    public function findAll(): array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();
        $qb->select([
            'id',
            'email',
        ]);
        $qb->from('psiholog');

        return $qb->execute()->fetchAllAssociative();
    }

    public function findAllNameWithIdList(): array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();
        $qb->select([
            'psiholog_id',
            'first_name',
            'last_name',
        ]);
        $qb->from('psiholog_profile');

        return $qb->execute()->fetchAllAssociative();

    }
}
