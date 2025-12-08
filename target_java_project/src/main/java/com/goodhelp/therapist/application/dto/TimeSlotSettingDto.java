package com.goodhelp.therapist.application.dto;

import java.time.LocalDateTime;

/**
 * DTO for a single time slot in the weekly schedule grid.
 */
public record TimeSlotSettingDto(
    SlotState state,
    long timestamp,
    String time,
    LocalDateTime datetime,
    Long availableAtTimestampUtc
) {
    /**
     * Slot states for the schedule settings grid.
     */
    public enum SlotState {
        UNUSED(1),      // Not set as available
        AVAILABLE(2),   // Available for booking
        BOOKED(3),      // Already booked by a client
        DONE(4),        // Completed consultation
        PASSED(15);     // Time has passed

        private final int code;

        SlotState(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * Check if this slot can be toggled (not passed, not done, not booked).
     */
    public boolean canToggle() {
        return state != SlotState.PASSED && state != SlotState.DONE && state != SlotState.BOOKED;
    }

    /**
     * Check if this slot is marked as available.
     */
    public boolean isAvailable() {
        return state == SlotState.AVAILABLE;
    }

    /**
     * Check if this slot is booked.
     */
    public boolean isBooked() {
        return state == SlotState.BOOKED;
    }

    /**
     * Check if this slot time has passed.
     */
    public boolean isPassed() {
        return state == SlotState.PASSED;
    }

    /**
     * Get CSS class for this slot state.
     */
    public String getCssClass() {
        return switch (state) {
            case AVAILABLE -> "slot-available";
            case BOOKED -> "slot-booked";
            case DONE -> "slot-done";
            case PASSED -> "slot-passed";
            case UNUSED -> "slot-unused";
        };
    }
}

