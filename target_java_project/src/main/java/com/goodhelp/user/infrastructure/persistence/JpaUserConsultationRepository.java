package com.goodhelp.user.infrastructure.persistence;

import com.goodhelp.user.domain.model.ConsultationState;
import com.goodhelp.user.domain.model.UserConsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for UserConsultation entity.
 */
@Repository
public interface JpaUserConsultationRepository extends JpaRepository<UserConsultation, Long> {

    /**
     * Find all consultations for a user.
     */
    List<UserConsultation> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find consultations by user and therapist.
     */
    List<UserConsultation> findByUserIdAndTherapistIdOrderByCreatedAtDesc(
        Long userId, Long therapistId);

    /**
     * Find upcoming consultations for a user.
     * Uses a native query to join with schedule slots and filter by state and time.
     */
    @Query(value = """
        SELECT DISTINCT uc.*
        FROM user_consultation uc
        INNER JOIN user_consultation_psiholog_schedule ucps ON uc.id = ucps.user_consultation_id
        INNER JOIN psiholog_schedule ps ON ucps.psiholog_schedule_id = ps.id
        WHERE uc.user_id = :userId
        AND uc.state = :state
        AND ps.state = 2
        AND ps.available_at > :now
        ORDER BY ps.available_at ASC
        """, nativeQuery = true)
    List<UserConsultation> findUpcomingByUserId(
        @Param("userId") Long userId,
        @Param("state") int state,
        @Param("now") LocalDateTime now);

    /**
     * Find the closest upcoming consultation for a user.
     */
    @Query(value = """
        SELECT DISTINCT uc.*
        FROM user_consultation uc
        INNER JOIN user_consultation_psiholog_schedule ucps ON uc.id = ucps.user_consultation_id
        INNER JOIN psiholog_schedule ps ON ucps.psiholog_schedule_id = ps.id
        WHERE uc.user_id = :userId
        AND uc.state = :state
        AND ps.state = 2
        AND ps.available_at > :now
        ORDER BY ps.available_at ASC
        LIMIT 1
        """, nativeQuery = true)
    Optional<UserConsultation> findClosestUpcomingByUserId(
        @Param("userId") Long userId,
        @Param("state") int state,
        @Param("now") LocalDateTime now);

    /**
     * Count completed consultations for a user.
     */
    @Query("SELECT COUNT(c) FROM UserConsultation c WHERE c.user.id = :userId AND c.state = :state")
    int countByUserIdAndState(@Param("userId") Long userId, @Param("state") ConsultationState state);
}

