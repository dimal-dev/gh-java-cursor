<?php

declare(strict_types=1);

namespace App\User\Repository;

use App\Psiholog\Entity\PsihologSchedule;
use App\User\Entity\UserConsultationPsihologSchedule;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;

final class UserConsultationPsihologScheduleRepository
{
    private EntityRepository $repository;

    public function __construct(
        private EntityManagerInterface $em
    ) {
        $this->repository = $em->getRepository(UserConsultationPsihologSchedule::class);
    }

    public function findById(int $id): array
    {
        return $this->repository->findBy([
            'userConsultationId' => $id
        ]);
    }

    public function findBySchedule(PsihologSchedule $psihologSchedule): ?UserConsultationPsihologSchedule
    {
        return $this->repository->findOneBy([
            'psihologSchedule' => $psihologSchedule
        ]);

    }
}
