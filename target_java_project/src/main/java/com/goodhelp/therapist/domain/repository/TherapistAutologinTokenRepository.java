package com.goodhelp.therapist.domain.repository;

import com.goodhelp.therapist.domain.model.TherapistAutologinToken;

import java.util.Optional;

/**
 * Repository interface for TherapistAutologinToken.
 * Manages authentication tokens for passwordless login.
 */
public interface TherapistAutologinTokenRepository {

    /**
     * Find token by its value.
     * Used during authentication to validate the token.
     */
    Optional<TherapistAutologinToken> findByToken(String token);

    /**
     * Find token for a specific therapist.
     */
    Optional<TherapistAutologinToken> findByTherapistId(Long therapistId);

    /**
     * Save a token (create or update).
     */
    TherapistAutologinToken save(TherapistAutologinToken token);

    /**
     * Delete token for a therapist.
     * Used when regenerating tokens.
     */
    void deleteByTherapistId(Long therapistId);
}
