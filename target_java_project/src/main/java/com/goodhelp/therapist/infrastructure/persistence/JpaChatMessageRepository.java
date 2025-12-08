package com.goodhelp.therapist.infrastructure.persistence;

import com.goodhelp.therapist.domain.model.ChatMessage;
import com.goodhelp.therapist.domain.model.MessageStatus;
import com.goodhelp.therapist.domain.model.SenderType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository for ChatMessage entity.
 */
@Repository
public interface JpaChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Find messages in a conversation after a certain ID.
     */
    @Query("""
        SELECT m FROM ChatMessage m 
        WHERE m.userId = :userId 
          AND m.therapistId = :therapistId 
          AND m.id > :afterId 
        ORDER BY m.id ASC
        """)
    List<ChatMessage> findConversationMessagesAfterId(
        @Param("userId") Long userId,
        @Param("therapistId") Long therapistId,
        @Param("afterId") Long afterId,
        Pageable pageable
    );

    /**
     * Count unread messages from users for a therapist.
     */
    @Query("""
        SELECT COUNT(m) FROM ChatMessage m 
        WHERE m.therapistId = :therapistId 
          AND m.senderType = :senderType 
          AND m.status = :status
        """)
    int countUnreadFromUsers(
        @Param("therapistId") Long therapistId,
        @Param("senderType") SenderType senderType,
        @Param("status") MessageStatus status
    );

    /**
     * Get the latest message ID per user with unread messages for a therapist.
     */
    @Query(value = """
        SELECT MAX(m.id) as latestMessageId, m.user_id as userId, COUNT(m.id) as unreadCount
        FROM chat_message m
        WHERE m.psiholog_id = :therapistId
          AND m.type = :senderTypeValue
          AND m.state = :statusValue
        GROUP BY m.user_id
        ORDER BY latestMessageId DESC
        LIMIT 100
        """, nativeQuery = true)
    List<Object[]> getUnreadConversationsNative(
        @Param("therapistId") Long therapistId,
        @Param("senderTypeValue") int senderTypeValue,
        @Param("statusValue") int statusValue
    );

    /**
     * Mark messages as read.
     */
    @Modifying
    @Query("""
        UPDATE ChatMessage m 
        SET m.status = :status 
        WHERE m.id IN :ids
        """)
    void updateStatusByIds(
        @Param("ids") List<Long> ids,
        @Param("status") MessageStatus status
    );

    /**
     * Find messages by IDs ordered by ID.
     */
    @Query("""
        SELECT m FROM ChatMessage m 
        WHERE m.id IN :ids 
        ORDER BY m.id DESC
        """)
    List<ChatMessage> findByIdInOrderByIdDesc(@Param("ids") List<Long> ids);
}

