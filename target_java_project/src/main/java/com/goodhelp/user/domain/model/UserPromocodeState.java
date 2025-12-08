package com.goodhelp.user.domain.model;

import java.util.Arrays;

/**
 * State of a user's promocode usage.
 */
public enum UserPromocodeState {
    APPLIED(1),
    USED(2);

    private final int value;

    UserPromocodeState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserPromocodeState fromValue(int value) {
        return Arrays.stream(values())
            .filter(state -> state.value == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown user promocode state: " + value));
    }
}

