package com.goodhelp.therapist.domain.model;

import java.util.Arrays;

/**
 * Represents the sex/gender of a therapist for profile display.
 * Used primarily for proper pronoun usage in UI and communications.
 */
public enum Sex {
    FEMALE(1),
    MALE(2);

    private final int value;

    Sex(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Check if female (for pronoun selection in templates).
     */
    public boolean isFemale() {
        return this == FEMALE;
    }

    /**
     * Check if male (for pronoun selection in templates).
     */
    public boolean isMale() {
        return this == MALE;
    }

    /**
     * Convert from database integer value.
     * 
     * @param value database value
     * @return corresponding enum
     * @throws IllegalArgumentException if value is unknown
     */
    public static Sex fromValue(int value) {
        return Arrays.stream(values())
            .filter(sex -> sex.value == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Unknown Sex value: " + value
            ));
    }
}

