package com.goodhelp.therapist.infrastructure.persistence;

import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistRole;
import com.goodhelp.therapist.domain.model.TherapistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Therapist entity.
 * Provides low-level data access operations.
 */
@Repository
public interface JpaTherapistRepository extends JpaRepository<Therapist, Long> {

    /**
     * Find therapist by email (case-insensitive).
     */
    Optional<Therapist> findByEmailIgnoreCase(String email);

    /**
     * Find all therapists with given status.
     */
    List<Therapist> findByStatus(TherapistStatus status);

    /**
     * Find all therapists with given status and roles.
     */
    @Query("SELECT t FROM Therapist t WHERE t.status = :status AND t.role IN :roles ORDER BY t.id")
    List<Therapist> findByStatusAndRoleIn(
        @Param("status") TherapistStatus status,
        @Param("roles") List<TherapistRole> roles
    );

    /**
     * Check if email is already registered.
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Find active therapists for landing page display.
     */
    @Query("SELECT t FROM Therapist t WHERE t.status = :status AND t.role = :role " +
           "AND t.profile IS NOT NULL ORDER BY t.id")
    List<Therapist> findActiveTherapistsWithProfile(
        @Param("status") TherapistStatus status,
        @Param("role") TherapistRole role
    );
}

