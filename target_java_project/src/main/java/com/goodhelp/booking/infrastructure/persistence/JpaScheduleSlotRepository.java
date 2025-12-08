package com.goodhelp.booking.infrastructure.persistence;

import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.booking.domain.model.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for ScheduleSlot entity.
 * Manages therapist schedule slots for booking.
 */
@Repository
public interface JpaScheduleSlotRepository extends JpaRepository<ScheduleSlot, Long> {

    /**
     * Find slots for a therapist within a date range.
     */
    @Query("SELECT s FROM ScheduleSlot s WHERE s.therapist.id = :therapistId " +
           "AND s.availableAt >= :from AND s.availableAt < :to ORDER BY s.availableAt")
    List<ScheduleSlot> findByTherapistIdAndAvailableAtBetween(
        @Param("therapistId") Long therapistId,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );

    /**
     * Find slots for a therapist after a certain time with specific status.
     */
    @Query("SELECT s FROM ScheduleSlot s WHERE s.therapist.id = :therapistId " +
           "AND s.availableAt > :after AND s.status = :status ORDER BY s.availableAt")
    List<ScheduleSlot> findByTherapistIdAndAvailableAtAfterAndStatus(
        @Param("therapistId") Long therapistId,
        @Param("after") LocalDateTime after,
        @Param("status") SlotStatus status
    );

    /**
     * Find all upcoming slots for a therapist (any status).
     */
    @Query("SELECT s FROM ScheduleSlot s WHERE s.therapist.id = :therapistId " +
           "AND s.availableAt > :after ORDER BY s.availableAt")
    List<ScheduleSlot> findByTherapistIdAndAvailableAtAfter(
        @Param("therapistId") Long therapistId,
        @Param("after") LocalDateTime after
    );

    /**
     * Find slot by therapist and exact time.
     */
    @Query("SELECT s FROM ScheduleSlot s WHERE s.therapist.id = :therapistId " +
           "AND s.availableAt = :availableAt")
    Optional<ScheduleSlot> findByTherapistIdAndAvailableAt(
        @Param("therapistId") Long therapistId,
        @Param("availableAt") LocalDateTime availableAt
    );

    /**
     * Count slots by therapist, time after, and status.
     */
    @Query("SELECT COUNT(s) FROM ScheduleSlot s WHERE s.therapist.id = :therapistId " +
           "AND s.availableAt > :after AND s.status = :status")
    long countByTherapistIdAndAvailableAtAfterAndStatus(
        @Param("therapistId") Long therapistId,
        @Param("after") LocalDateTime after,
        @Param("status") SlotStatus status
    );

    /**
     * Find available slots for a therapist, limited.
     * Used for booking page to show next available slots.
     */
    @Query("SELECT s FROM ScheduleSlot s WHERE s.therapist.id = :therapistId " +
           "AND s.availableAt > :after AND s.status = :status ORDER BY s.availableAt")
    List<ScheduleSlot> findAvailableSlots(
        @Param("therapistId") Long therapistId,
        @Param("after") LocalDateTime after,
        @Param("status") SlotStatus status
    );

    /**
     * Find booked slots for upcoming consultations reminder.
     */
    @Query("SELECT s FROM ScheduleSlot s WHERE s.status = :status " +
           "AND s.availableAt BETWEEN :from AND :to")
    List<ScheduleSlot> findBookedSlotsBetween(
        @Param("status") SlotStatus status,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );
}

