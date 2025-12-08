package com.goodhelp.therapist.domain.model;

/**
 * Identifies who sent a chat message.
 */
public enum SenderType {
    /**
     * Message sent by the user (client).
     */
    USER(1),
    
    /**
     * Message sent by the therapist.
     */
    THERAPIST(2);

    private final int value;

    SenderType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SenderType fromValue(int value) {
        for (SenderType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown SenderType value: " + value);
    }
}

