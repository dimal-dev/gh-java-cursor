package com.goodhelp.therapist.domain.repository;

import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistRole;
import com.goodhelp.therapist.domain.model.TherapistStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Therapist aggregate.
 * Defines domain-level persistence operations.
 * 
 * <p>Implementation will be provided by the infrastructure layer
 * using Spring Data JPA.</p>
 */
public interface TherapistRepository {

    /**
     * Find therapist by ID.
     */
    Optional<Therapist> findById(Long id);

    /**
     * Find therapist by email address.
     * Email comparison should be case-insensitive.
     */
    Optional<Therapist> findByEmail(String email);

    /**
     * Find all active therapists.
     */
    List<Therapist> findAllByStatus(TherapistStatus status);

    /**
     * Find all therapists with specific status and roles.
     * Used to get real therapists (excluding test accounts) for landing page.
     */
    List<Therapist> findAllByStatusAndRoleIn(TherapistStatus status, List<TherapistRole> roles);

    /**
     * Find all active real therapists (non-test accounts).
     */
    default List<Therapist> findAllActiveRealTherapists() {
        return findAllByStatusAndRoleIn(
            TherapistStatus.ACTIVE, 
            List.of(TherapistRole.THERAPIST)
        );
    }

    /**
     * Save a therapist (create or update).
     */
    Therapist save(Therapist therapist);

    /**
     * Check if email is already in use.
     */
    boolean existsByEmail(String email);

    /**
     * Check if therapist exists by ID.
     */
    boolean existsById(Long id);
}
