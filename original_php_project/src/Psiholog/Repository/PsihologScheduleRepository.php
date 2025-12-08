<?php

namespace App\Psiholog\Repository;

use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologSchedule;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;

class PsihologScheduleRepository
{
    private EntityRepository $repository;

    public function __construct(
        private EntityManagerInterface $em
    ) {
        $this->repository = $em->getRepository(PsihologSchedule::class);
    }

    public function findFromTime(int $psihologId, string $from, ?int $state = null): array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();

        $qb->select([
            'id',
            'available_at',
            'state',
        ]);
        $qb->from('psiholog_schedule');

        $qb->andWhere('psiholog_id = :psihologId');
        $qb->setParameter('psihologId', $psihologId);

        $qb->andWhere('available_at >= :from');
        $qb->setParameter('from', $from);

        if ($state) {
            $qb->andWhere('state = :state');
            $qb->setParameter('state', $state);
        }

        $qb->orderBy('available_at');

        return $qb->execute()->fetchAllAssociative();
    }

    public function timeExists(int $psihologId, string $time): bool
    {
        $qb = $this->em->getConnection()->createQueryBuilder();

        $qb->select([
            'id',
        ]);
        $qb->from('psiholog_schedule');

        $qb->andWhere('psiholog_id = :psihologId');
        $qb->setParameter('psihologId', $psihologId);

        $qb->andWhere('available_at = :time');
        $qb->setParameter('time', $time);

        return (bool)$qb->execute()->fetchOne();
    }

    public function findByTime(Psiholog $psiholog, \DateTime $availableAt): ?PsihologSchedule
    {
        return $this->repository->findOneBy([
            'psiholog' => $psiholog,
            'availableAt' => $availableAt
        ]);
    }

    public function findBetween(Psiholog $psiholog, \DateTime $from, \DateTime $to): array
    {
        $qb = $this->repository->createQueryBuilder("t");
        $qb->andWhere('t.psiholog = :psiholog');
        $qb->andWhere('t.availableAt >= :from and t.availableAt <= :to');
        $qb->andWhere('t.state = :state');

        $qb->setParameter('psiholog', $psiholog);
        $qb->setParameter('from', $from);
        $qb->setParameter('to', $to);
        $qb->setParameter('state', PsihologSchedule::STATE_AVAILABLE);

        return $qb->getQuery()->getResult();
    }

    public function findById(int $id): ?PsihologSchedule
    {
        return $this->repository->find($id);
    }
}