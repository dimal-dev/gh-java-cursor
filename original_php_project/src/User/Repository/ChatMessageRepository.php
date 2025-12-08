<?php

declare(strict_types=1);

namespace App\User\Repository;

use App\User\Entity\ChatMessage;
use Doctrine\DBAL\Connection;
use Doctrine\ORM\EntityManagerInterface;

final class ChatMessageRepository
{
    private Connection $connection;

    public function __construct(EntityManagerInterface $em)
    {
        $this->connection = $em->getConnection();
    }

    public function findUserCurrentMessages(int $userId, int $psihologId, int $startFromChatMessageId = 0): array
    {
        $qb = $this->connection->createQueryBuilder();
        $qb->select('*');
        $qb->from('chat_message');

        $qb->andWhere('user_id = :userId');
        $qb->setParameter('userId', $userId);

        $qb->andWhere('psiholog_id = :psihologId');
        $qb->setParameter('psihologId', $psihologId);

        $qb->andWhere('id > :startFromChatMessageId');
        $qb->setParameter('startFromChatMessageId', $startFromChatMessageId);

        $qb->orderBy('id', 'ASC');
        $qb->setMaxResults(100);

        return $qb->execute()->fetchAllAssociative();
    }

    public function getUnreadMessageFromPsihologAmount(int $userId, int $psihologId): int
    {
        $qb = $this->connection->createQueryBuilder();
        $qb->select('COUNT(1)');
        $qb->from('chat_message');

        $qb->andWhere('user_id = :userId');
        $qb->setParameter('userId', $userId);

        $qb->andWhere('psiholog_id = :psihologId');
        $qb->setParameter('psihologId', $psihologId);

        $qb->andWhere('state = :state');
        $qb->setParameter('state', ChatMessage::STATE_UNREAD);

        $qb->andWhere('type = :type');
        $qb->setParameter('type', ChatMessage::TYPE_SENT_BY_THERAPIST);

        return (int) $qb->execute()->fetchOne();
    }

    public function makeRead(array $idList): void
    {
        $qb = $this->connection->createQueryBuilder();
        $qb->update('chat_message');
        $qb->andWhere('id IN(:idList)');
        $qb->setParameter('idList', $idList, Connection::PARAM_INT_ARRAY);
        $qb->set('state', ChatMessage::STATE_READ);

        $qb->execute();
    }
}
