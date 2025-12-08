package com.goodhelp.therapist.domain.repository;

import com.goodhelp.therapist.domain.model.ChatMessage;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ChatMessage aggregate.
 * 
 * Defines the contract for chat message persistence operations.
 * Implementation provided by infrastructure layer.
 */
public interface ChatMessageRepository {

    /**
     * Save a chat message.
     */
    ChatMessage save(ChatMessage message);

    /**
     * Find a message by ID.
     */
    Optional<ChatMessage> findById(Long id);

    /**
     * Find messages between a user and therapist.
     * Messages are ordered by ID ascending (oldest first).
     * 
     * @param userId the user ID
     * @param therapistId the therapist ID
     * @param afterId only return messages with ID greater than this (0 for all)
     * @param limit maximum number of messages to return
     * @return list of messages
     */
    List<ChatMessage> findConversationMessages(Long userId, Long therapistId, Long afterId, int limit);

    /**
     * Get count of unread messages from users for a therapist.
     */
    int countUnreadFromUsers(Long therapistId);

    /**
     * Get list of user IDs with unread messages for a therapist,
     * along with the most recent message ID and unread count.
     */
    List<UnreadConversationInfo> getUnreadConversationsForTherapist(Long therapistId);

    /**
     * Mark messages as read by their IDs.
     */
    void markAsRead(List<Long> messageIds);

    /**
     * Find messages by ID list.
     */
    List<ChatMessage> findByIdIn(List<Long> ids);

    /**
     * DTO for unread conversation summary.
     */
    record UnreadConversationInfo(
        Long latestMessageId,
        Long userId,
        int unreadCount
    ) {}
}

