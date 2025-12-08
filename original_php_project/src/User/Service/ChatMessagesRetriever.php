<?php

namespace App\User\Service;

use App\User\Entity\ChatMessage;
use App\User\Entity\User;
use App\User\Repository\ChatMessageRepository;
use Symfony\Component\HttpFoundation\Session\Session;

class ChatMessagesRetriever
{
    public const UNREAD_MESSAGES_SESSION_KEY = 'cumsk';

    public function __construct(
        private ChatMessageRepository $chatMessageRepository,
        private TimeHelper $timeHelper,
    ) {
    }

    public function get(User $user, int $psihologId, int $startFromChatMessageId = 0): array
    {
        $messages = $this->chatMessageRepository->findUserCurrentMessages(
            $user->getId(),
            $psihologId,
            $startFromChatMessageId
        );

        $this->timeHelper->setTimezone(new \DateTimeZone($user->getTimezone()));

        $makeReadIdList = [];
        foreach ($messages as $key => $message) {
            if ($message['state'] == ChatMessage::STATE_UNREAD && $message['type'] == ChatMessage::TYPE_SENT_BY_THERAPIST) {
                $makeReadIdList[] = $message['id'];
            } else {
                $message['state'] = ChatMessage::STATE_READ;
            }
            $message['sent_at'] = $this->timeHelper->toUserTz($message['sent_at']);
            $messages[$key] = $message;
        }

        if (!empty($makeReadIdList)) {
            $this->chatMessageRepository->makeRead($makeReadIdList);
        }

        return $messages;
    }
}
