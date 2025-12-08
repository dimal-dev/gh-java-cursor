<?php

declare(strict_types=1);

namespace App\User\Repository;

use App\Psiholog\Entity\PsihologSchedule;
use App\User\Entity\User;
use App\User\Entity\UserConsultation;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;
use Doctrine\Persistence\ObjectRepository;

final class UserConsultationRepository
{
    private EntityRepository $repository;

    public function __construct(
        private EntityManagerInterface $em
    ) {
        $this->repository = $em->getRepository(UserConsultation::class);
    }

    public function findById(int $id): ?UserConsultation
    {
        return $this->repository->find($id);
    }

    public function findUpcoming(int $userId): array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();

        $qb->select([
            'uc.psiholog_id',
            'MIN(ps.available_at) as available_at',
            'uc.id',
        ]);
        $qb->from('user_consultation', 'uc');
        $qb->innerJoin('uc', 'user_consultation_psiholog_schedule', 'ucps', 'uc.id = ucps.user_consultation_id');
        $qb->innerJoin('ucps', 'psiholog_schedule', 'ps', 'ucps.psiholog_schedule_id = ps.id');

        $qb->andWhere('uc.user_id = :userId');
        $qb->setParameter('userId', $userId);

        $qb->andWhere('uc.state = :ucState');
        $qb->setParameter('ucState', UserConsultation::STATE_CREATED);

        $qb->andWhere('ps.state = :psState');
        $qb->setParameter('psState', PsihologSchedule::STATE_BOOKED);

        $qb->andWhere('ps.available_at > :from');
        $qb->setParameter('from', date('Y-m-d H:i:s'));

        $qb->groupBy('uc.id');

        $qb->orderBy('ps.available_at', 'ASC');

        return $qb->execute()->fetchAllAssociative();
    }

    public function findLatest(int $userId): ?array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();

        $qb->select([
            'uc.psiholog_id',
            'ps.available_at',
            'uc.id',
        ]);
        $qb->from('user_consultation', 'uc');
        $qb->innerJoin('uc', 'user_consultation_psiholog_schedule', 'ucps', 'uc.id = ucps.user_consultation_id');
        $qb->innerJoin('ucps', 'psiholog_schedule', 'ps', 'ucps.psiholog_schedule_id = ps.id');

        $qb->andWhere('uc.user_id = :userId');
        $qb->setParameter('userId', $userId);

        $qb->orderBy('ps.available_at', 'DESC');
        $qb->setMaxResults(1);

        return $qb->execute()->fetchAssociative() ?: null;
    }
}
