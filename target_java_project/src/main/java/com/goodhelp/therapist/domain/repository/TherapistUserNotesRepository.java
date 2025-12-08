package com.goodhelp.therapist.domain.repository;

import com.goodhelp.therapist.domain.model.TherapistUserNotes;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TherapistUserNotes.
 * Manages private notes therapists keep about their clients.
 */
public interface TherapistUserNotesRepository {

    /**
     * Find notes for a specific therapist-user pair.
     */
    Optional<TherapistUserNotes> findByTherapistIdAndUserId(Long therapistId, Long userId);

    /**
     * Find all notes for a therapist.
     */
    List<TherapistUserNotes> findByTherapistId(Long therapistId);

    /**
     * Save notes (create or update).
     */
    TherapistUserNotes save(TherapistUserNotes notes);

    /**
     * Delete notes.
     */
    void delete(TherapistUserNotes notes);
}
