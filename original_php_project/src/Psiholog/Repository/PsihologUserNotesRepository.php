<?php

declare(strict_types=1);

namespace App\Psiholog\Repository;

use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologSettings;
use App\Psiholog\Entity\PsihologUserNotes;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;
use Doctrine\Persistence\ObjectRepository;

final class PsihologUserNotesRepository
{
    private EntityRepository $repository;

    public function __construct(EntityManagerInterface $em)
    {
        $this->repository = $em->getRepository(PsihologUserNotes::class);
    }

    public function findByPsihologAndUserId(Psiholog $psiholog, int $userId): ?PsihologUserNotes
    {
        return $this->repository->findOneBy([
            'psiholog' => $psiholog,
            'userId' => $userId
        ]);
    }
}
