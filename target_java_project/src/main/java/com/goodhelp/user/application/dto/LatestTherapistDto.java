package com.goodhelp.user.application.dto;

/**
 * DTO for latest therapist information (when no consultations exist).
 */
public record LatestTherapistDto(
    Long therapistId,
    String firstName,
    String lastName,
    String photoUrl
) {
    public String fullName() {
        return firstName + " " + lastName;
    }
}

