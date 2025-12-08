package com.goodhelp.therapist.application.command;

import jakarta.validation.constraints.NotNull;

/**
 * Command to save therapist's private notes about a client.
 */
public record SaveUserNotesCommand(
    @NotNull(message = "User ID is required")
    Long userId,
    
    String notes,
    
    String clientName
) {
    /**
     * Create command with just notes (name unchanged).
     */
    public static SaveUserNotesCommand withNotes(Long userId, String notes) {
        return new SaveUserNotesCommand(userId, notes, null);
    }
}
