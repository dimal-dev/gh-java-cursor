<?php

namespace App\Psiholog\Service;

use App\Psiholog\Entity\Psiholog;
use App\User\Entity\ChatMessage;
use App\User\Repository\ChatMessageRepository;
use Symfony\Component\HttpFoundation\Session\Session;

class ChatMessagesRetriever
{
    public function __construct(
        private ChatMessageRepository $chatMessageRepository,
        private TimeHelper $timeHelper,
    ) {
    }

    public function get(int $userId, Psiholog $psiholog, int $startFromChatMessageId = 0): array
    {
        $messages = $this->chatMessageRepository->findUserCurrentMessages(
            $userId,
            $psiholog->getId(),
            $startFromChatMessageId
        );

        $makeReadIdList = [];
        foreach ($messages as $key => $message) {
            if ($message['state'] == ChatMessage::STATE_UNREAD && $message['type'] == ChatMessage::TYPE_SENT_BY_USER) {
                $makeReadIdList[] = $message['id'];
            } else {
                $message['state'] = ChatMessage::STATE_READ;
            }
            $message['sent_at'] = $this->timeHelper->toPsihologTz($message['sent_at']);
            $messages[$key] = $message;
        }

        if (!empty($makeReadIdList)) {
            $this->chatMessageRepository->makeRead($makeReadIdList);
        }

        return $messages;
    }
}
