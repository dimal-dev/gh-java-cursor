package com.goodhelp.therapist.application.command;

import jakarta.validation.constraints.NotBlank;

/**
 * Command to update therapist settings.
 */
public record UpdateSettingsCommand(
    @NotBlank(message = "Timezone is required")
    String timezone
) {
}
