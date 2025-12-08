package com.goodhelp.therapist.application.command;

import jakarta.validation.constraints.NotBlank;

/**
 * Command to authenticate a therapist using an auto-login token.
 */
public record AuthenticateCommand(
    @NotBlank(message = "Token is required")
    String token
) {
}
