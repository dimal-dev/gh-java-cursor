package com.goodhelp.therapist.domain.model;

import java.util.Arrays;

/**
 * Represents the role of a therapist in the system.
 * Maps to database integer values for compatibility with existing data.
 */
public enum TherapistRole {
    /**
     * Regular therapist providing consultations.
     */
    THERAPIST(1),
    
    /**
     * Test therapist for development and QA purposes.
     */
    TEST_THERAPIST(2);

    private final int value;

    TherapistRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Get the Spring Security role authority string.
     */
    public String getAuthority() {
        return "ROLE_" + name();
    }

    /**
     * Convert from database integer value.
     * 
     * @param value database value
     * @return corresponding enum
     * @throws IllegalArgumentException if value is unknown
     */
    public static TherapistRole fromValue(int value) {
        return Arrays.stream(values())
            .filter(role -> role.value == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Unknown TherapistRole value: " + value
            ));
    }

    /**
     * Check if this is a real therapist (not test).
     */
    public boolean isRealTherapist() {
        return this == THERAPIST;
    }
}

