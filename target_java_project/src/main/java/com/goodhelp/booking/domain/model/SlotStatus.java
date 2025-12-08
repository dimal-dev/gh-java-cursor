package com.goodhelp.booking.domain.model;

import java.util.Arrays;

/**
 * Represents the status of a schedule slot.
 * Controls the booking lifecycle from available through completion.
 */
public enum SlotStatus {
    /**
     * Slot is open and can be booked.
     */
    AVAILABLE(1),
    
    /**
     * Slot has been booked for a consultation.
     */
    BOOKED(2),
    
    /**
     * Slot is blocked by therapist (not available for booking).
     */
    UNAVAILABLE(3),
    
    /**
     * Consultation completed successfully.
     */
    DONE(4),
    
    /**
     * Consultation failed or was cancelled.
     */
    FAILED(5),
    
    /**
     * Slot time has passed without being booked.
     */
    EXPIRED(6);

    private final int value;

    SlotStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Check if slot can be booked.
     */
    public boolean isBookable() {
        return this == AVAILABLE;
    }

    /**
     * Check if slot is currently in use (booked but not completed).
     */
    public boolean isActive() {
        return this == BOOKED;
    }

    /**
     * Check if slot is in a terminal state (cannot change).
     */
    public boolean isTerminal() {
        return this == DONE || this == FAILED || this == EXPIRED;
    }

    /**
     * Check if slot can be released (made available again).
     */
    public boolean canBeReleased() {
        return this == BOOKED || this == UNAVAILABLE;
    }

    /**
     * Convert from database integer value.
     */
    public static SlotStatus fromValue(int value) {
        return Arrays.stream(values())
            .filter(status -> status.value == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Unknown SlotStatus value: " + value
            ));
    }
}
