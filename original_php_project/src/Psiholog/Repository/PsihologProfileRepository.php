<?php

declare(strict_types=1);

namespace App\Psiholog\Repository;

use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologProfile;
use App\Psiholog\Entity\PsihologSettings;
use Doctrine\DBAL\Connection;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;

final class PsihologProfileRepository
{
    private EntityRepository $repository;

    public function __construct(
        private EntityManagerInterface $em
    ) {
        $this->repository = $em->getRepository(PsihologProfile::class);
    }

    public function findByPsiholog(Psiholog $psiholog): ?PsihologProfile
    {
        return $this->repository->findOneBy([
            'psiholog' => $psiholog,
        ]);
    }

    public function findByPsihologId(int $psihologId): ?PsihologProfile
    {
        return $this->repository->findOneBy([
            'psihologId' => $psihologId,
        ]);
    }

    public function findAllByIdList(array $idList): array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();

        $qb->from('psiholog_profile');
        $qb->select([
            'psiholog_id',
            'first_name',
            'last_name',
            'profile_template',
        ]);

        $qb->andWhere('psiholog_id IN(:idList)');
        $qb->setParameter('idList', $idList, Connection::PARAM_INT_ARRAY);

        return $qb->execute()->fetchAllAssociative();
    }
}
