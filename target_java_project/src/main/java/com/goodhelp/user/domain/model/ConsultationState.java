package com.goodhelp.user.domain.model;

import java.util.Arrays;

/**
 * Enum representing the state of a user consultation.
 * 
 * <p>Consultations can be in various states:</p>
 * <ul>
 *   <li>CREATED - Consultation is booked and active</li>
 *   <li>COMPLETED - Consultation has been completed</li>
 *   <li>CANCELLED_BY_USER_IN_TIME - User cancelled with 24+ hours notice</li>
 *   <li>CANCELLED_BY_USER_NOT_IN_TIME - User cancelled with less than 24 hours notice</li>
 *   <li>CANCELLED_BY_PSIHOLOG_IN_TIME - Therapist cancelled with 24+ hours notice</li>
 *   <li>CANCELLED_BY_PSIHOLOG_NOT_IN_TIME - Therapist cancelled with less than 24 hours notice</li>
 * </ul>
 */
public enum ConsultationState {
    CREATED(1),
    COMPLETED(2),
    CANCELLED_BY_USER_IN_TIME(5),
    CANCELLED_BY_USER_NOT_IN_TIME(6),
    CANCELLED_BY_PSIHOLOG_IN_TIME(7),
    CANCELLED_BY_PSIHOLOG_NOT_IN_TIME(8);

    private final int value;

    ConsultationState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ConsultationState fromValue(int value) {
        return Arrays.stream(values())
            .filter(state -> state.value == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown consultation state value: " + value));
    }

    /**
     * Check if this state represents an active consultation.
     */
    public boolean isActive() {
        return this == CREATED;
    }

    /**
     * Check if this state represents a completed consultation.
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /**
     * Check if this state represents a cancelled consultation.
     */
    public boolean isCancelled() {
        return this == CANCELLED_BY_USER_IN_TIME ||
               this == CANCELLED_BY_USER_NOT_IN_TIME ||
               this == CANCELLED_BY_PSIHOLOG_IN_TIME ||
               this == CANCELLED_BY_PSIHOLOG_NOT_IN_TIME;
    }

    /**
     * Check if this state represents a cancellation by the user.
     */
    public boolean isCancelledByUser() {
        return this == CANCELLED_BY_USER_IN_TIME ||
               this == CANCELLED_BY_USER_NOT_IN_TIME;
    }

    /**
     * Check if this state represents a cancellation by the therapist.
     */
    public boolean isCancelledByTherapist() {
        return this == CANCELLED_BY_PSIHOLOG_IN_TIME ||
               this == CANCELLED_BY_PSIHOLOG_NOT_IN_TIME;
    }

    /**
     * Check if this cancellation was done in time (24+ hours notice).
     */
    public boolean isCancelledInTime() {
        return this == CANCELLED_BY_USER_IN_TIME ||
               this == CANCELLED_BY_PSIHOLOG_IN_TIME;
    }

    /**
     * Check if this cancellation was done not in time (less than 24 hours notice).
     */
    public boolean isCancelledNotInTime() {
        return this == CANCELLED_BY_USER_NOT_IN_TIME ||
               this == CANCELLED_BY_PSIHOLOG_NOT_IN_TIME;
    }
}

