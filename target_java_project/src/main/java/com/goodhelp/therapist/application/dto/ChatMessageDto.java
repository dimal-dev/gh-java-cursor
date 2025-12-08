package com.goodhelp.therapist.application.dto;

import com.goodhelp.therapist.domain.model.ChatMessage;
import com.goodhelp.therapist.domain.model.MessageStatus;
import com.goodhelp.therapist.domain.model.SenderType;

import java.time.LocalDateTime;

/**
 * DTO representing a single chat message.
 */
public record ChatMessageDto(
    Long id,
    Long userId,
    Long therapistId,
    String body,
    SenderType senderType,
    MessageStatus status,
    LocalDateTime sentAt,
    boolean isUnread
) {
    /**
     * Create from domain entity.
     */
    public static ChatMessageDto fromEntity(ChatMessage message) {
        return new ChatMessageDto(
            message.getId(),
            message.getUserId(),
            message.getTherapistId(),
            message.getBody(),
            message.getSenderType(),
            message.getStatus(),
            message.getSentAt(),
            message.isUnread()
        );
    }
}

