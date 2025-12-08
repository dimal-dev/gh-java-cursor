package com.goodhelp.therapist.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Command to request a login link for a therapist.
 * Triggers email with auto-login token.
 */
public record RequestLoginCommand(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email
) {
}
