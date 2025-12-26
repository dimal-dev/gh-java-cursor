package com.goodhelp.booking.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import com.goodhelp.shared.domain.valueobject.Money;
import com.goodhelp.therapist.domain.model.Therapist;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.Objects;

/**
 * Entity representing a therapist's pricing option.
 * 
 * <p>Each therapist can have multiple price options for:</p>
 * <ul>
 *   <li>Different consultation types (individual vs couple)</li>
 *   <li>Different currencies</li>
 *   <li>Special/promotional pricing (via slug)</li>
 * </ul>
 * 
 * <p>The payRatePercent indicates what percentage of the price
 * goes to the therapist (the rest is platform fee).</p>
 */
@Entity
@Table(name = "therapist_price", indexes = {
    @Index(name = "idx_price_therapist", columnList = "therapist_id"),
    @Index(name = "idx_price_slug", columnList = "slug")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TherapistPrice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;

    /**
     * Price in whole currency units (not cents).
     * For display/backward compatibility with PHP.
     */
    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "type", nullable = false)
    @Convert(converter = PriceTypeConverter.class)
    private PriceType type;

    @Column(name = "state", nullable = false)
    @Convert(converter = PriceStateConverter.class)
    private PriceState state;

    /**
     * Percentage of price that goes to therapist (e.g., 70 = 70%).
     */
    @Column(name = "pay_rate_percent", nullable = false)
    private int payRatePercent;

    /**
     * Optional unique identifier for direct booking links.
     */
    @Column(name = "slug", unique = true)
    private String slug;

    /**
     * Private constructor - use factory methods.
     */
    private TherapistPrice(Therapist therapist, int price, String currency,
                          PriceType type, int payRatePercent) {
        this.therapist = Objects.requireNonNull(therapist, "Therapist is required");
        this.price = validatePrice(price);
        this.currency = validateCurrency(currency);
        this.type = Objects.requireNonNull(type, "Price type is required");
        this.payRatePercent = validatePayRate(payRatePercent);
        this.state = PriceState.CURRENT;
    }

    /**
     * Create a new price option.
     */
    public static TherapistPrice create(Therapist therapist, int price, String currency,
                                        PriceType type, int payRatePercent) {
        return new TherapistPrice(therapist, price, currency, type, payRatePercent);
    }

    // ==================== Query Methods ====================

    /**
     * Get price as Money value object (in cents).
     */
    public Money getMoney() {
        return Money.of(price, currency);
    }

    /**
     * Get price in cents (minor currency units).
     */
    public int getPriceInCents() {
        return price * 100;
    }

    /**
     * Calculate therapist's earnings for this price.
     */
    public Money getTherapistEarnings() {
        return getMoney().percentage(payRatePercent);
    }

    /**
     * Calculate platform fee for this price.
     */
    public Money getPlatformFee() {
        return getMoney().percentage(100 - payRatePercent);
    }

    /**
     * Get consultation duration based on type.
     */
    public int getDurationMinutes() {
        return type.getDurationMinutes();
    }

    /**
     * Check if this price is available for new bookings.
     */
    public boolean isAvailableForBooking() {
        return state.isAvailableForBooking();
    }

    /**
     * Check if this price is publicly visible.
     */
    public boolean isPubliclyVisible() {
        return state.isPubliclyVisible();
    }

    /**
     * Check if this is an individual session price.
     */
    public boolean isIndividual() {
        return type == PriceType.INDIVIDUAL;
    }

    /**
     * Check if this is a couple session price.
     */
    public boolean isCouple() {
        return type == PriceType.COUPLE;
    }

    /**
     * Get display label for this price type.
     */
    public String getTypeLabel() {
        return type.getLabel();
    }

    /**
     * Format price for display.
     */
    public String getFormattedPrice() {
        return getMoney().formatted();
    }

    // ==================== State Modification Methods ====================

    /**
     * Update the price amount.
     */
    public void updatePrice(int newPrice) {
        this.price = validatePrice(newPrice);
    }

    /**
     * Update the pay rate percentage.
     */
    public void updatePayRate(int newPayRatePercent) {
        this.payRatePercent = validatePayRate(newPayRatePercent);
    }

    /**
     * Set a unique slug for direct booking links.
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * Archive this price (mark as past).
     */
    public void archive() {
        this.state = PriceState.PAST;
    }

    /**
     * Hide this price from public listings.
     */
    public void unlist() {
        this.state = PriceState.UNLISTED;
    }

    /**
     * Make this price publicly visible again.
     */
    public void publish() {
        this.state = PriceState.CURRENT;
    }

    // ==================== Validation ====================

    private int validatePrice(int price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive: " + price);
        }
        return price;
    }

    private String validateCurrency(String currency) {
        Objects.requireNonNull(currency, "Currency is required");
        currency = currency.toUpperCase().trim();
        if (currency.length() != 3) {
            throw new IllegalArgumentException("Currency must be 3-letter code: " + currency);
        }
        return currency;
    }

    private int validatePayRate(int payRatePercent) {
        if (payRatePercent < 0 || payRatePercent > 100) {
            throw new IllegalArgumentException(
                "Pay rate must be between 0 and 100: " + payRatePercent
            );
        }
        return payRatePercent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TherapistPrice that = (TherapistPrice) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("TherapistPrice[id=%d, therapist=%d, %d %s, type=%s, state=%s]",
            id, therapist.getId(), price, currency, type, state);
    }
}
