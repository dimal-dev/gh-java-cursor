package com.goodhelp.therapist.application.dto;

import com.goodhelp.therapist.domain.model.TherapistSettings;

/**
 * DTO for therapist settings display.
 * Contains all information needed for the settings page.
 */
public record SettingsDto(
    String email,
    String timezone,
    String telegramSetupToken,
    boolean telegramConnected,
    String scheduleTimeCap
) {

    /**
     * Create DTO from TherapistSettings entity.
     *
     * @param settings the settings entity
     * @param email the therapist's email
     * @param telegramSetupToken token for Telegram setup
     * @return populated DTO
     */
    public static SettingsDto fromEntity(TherapistSettings settings, String email, String telegramSetupToken) {
        return new SettingsDto(
            email,
            settings.getTimezone(),
            telegramSetupToken,
            settings.isTelegramLinked(),
            settings.getScheduleTimeCap()
        );
    }

    /**
     * Create a default DTO when settings don't exist yet.
     *
     * @param email the therapist's email
     * @param telegramSetupToken token for Telegram setup
     * @return default settings DTO
     */
    public static SettingsDto defaultSettings(String email, String telegramSetupToken) {
        return new SettingsDto(
            email,
            "Europe/Kiev",
            telegramSetupToken,
            false,
            "+3 hour"
        );
    }
}

