package com.goodhelp.user.domain.model;

import java.util.Arrays;

/**
 * State of promocode availability.
 */
public enum PromocodeState {
    ACTIVE(1),
    INACTIVE(2);

    private final int value;

    PromocodeState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PromocodeState fromValue(int value) {
        return Arrays.stream(values())
            .filter(state -> state.value == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown promocode state: " + value));
    }
}

