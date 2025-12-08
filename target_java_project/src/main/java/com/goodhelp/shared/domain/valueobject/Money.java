package com.goodhelp.shared.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

/**
 * Value Object representing monetary amounts.
 * Stores amount in minor currency units (cents/kopecks) to avoid floating point issues.
 * Immutable with business operations that return new instances.
 */
@Embeddable
public record Money(
    @Column(name = "amount") int amountInCents,
    @Column(name = "currency", length = 3) String currency
) {

    /**
     * Compact constructor for validation.
     */
    public Money {
        if (amountInCents < 0) {
            throw new IllegalArgumentException("Amount cannot be negative: " + amountInCents);
        }
        Objects.requireNonNull(currency, "Currency cannot be null");
        currency = currency.toUpperCase().trim();
        if (currency.length() != 3) {
            throw new IllegalArgumentException("Currency must be a 3-letter code: " + currency);
        }
    }

    /**
     * Create Money from a whole unit amount (e.g., UAH, not kopecks).
     * 
     * @param amount the amount in whole units
     * @param currency the currency code
     * @return Money instance
     */
    public static Money of(int amount, String currency) {
        return new Money(amount * 100, currency);
    }

    /**
     * Create Money from cents/kopecks.
     * 
     * @param cents the amount in minor units
     * @param currency the currency code
     * @return Money instance
     */
    public static Money ofCents(int cents, String currency) {
        return new Money(cents, currency);
    }

    /**
     * Zero money in specified currency.
     */
    public static Money zero(String currency) {
        return new Money(0, currency);
    }

    /**
     * Get amount in whole units (e.g., UAH, not kopecks).
     */
    public int amountInWholeUnits() {
        return amountInCents / 100;
    }

    /**
     * Get the cents/kopecks part.
     */
    public int cents() {
        return amountInCents % 100;
    }

    /**
     * Add two money amounts. Must be same currency.
     */
    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amountInCents + other.amountInCents, this.currency);
    }

    /**
     * Subtract money. Must be same currency. Result cannot be negative.
     */
    public Money subtract(Money other) {
        assertSameCurrency(other);
        int result = this.amountInCents - other.amountInCents;
        if (result < 0) {
            throw new IllegalArgumentException("Cannot subtract: would result in negative amount");
        }
        return new Money(result, this.currency);
    }

    /**
     * Apply a percentage discount.
     * 
     * @param discountPercent discount as integer (e.g., 10 for 10%)
     * @return new Money with discount applied
     */
    public Money applyDiscount(int discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100: " + discountPercent);
        }
        int discounted = amountInCents - (amountInCents * discountPercent / 100);
        return new Money(discounted, currency);
    }

    /**
     * Calculate a percentage of this amount.
     * 
     * @param percent the percentage (e.g., 70 for 70%)
     * @return new Money representing the percentage
     */
    public Money percentage(int percent) {
        if (percent < 0) {
            throw new IllegalArgumentException("Percentage cannot be negative: " + percent);
        }
        return new Money(amountInCents * percent / 100, currency);
    }

    /**
     * Check if this money is greater than another.
     */
    public boolean isGreaterThan(Money other) {
        assertSameCurrency(other);
        return this.amountInCents > other.amountInCents;
    }

    /**
     * Check if this is zero.
     */
    public boolean isZero() {
        return amountInCents == 0;
    }

    /**
     * Format as string with currency symbol.
     */
    public String formatted() {
        String symbol = switch (currency) {
            case "UAH" -> "₴";
            case "USD" -> "$";
            case "EUR" -> "€";
            default -> currency + " ";
        };
        return String.format("%s%d.%02d", symbol, amountInWholeUnits(), cents());
    }

    private void assertSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Currency mismatch: " + this.currency + " vs " + other.currency
            );
        }
    }

    @Override
    public String toString() {
        return formatted();
    }
}

