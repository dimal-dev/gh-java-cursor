package com.goodhelp.booking.domain.model;

import java.util.Arrays;

/**
 * Represents the state of a price option.
 * Controls whether a price is visible and usable for new bookings.
 */
public enum PriceState {
    /**
     * Active price, visible and usable for new bookings.
     */
    CURRENT(1),
    
    /**
     * Historical price, no longer offered but exists for records.
     */
    PAST(2),
    
    /**
     * Hidden price, not publicly visible but still valid.
     */
    UNLISTED(3);

    private final int value;

    PriceState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Check if this price is available for new bookings.
     */
    public boolean isAvailableForBooking() {
        return this == CURRENT || this == UNLISTED;
    }

    /**
     * Check if this price should be displayed publicly.
     */
    public boolean isPubliclyVisible() {
        return this == CURRENT;
    }

    /**
     * Convert from database integer value.
     */
    public static PriceState fromValue(int value) {
        return Arrays.stream(values())
            .filter(state -> state.value == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Unknown PriceState value: " + value
            ));
    }
}
