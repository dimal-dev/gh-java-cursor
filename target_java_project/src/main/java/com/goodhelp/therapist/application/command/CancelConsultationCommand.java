package com.goodhelp.therapist.application.command;

import jakarta.validation.constraints.NotNull;

/**
 * Command to cancel a consultation.
 */
public record CancelConsultationCommand(
    @NotNull(message = "Consultation ID is required")
    Long consultationId,
    
    String reason
) {
}
