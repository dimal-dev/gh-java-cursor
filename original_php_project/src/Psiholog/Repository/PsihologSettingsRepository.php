<?php

declare(strict_types=1);

namespace App\Psiholog\Repository;

use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologSettings;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;
use Doctrine\Persistence\ObjectRepository;

final class PsihologSettingsRepository
{
    private EntityRepository $repository;

    public function __construct(EntityManagerInterface $em)
    {
        $this->repository = $em->getRepository(PsihologSettings::class);
    }

    public function findByPsiholog(Psiholog $psiholog): ?PsihologSettings
    {
        return $this->repository->findOneBy([
            'psiholog' => $psiholog,
        ]);
    }
}
