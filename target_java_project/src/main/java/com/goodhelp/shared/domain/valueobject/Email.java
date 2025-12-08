package com.goodhelp.shared.domain.valueobject;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representing a validated email address.
 * Immutable and self-validating - ensures email is always in a valid state.
 */
@Embeddable
public record Email(String value) {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Compact constructor for validation and normalization.
     */
    public Email {
        Objects.requireNonNull(value, "Email cannot be null");
        value = value.toLowerCase().trim();
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
    }

    /**
     * Factory method for creating Email from potentially null string.
     * 
     * @param value the email string, may be null
     * @return Email instance or null if input is null/blank
     */
    public static Email ofNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new Email(value);
    }

    /**
     * Get the domain part of the email.
     * 
     * @return the domain (part after @)
     */
    public String domain() {
        return value.substring(value.indexOf('@') + 1);
    }

    /**
     * Get the local part of the email.
     * 
     * @return the local part (part before @)
     */
    public String localPart() {
        return value.substring(0, value.indexOf('@'));
    }

    @Override
    public String toString() {
        return value;
    }
}

