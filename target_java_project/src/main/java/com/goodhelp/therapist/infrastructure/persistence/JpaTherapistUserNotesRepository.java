package com.goodhelp.therapist.infrastructure.persistence;

import com.goodhelp.therapist.domain.model.TherapistUserNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for TherapistUserNotes entity.
 * Manages private notes therapists keep about their clients.
 */
@Repository
public interface JpaTherapistUserNotesRepository extends JpaRepository<TherapistUserNotes, Long> {

    /**
     * Find notes for a specific therapist-user pair.
     */
    @Query("SELECT n FROM TherapistUserNotes n WHERE n.therapist.id = :therapistId AND n.userId = :userId")
    Optional<TherapistUserNotes> findByTherapistIdAndUserId(
        @Param("therapistId") Long therapistId,
        @Param("userId") Long userId
    );

    /**
     * Find all notes for a therapist.
     */
    @Query("SELECT n FROM TherapistUserNotes n WHERE n.therapist.id = :therapistId ORDER BY n.id DESC")
    List<TherapistUserNotes> findByTherapistId(@Param("therapistId") Long therapistId);

    /**
     * Find notes with non-empty content for a therapist.
     */
    @Query("SELECT n FROM TherapistUserNotes n WHERE n.therapist.id = :therapistId " +
           "AND n.notes IS NOT NULL AND n.notes <> '' ORDER BY n.id DESC")
    List<TherapistUserNotes> findNonEmptyByTherapistId(@Param("therapistId") Long therapistId);
}

