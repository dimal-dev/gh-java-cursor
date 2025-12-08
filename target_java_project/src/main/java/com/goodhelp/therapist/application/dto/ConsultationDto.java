package com.goodhelp.therapist.application.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * DTO for displaying upcoming consultations in the schedule list.
 */
public record ConsultationDto(
    Long id,
    Long userId,
    String clientName,
    String therapistNotesName,
    boolean isFullNameSetByUser,
    String consultationType,
    LocalDateTime availableAtUtc,
    ZonedDateTime availableAtLocal,
    String availableAtLabel,
    long availableAtTimestampUtc
) {
    /**
     * Check if client has a display name.
     */
    public boolean hasClientName() {
        return clientName != null && !clientName.isBlank();
    }

    /**
     * Get the display name for the client.
     * Returns therapist notes name if available, otherwise client name.
     */
    public String getDisplayName() {
        if (therapistNotesName != null && !therapistNotesName.isBlank()) {
            if (hasClientName()) {
                return clientName + " / " + therapistNotesName;
            }
            return therapistNotesName;
        }
        return clientName != null ? clientName : "клиент не указал";
    }

    /**
     * Get consultation type label.
     */
    public String getTypeLabel() {
        return switch (consultationType) {
            case "individual" -> "Індивідуальна (50 хвилин)";
            case "couple" -> "Парна/Сімейна (80 хвилин)";
            default -> consultationType;
        };
    }
}

