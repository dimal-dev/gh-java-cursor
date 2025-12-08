package com.goodhelp.therapist.application.dto;

import java.time.LocalDateTime;

/**
 * DTO for client (user) information as seen by therapist.
 */
public record ClientDto(
    Long userId,
    String name,
    String email,
    int consultationCount,
    LocalDateTime lastConsultation,
    boolean hasNotes
) {
    /**
     * Create a simple client DTO.
     */
    public static ClientDto of(Long userId, String name, String email) {
        return new ClientDto(userId, name, email, 0, null, false);
    }

    /**
     * Create client DTO with consultation info.
     */
    public static ClientDto withConsultations(
            Long userId, 
            String name, 
            String email,
            int consultationCount,
            LocalDateTime lastConsultation) {
        return new ClientDto(userId, name, email, consultationCount, lastConsultation, false);
    }

    /**
     * Check if this client has had any consultations.
     */
    public boolean hasConsultations() {
        return consultationCount > 0;
    }
}
