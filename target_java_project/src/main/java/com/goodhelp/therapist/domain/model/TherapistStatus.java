package com.goodhelp.therapist.domain.model;

import java.util.Arrays;

/**
 * Represents the current status of a therapist account.
 * Controls whether the therapist can accept consultations.
 */
public enum TherapistStatus {
    /**
     * Therapist is active and can accept consultations.
     */
    ACTIVE(1),
    
    /**
     * Therapist is inactive (disabled their account).
     */
    INACTIVE(2),
    
    /**
     * Therapist is suspended by administration.
     */
    SUSPENDED(3),
    
    /**
     * Therapist is pending approval (new registration).
     */
    PENDING_APPROVAL(4);

    private final int value;

    TherapistStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Check if therapist can accept new consultations.
     */
    public boolean canAcceptConsultations() {
        return this == ACTIVE;
    }

    /**
     * Convert from database integer value.
     * 
     * @param value database value
     * @return corresponding enum
     * @throws IllegalArgumentException if value is unknown
     */
    public static TherapistStatus fromValue(int value) {
        return Arrays.stream(values())
            .filter(status -> status.value == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Unknown TherapistStatus value: " + value
            ));
    }
}

