package com.goodhelp.therapist.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Command to send a chat message from therapist to user.
 */
public record SendChatMessageCommand(
    @NotNull(message = "User ID is required")
    Long userId,
    
    @NotBlank(message = "Message body is required")
    String body
) {
    /**
     * Sanitize the message body.
     */
    public String sanitizedBody() {
        return body != null ? body.trim() : "";
    }
}

