package com.goodhelp.therapist.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.Objects;

/**
 * Entity representing a therapist's pricing option.
 * Each therapist can have multiple prices (individual, couple, etc.).
 */
@Entity
@Table(name = "psiholog_price", indexes = {
    @Index(name = "idx_psiholog_price_psiholog", columnList = "psiholog_id"),
    @Index(name = "idx_psiholog_price_slug", columnList = "slug")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TherapistPrice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "psiholog_id", nullable = false)
    private Long therapistId;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "slug")
    private String slug;

    @Column(name = "currency", nullable = false)
    private String currency = "UAH";

    @Column(name = "type", nullable = false)
    @Convert(converter = PriceTypeConverter.class)
    private PriceType type = PriceType.INDIVIDUAL;

    @Column(name = "state", nullable = false)
    @Convert(converter = PriceStateConverter.class)
    private PriceState state = PriceState.ACTIVE;

    @Column(name = "pay_rate_percent", nullable = false)
    private Integer payRatePercent = 70;

    /**
     * Private constructor - use factory method.
     */
    private TherapistPrice(Long therapistId, Integer price, PriceType type, String slug) {
        this.therapistId = Objects.requireNonNull(therapistId, "Therapist ID is required");
        this.price = Objects.requireNonNull(price, "Price is required");
        this.type = Objects.requireNonNull(type, "Price type is required");
        this.slug = slug;
        this.currency = "UAH";
        this.state = PriceState.ACTIVE;
    }

    /**
     * Create a new price.
     */
    public static TherapistPrice create(Long therapistId, Integer price, PriceType type, String slug) {
        return new TherapistPrice(therapistId, price, type, slug);
    }

    /**
     * Check if this price is currently active.
     */
    public boolean isActive() {
        return state == PriceState.ACTIVE;
    }

    /**
     * Get formatted price string (e.g., "500 ₴").
     */
    public String getFormattedPrice() {
        return price + " ₴";
    }

    /**
     * Get the amount that therapist earns (based on pay rate).
     */
    public Integer getTherapistEarnings() {
        return price * payRatePercent / 100;
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
        return String.format("TherapistPrice[id=%d, therapistId=%d, price=%d %s, type=%s]",
            id, therapistId, price, currency, type);
    }
}

