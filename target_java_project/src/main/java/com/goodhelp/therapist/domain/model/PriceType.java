package com.goodhelp.therapist.domain.model;

/**
 * Types of therapy pricing.
 */
public enum PriceType {
    INDIVIDUAL(1, "Individual therapy"),
    COUPLE(2, "Couple therapy"),
    TEENAGER(3, "Teenager therapy");

    private final int code;
    private final String description;

    PriceType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static PriceType fromCode(int code) {
        for (PriceType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PriceType code: " + code);
    }
}

