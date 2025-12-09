package com.goodhelp.user.domain.repository;

import com.goodhelp.user.domain.model.UserConsultation;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserConsultation entity.
 * Defines domain-level persistence operations.
 * 
 * <p>Implementation will be provided by the infrastructure layer
 * using Spring Data JPA.</p>
 */
public interface UserConsultationRepository {

    /**
     * Find consultation by ID.
     */
    Optional<UserConsultation> findById(Long id);

    /**
     * Find all upcoming (active) consultations for a user.
     * Returns consultations with CREATED state and start time in the future.
     */
    List<UserConsultation> findUpcomingByUserId(Long userId);

    /**
     * Find all upcoming (active) consultations for a user, ordered by start time.
     */
    List<UserConsultation> findUpcomingByUserIdOrderByStartTime(Long userId);

    /**
     * Find consultations by user and therapist.
     */
    List<UserConsultation> findByUserIdAndTherapistId(Long userId, Long therapistId);

    /**
     * Find the closest upcoming consultation for a user.
     */
    Optional<UserConsultation> findClosestUpcomingByUserId(Long userId);

    /**
     * Find all consultations for a user (all states).
     */
    List<UserConsultation> findByUserId(Long userId);

    /**
     * Count completed consultations for a user.
     */
    int countCompletedByUserId(Long userId);

    /**
     * Save a consultation (create or update).
     */
    UserConsultation save(UserConsultation consultation);

    /**
     * Check if consultation exists by ID.
     */
    boolean existsById(Long id);
}

