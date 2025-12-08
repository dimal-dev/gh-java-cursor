package com.goodhelp.therapist.application.usecase;

import com.goodhelp.therapist.application.dto.ChatMessageDto;
import com.goodhelp.therapist.domain.model.ChatMessage;
import com.goodhelp.therapist.domain.model.MessageStatus;
import com.goodhelp.therapist.domain.model.SenderType;
import com.goodhelp.therapist.domain.repository.ChatMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Use case for retrieving chat messages between therapist and user.
 * Also handles marking user messages as read when viewed by therapist.
 */
@Service
@Transactional
public class GetChatMessagesUseCase {

    private static final Logger logger = LoggerFactory.getLogger(GetChatMessagesUseCase.class);
    private static final int DEFAULT_LIMIT = 100;

    private final ChatMessageRepository chatMessageRepository;

    public GetChatMessagesUseCase(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * Get messages for a conversation and mark user messages as read.
     * 
     * @param therapistId the therapist ID
     * @param userId the user ID
     * @param afterMessageId only return messages after this ID (0 for all)
     * @return list of messages as DTOs
     */
    public List<ChatMessageDto> execute(Long therapistId, Long userId, Long afterMessageId) {
        logger.debug("Getting chat messages for therapist {} and user {}, after {}", 
            therapistId, userId, afterMessageId);

        List<ChatMessage> messages = chatMessageRepository.findConversationMessages(
            userId, 
            therapistId, 
            afterMessageId, 
            DEFAULT_LIMIT
        );

        // Collect IDs of unread messages from user to mark as read
        List<Long> unreadFromUserIds = new ArrayList<>();
        for (ChatMessage message : messages) {
            if (message.getSenderType() == SenderType.USER && message.getStatus() == MessageStatus.UNREAD) {
                unreadFromUserIds.add(message.getId());
            }
        }

        // Mark as read
        if (!unreadFromUserIds.isEmpty()) {
            chatMessageRepository.markAsRead(unreadFromUserIds);
            logger.debug("Marked {} messages as read", unreadFromUserIds.size());
        }

        return messages.stream()
            .map(ChatMessageDto::fromEntity)
            .toList();
    }

    /**
     * Get the latest message ID from a list of messages.
     */
    public Long getLatestMessageId(List<ChatMessageDto> messages) {
        if (messages == null || messages.isEmpty()) {
            return 0L;
        }
        return messages.get(messages.size() - 1).id();
    }
}

