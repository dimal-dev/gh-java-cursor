package com.goodhelp.therapist.application.dto;

import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistProfile;

/**
 * DTO for therapist data.
 */
public record TherapistDto(
    Long id,
    String email,
    String firstName,
    String lastName,
    String fullName,
    String timezone,
    boolean isTelegramLinked,
    String profileTemplate
) {
    /**
     * Create DTO from domain entity.
     */
    public static TherapistDto fromEntity(Therapist therapist) {
        TherapistProfile profile = therapist.getProfile();
        
        return new TherapistDto(
            therapist.getId(),
            therapist.getEmail(),
            profile != null ? profile.getFirstName() : null,
            profile != null ? profile.getLastName() : null,
            therapist.getFullName(),
            therapist.getTimezone(),
            therapist.isTelegramLinked(),
            profile != null ? profile.getProfileTemplate() : null
        );
    }
}
