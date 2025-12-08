package com.goodhelp.therapist.domain.repository;

import com.goodhelp.therapist.domain.model.TherapistSettings;

import java.util.Optional;

/**
 * Repository interface for TherapistSettings.
 * Manages therapist configuration and preferences.
 */
public interface TherapistSettingsRepository {

    /**
     * Find settings by therapist ID.
     */
    Optional<TherapistSettings> findByTherapistId(Long therapistId);

    /**
     * Save settings (create or update).
     */
    TherapistSettings save(TherapistSettings settings);
}
