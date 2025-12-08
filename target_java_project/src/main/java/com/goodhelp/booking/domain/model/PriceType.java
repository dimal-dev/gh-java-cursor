package com.goodhelp.booking.domain.model;

import java.util.Arrays;

/**
 * Represents the type of consultation and its corresponding duration.
 * Individual sessions are shorter than couple sessions.
 */
public enum PriceType {
    /**
     * Individual therapy session (50 minutes).
     */
    INDIVIDUAL(1, 50),
    
    /**
     * Couple therapy session (80 minutes).
     */
    COUPLE(2, 80);

    private final int value;
    private final int durationMinutes;

    PriceType(int value, int durationMinutes) {
        this.value = value;
        this.durationMinutes = durationMinutes;
    }

    public int getValue() {
        return value;
    }

    /**
     * Get the standard duration for this consultation type in minutes.
     */
    public int getDurationMinutes() {
        return durationMinutes;
    }

    /**
     * Get display label for this type.
     */
    public String getLabel() {
        return switch (this) {
            case INDIVIDUAL -> "Individual";
            case COUPLE -> "Couple";
        };
    }

    /**
     * Get message key for i18n.
     */
    public String getMessageKey() {
        return "price.type." + name().toLowerCase();
    }

    /**
     * Convert from database integer value.
     */
    public static PriceType fromValue(int value) {
        return Arrays.stream(values())
            .filter(type -> type.value == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Unknown PriceType value: " + value
            ));
    }
}
