package com.goodhelp.therapist.application.dto;

import com.goodhelp.therapist.domain.model.TherapistUserNotes;

import java.time.LocalDateTime;

/**
 * DTO for user notes data.
 */
public record UserNotesDto(
    Long id,
    Long userId,
    String clientName,
    String notes,
    LocalDateTime updatedAt
) {
    /**
     * Create DTO from domain entity.
     */
    public static UserNotesDto fromEntity(TherapistUserNotes entity) {
        return new UserNotesDto(
            entity.getId(),
            entity.getUserId(),
            entity.getClientName(),
            entity.getNotesOrEmpty(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Create empty notes DTO.
     */
    public static UserNotesDto empty(Long userId, String clientName) {
        return new UserNotesDto(null, userId, clientName, "", null);
    }

    /**
     * Check if notes exist.
     */
    public boolean hasNotes() {
        return notes != null && !notes.isBlank();
    }
}
