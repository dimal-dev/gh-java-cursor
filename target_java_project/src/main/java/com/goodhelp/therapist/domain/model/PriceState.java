package com.goodhelp.therapist.domain.model;

/**
 * State of a price entry.
 */
public enum PriceState {
    ACTIVE(1),
    INACTIVE(0);

    private final int code;

    PriceState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PriceState fromCode(int code) {
        for (PriceState state : values()) {
            if (state.code == code) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown PriceState code: " + code);
    }
}

