package com.goodhelp.therapist.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a chat message between a user and a therapist.
 * 
 * <p>This entity is part of a shared context - messages can be sent
 * by either the user or the therapist. The senderType field determines
 * the direction.</p>
 * 
 * <p>Business rules:</p>
 * <ul>
 *   <li>Messages sent by users are marked as unread for therapists</li>
 *   <li>Messages sent by therapists are marked as unread for users</li>
 *   <li>Messages become read when the recipient views them</li>
 * </ul>
 */
@Entity
@Table(name = "chat_message", indexes = {
    @Index(name = "idx_chat_user_therapist", columnList = "user_id, therapist_id"),
    @Index(name = "idx_chat_therapist_user", columnList = "therapist_id, user_id"),
    @Index(name = "idx_chat_created", columnList = "sent_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "therapist_id", nullable = false)
    private Long therapistId;

    @Column(name = "type", nullable = false)
    @Convert(converter = SenderTypeConverter.class)
    private SenderType senderType;

    @Column(name = "state", nullable = false)
    @Convert(converter = MessageStatusConverter.class)
    private MessageStatus status;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    /**
     * Private constructor - use factory methods.
     */
    private ChatMessage(Long userId, Long therapistId, String body, SenderType senderType) {
        this.userId = Objects.requireNonNull(userId, "User ID is required");
        this.therapistId = Objects.requireNonNull(therapistId, "Therapist ID is required");
        this.body = Objects.requireNonNull(body, "Message body is required");
        this.senderType = Objects.requireNonNull(senderType, "Sender type is required");
        this.status = MessageStatus.UNREAD;
        this.sentAt = LocalDateTime.now();
    }

    // ==================== Factory Methods ====================

    /**
     * Create a new message sent by the therapist to the user.
     */
    public static ChatMessage fromTherapist(Long therapistId, Long userId, String body) {
        return new ChatMessage(userId, therapistId, body, SenderType.THERAPIST);
    }

    /**
     * Create a new message sent by the user to the therapist.
     */
    public static ChatMessage fromUser(Long userId, Long therapistId, String body) {
        return new ChatMessage(userId, therapistId, body, SenderType.USER);
    }

    // ==================== Business Methods ====================

    /**
     * Check if this message was sent by the user.
     */
    public boolean isSentByUser() {
        return senderType == SenderType.USER;
    }

    /**
     * Check if this message was sent by the therapist.
     */
    public boolean isSentByTherapist() {
        return senderType == SenderType.THERAPIST;
    }

    /**
     * Check if this message is unread.
     */
    public boolean isUnread() {
        return status == MessageStatus.UNREAD;
    }

    /**
     * Check if this message is read.
     */
    public boolean isRead() {
        return status == MessageStatus.READ;
    }

    /**
     * Mark this message as read.
     */
    public void markAsRead() {
        this.status = MessageStatus.READ;
    }

    // ==================== Equality ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("ChatMessage[id=%d, userId=%d, therapistId=%d, sender=%s, status=%s]",
            id, userId, therapistId, senderType, status);
    }
}

