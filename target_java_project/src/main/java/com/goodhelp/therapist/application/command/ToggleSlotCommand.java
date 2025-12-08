package com.goodhelp.therapist.application.command;

import jakarta.validation.constraints.NotNull;

/**
 * Command to toggle a schedule slot between available and unavailable.
 * Used by schedule settings UI.
 */
public record ToggleSlotCommand(
    @NotNull(message = "Slot ID is required")
    Long slotId
) {
}
