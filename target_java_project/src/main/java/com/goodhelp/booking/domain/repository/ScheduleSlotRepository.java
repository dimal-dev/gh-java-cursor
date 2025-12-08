package com.goodhelp.booking.domain.repository;

import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.booking.domain.model.SlotStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ScheduleSlot.
 * Manages therapist schedule slots for booking.
 */
public interface ScheduleSlotRepository {

    /**
     * Find slot by ID.
     */
    Optional<ScheduleSlot> findById(Long id);

    /**
     * Find all slots for a therapist within a date range.
     */
    List<ScheduleSlot> findByTherapistIdAndAvailableAtBetween(
        Long therapistId, 
        LocalDateTime from, 
        LocalDateTime to
    );

    /**
     * Find slots for a therapist after a certain time with specific status.
     */
    List<ScheduleSlot> findByTherapistIdAndAvailableAtAfterAndStatus(
        Long therapistId, 
        LocalDateTime after, 
        SlotStatus status
    );

    /**
     * Find available slots for a therapist from a given time.
     */
    default List<ScheduleSlot> findAvailableByTherapistIdAfter(Long therapistId, LocalDateTime after) {
        return findByTherapistIdAndAvailableAtAfterAndStatus(therapistId, after, SlotStatus.AVAILABLE);
    }

    /**
     * Find booked slots for a therapist from a given time.
     */
    default List<ScheduleSlot> findBookedByTherapistIdAfter(Long therapistId, LocalDateTime after) {
        return findByTherapistIdAndAvailableAtAfterAndStatus(therapistId, after, SlotStatus.BOOKED);
    }

    /**
     * Find all upcoming slots for a therapist (any status).
     */
    List<ScheduleSlot> findByTherapistIdAndAvailableAtAfter(Long therapistId, LocalDateTime after);

    /**
     * Find slot by therapist and exact time.
     */
    Optional<ScheduleSlot> findByTherapistIdAndAvailableAt(Long therapistId, LocalDateTime availableAt);

    /**
     * Save a slot (create or update).
     */
    ScheduleSlot save(ScheduleSlot slot);

    /**
     * Save multiple slots.
     */
    List<ScheduleSlot> saveAll(List<ScheduleSlot> slots);

    /**
     * Delete a slot.
     */
    void delete(ScheduleSlot slot);

    /**
     * Count available slots for a therapist from a given time.
     */
    long countByTherapistIdAndAvailableAtAfterAndStatus(
        Long therapistId, 
        LocalDateTime after, 
        SlotStatus status
    );
}
