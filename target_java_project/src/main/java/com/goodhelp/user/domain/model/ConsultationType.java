package com.goodhelp.user.domain.model;

import java.util.Arrays;

/**
 * Enum representing the type of consultation.
 */
public enum ConsultationType {
    INDIVIDUAL(1),
    COUPLE(2);

    private final int value;

    ConsultationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ConsultationType fromValue(int value) {
        return Arrays.stream(values())
            .filter(type -> type.value == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown consultation type value: " + value));
    }

    /**
     * Check if this is an individual consultation.
     */
    public boolean isIndividual() {
        return this == INDIVIDUAL;
    }

    /**
     * Check if this is a couple consultation.
     */
    public boolean isCouple() {
        return this == COUPLE;
    }
}

