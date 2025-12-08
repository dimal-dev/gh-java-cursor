package com.goodhelp.billing.domain.model;

/**
 * Lifecycle states for a billing order.
 */
public enum OrderState {
    CREATED(1),
    PENDING(2),
    APPROVED(3),
    FAILED(4);

    private final int value;

    OrderState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static OrderState fromValue(int value) {
        for (OrderState state : values()) {
            if (state.value == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown order state: " + value);
    }
}

