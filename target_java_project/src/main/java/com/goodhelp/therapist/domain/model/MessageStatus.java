package com.goodhelp.therapist.domain.model;

/**
 * Status of a chat message (read/unread).
 */
public enum MessageStatus {
    /**
     * Message has not been read by recipient.
     */
    UNREAD(1),
    
    /**
     * Message has been read by recipient.
     */
    READ(2);

    private final int value;

    MessageStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MessageStatus fromValue(int value) {
        for (MessageStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown MessageStatus value: " + value);
    }
}

