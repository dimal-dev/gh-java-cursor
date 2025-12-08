<?php

declare(strict_types=1);

namespace App\Psiholog\Repository;

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

    public function getUnreadMessagesListGroupedByUserWithAmount(int $psihologId): array
    {
        $qb = $this->connection->createQueryBuilder();
        $qb->select([
            'MAX(cm.id) as id',
            'u.is_full_name_set_by_user',
            'COUNT(1) as unread_messages_amount',
            'IFNULL(u.full_name, "клиент не указал") as full_name',
            'IFNULL(pun.name, "") as psiholog_user_notes_name',
        ]);
        $qb->from('chat_message', 'cm');

        $qb->leftJoin('cm', 'user', 'u', 'cm.user_id = u.id');

        $qb->leftJoin('cm', 'psiholog_user_notes', 'pun', 'cm.psiholog_id = pun.psiholog_id and cm.user_id = pun.user_id');

        $qb->andWhere('cm.psiholog_id = :psihologId');
        $qb->setParameter('psihologId', $psihologId);

        $qb->andWhere('cm.type = :type');
        $qb->setParameter('type', ChatMessage::TYPE_SENT_BY_USER);

        $qb->andWhere('cm.state = :state');
        $qb->setParameter('state', ChatMessage::STATE_UNREAD);

        $qb->groupBy('cm.user_id');
        $qb->setMaxResults(100);

        return $qb->execute()->fetchAllAssociative();
    }

    public function findByIdList(array $idList): array
    {
        $qb = $this->connection->createQueryBuilder();
        $qb->select([
            'id',
            'user_id',
            'type',
            'state',
            'body',
            'sent_at',
        ]);
        $qb->from('chat_message');
        $qb->andWhere('id IN(:idList)');
        $qb->setParameter('idList', $idList, Connection::PARAM_INT_ARRAY);

        return $qb->execute()->fetchAllAssociative();
    }

    public function getUnreadMessageFromUsersAmount(int $psihologId): int
    {
        $qb = $this->connection->createQueryBuilder();
        $qb->select('COUNT(1)');
        $qb->from('chat_message');

        $qb->andWhere('psiholog_id = :psihologId');
        $qb->setParameter('psihologId', $psihologId);

        $qb->andWhere('state = :state');
        $qb->setParameter('state', ChatMessage::STATE_UNREAD);

        $qb->andWhere('type = :type');
        $qb->setParameter('type', ChatMessage::TYPE_SENT_BY_USER);

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
