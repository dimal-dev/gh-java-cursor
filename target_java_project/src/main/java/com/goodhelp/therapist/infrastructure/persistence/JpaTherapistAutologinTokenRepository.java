package com.goodhelp.therapist.infrastructure.persistence;

import com.goodhelp.therapist.domain.model.TherapistAutologinToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for TherapistAutologinToken entity.
 * Manages authentication tokens for passwordless login.
 */
@Repository
public interface JpaTherapistAutologinTokenRepository extends JpaRepository<TherapistAutologinToken, Long> {

    /**
     * Find token by its value.
     */
    Optional<TherapistAutologinToken> findByToken(String token);

    /**
     * Find token by therapist ID.
     */
    @Query("SELECT t FROM TherapistAutologinToken t WHERE t.therapist.id = :therapistId")
    Optional<TherapistAutologinToken> findByTherapistId(@Param("therapistId") Long therapistId);

    /**
     * Delete all tokens for a therapist.
     */
    @Modifying
    @Query("DELETE FROM TherapistAutologinToken t WHERE t.therapist.id = :therapistId")
    void deleteByTherapistId(@Param("therapistId") Long therapistId);
}

