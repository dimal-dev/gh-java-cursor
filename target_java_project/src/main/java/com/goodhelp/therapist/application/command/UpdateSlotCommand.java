package com.goodhelp.therapist.application.command;

import com.goodhelp.booking.domain.model.SlotStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Command to update a schedule slot's status.
 * Used for toggling availability or marking slots as done/failed.
 */
public record UpdateSlotCommand(
    @NotNull(message = "Slot ID is required")
    Long slotId,
    
    @NotNull(message = "New status is required")
    SlotStatus newStatus
) {
}
