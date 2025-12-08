<?php

declare(strict_types=1);

namespace App\Psiholog\Repository;

use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologPrice;
use Doctrine\DBAL\Connection;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\ORM\EntityRepository;

final class PsihologPriceRepository
{
    private EntityRepository $repository;

    public function __construct(
        private EntityManagerInterface $em
    ) {
        $this->repository = $em->getRepository(PsihologPrice::class);
    }

    public function findCurrentByPsiholog(Psiholog $psiholog): array
    {
        return $this->repository->findBy([
            'psiholog' => $psiholog,
            'state' => PsihologPrice::STATE_CURRENT,
        ]);
    }

    public function findCurrent(Psiholog $psiholog, string $currency, int $type): ?PsihologPrice
    {
        return $this->repository->findOneBy([
            'psiholog' => $psiholog,
            'state' => PsihologPrice::STATE_CURRENT,
            'type' => $type,
            'currency' => $currency,
        ]);
    }

    public function findById(int $id): ?PsihologPrice
    {
        return $this->repository->find($id);
    }

    public function findBySlug(string $slug): ?PsihologPrice
    {
        return $this->repository->findOneBy([
            'slug' => $slug,
        ]);
    }

    public function findAllCurrentIndividualByIdList(array $idList): array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();

        $qb->from('psiholog_price');
        $qb->select([
            'psiholog_id',
            'price',
            'currency',
        ]);

        $qb->andWhere('psiholog_id IN(:idList)');
        $qb->setParameter('idList', $idList, Connection::PARAM_INT_ARRAY);

        $qb->andWhere('type = :type');
        $qb->setParameter('type', PsihologPrice::TYPE_INDIVIDUAL);

        $qb->andWhere('state = :state');
        $qb->setParameter('state', PsihologPrice::STATE_CURRENT);

        return $qb->execute()->fetchAllAssociative();
    }

    public function findAllCurrentByIdList(array $idList): array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();

        $qb->select([
            'psiholog_id',
            'price',
            'currency',
            'type',
        ]);
        $qb->from('psiholog_price');

        $qb->andWhere('psiholog_id IN(:idList)');
        $qb->setParameter('idList', $idList, Connection::PARAM_INT_ARRAY);

        $qb->andWhere('state = :state');
        $qb->setParameter('state', PsihologPrice::STATE_CURRENT);

        return $qb->execute()->fetchAllAssociative();
    }
}
