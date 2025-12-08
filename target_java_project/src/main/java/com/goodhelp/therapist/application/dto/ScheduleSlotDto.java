package com.goodhelp.therapist.application.dto;

import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.booking.domain.model.SlotStatus;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * DTO for schedule slot data.
 * Used to transfer slot information to the presentation layer.
 */
public record ScheduleSlotDto(
    Long id,
    LocalDateTime startTimeUtc,
    ZonedDateTime startTimeLocal,
    ZonedDateTime endTimeLocal,
    String status,
    boolean isBookable,
    boolean isPast,
    String clientName,
    Long consultationId
) {
    /**
     * Create DTO from domain entity (available slot).
     */
    public static ScheduleSlotDto fromEntity(ScheduleSlot slot, String timezone) {
        ZonedDateTime localStart = slot.getAvailableAtInZone(timezone);
        // Assuming 1 hour duration for schedule display
        ZonedDateTime localEnd = localStart.plusHours(1);
        
        return new ScheduleSlotDto(
            slot.getId(),
            slot.getAvailableAt(),
            localStart,
            localEnd,
            slot.getStatus().name(),
            slot.isBookable(),
            slot.isPast(),
            null,
            null
        );
    }

    /**
     * Create DTO from domain entity with consultation info.
     */
    public static ScheduleSlotDto fromEntityWithConsultation(
            ScheduleSlot slot, 
            String timezone,
            String clientName,
            Long consultationId) {
        
        ZonedDateTime localStart = slot.getAvailableAtInZone(timezone);
        ZonedDateTime localEnd = localStart.plusHours(1);
        
        return new ScheduleSlotDto(
            slot.getId(),
            slot.getAvailableAt(),
            localStart,
            localEnd,
            slot.getStatus().name(),
            false, // booked slots are not bookable
            slot.isPast(),
            clientName,
            consultationId
        );
    }

    /**
     * Check if this slot is available for booking.
     */
    public boolean isAvailable() {
        return SlotStatus.AVAILABLE.name().equals(status);
    }

    /**
     * Check if this slot is booked.
     */
    public boolean isBooked() {
        return SlotStatus.BOOKED.name().equals(status);
    }

    /**
     * Check if this slot is unavailable (blocked).
     */
    public boolean isUnavailable() {
        return SlotStatus.UNAVAILABLE.name().equals(status);
    }
}
