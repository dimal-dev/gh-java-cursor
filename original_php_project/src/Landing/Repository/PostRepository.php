<?php

namespace App\Landing\Repository;

use App\Landing\Entity\PostInterface;
use App\Landing\Entity\PostUa;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\Persistence\ObjectRepository;

class PostRepository
{
    public function __construct(
        private EntityManagerInterface $em
    ) {
    }

    public function findByIdAndLocale(int $id, string $locale): ?array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();
        $qb->from("blog_post_{$locale}", 'bp');
        $qb->select('*');

        $qb->andWhere('id = :id');
        $qb->setParameter('id', $id);

        $qb->setMaxResults(1);

        $result = $qb->execute()->fetchAssociative();

        return $result ?: null;
    }

    public function findByIdAndLocaleAndSlugPosted(int $id, string $locale, string $slug, bool $showDraft = false): ?array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();
        $qb->from("blog_post_{$locale}", 'bp');
        $qb->select('*');

        $qb->andWhere('id = :id');
        $qb->setParameter('id', $id);

        $qb->andWhere('slug = :slug');
        $qb->setParameter('slug', $slug);

        if (!$showDraft) {
            $qb->andWhere('state = :state');
            $qb->setParameter('state', PostInterface::STATE_POSTED);
        }

        $qb->setMaxResults(1);

        $result = $qb->execute()->fetchAssociative();

        return $result ?: null;
    }

    public function findByIdAndLocalePostedShort(int $id, string $locale, bool $showDraft = false): ?array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();
        $qb->from("blog_post_{$locale}", 'bp');
        $qb->select([
            'id',
            'slug',
        ]);

        $qb->andWhere('id = :id');
        $qb->setParameter('id', $id);

        if (!$showDraft) {
            $qb->andWhere('state = :state');
            $qb->setParameter('state', PostInterface::STATE_POSTED);
        }

        $qb->setMaxResults(1);

        $result = $qb->execute()->fetchAssociative();

        return $result ?: null;
    }

    public function findPostList(string $locale, int $limit, int $offset): array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();
        $qb->from("blog_post_{$locale}", 'bp');
        $qb->select('*');

        $qb->andWhere('state = :state');
        $qb->setParameter('state', PostInterface::STATE_POSTED);

        $qb->setMaxResults($limit);
        $qb->setFirstResult($offset);
        $qb->orderBy('posted_at', 'DESC');

        return $qb->execute()->fetchAllAssociative();
    }
}