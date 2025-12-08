package com.goodhelp.billing.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Checkout session created after booking form submission.
 * Stores user contact info, selected slot and price, and applied promocode.
 */
@Entity
@Table(name = "billing_checkout", indexes = {
    @Index(name = "idx_billing_checkout_slug", columnList = "slug", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Checkout extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slug", nullable = false, unique = true, length = 32)
    private String slug;

    @Column(name = "psiholog_price_id", nullable = false)
    private Long therapistPriceId;

    @Column(name = "psiholog_schedule_id", nullable = false)
    private Long scheduleSlotId;

    @Column(name = "user_promocode_id")
    private Long userPromocodeId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "auth_type")
    private String authType;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email", nullable = false, length = 500)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "ga_client_id", length = 50)
    private String gaClientId;

    @Column(name = "ga_client_id_original", length = 100)
    private String gaClientIdOriginal;

    private Checkout(String slug,
                     Long therapistPriceId,
                     Long scheduleSlotId,
                     Long userPromocodeId,
                     Long userId,
                     String authType,
                     String phone,
                     String email,
                     String name,
                     String gaClientId,
                     String gaClientIdOriginal) {
        this.slug = Objects.requireNonNull(slug, "Checkout slug is required");
        this.therapistPriceId = Objects.requireNonNull(therapistPriceId, "Therapist price id is required");
        this.scheduleSlotId = Objects.requireNonNull(scheduleSlotId, "Schedule slot id is required");
        this.userPromocodeId = userPromocodeId;
        this.userId = userId;
        this.authType = authType;
        this.phone = phone;
        this.email = Objects.requireNonNull(email, "Email is required");
        this.name = name;
        this.gaClientId = gaClientId;
        this.gaClientIdOriginal = gaClientIdOriginal;
    }

    public static Checkout create(String slug,
                                  Long therapistPriceId,
                                  Long scheduleSlotId,
                                  Long userPromocodeId,
                                  Long userId,
                                  String authType,
                                  String phone,
                                  String email,
                                  String name,
                                  String gaClientId,
                                  String gaClientIdOriginal) {
        return new Checkout(
            slug,
            therapistPriceId,
            scheduleSlotId,
            userPromocodeId,
            userId,
            authType,
            phone,
            email,
            name,
            gaClientId,
            gaClientIdOriginal
        );
    }

    public boolean hasPromocode() {
        return userPromocodeId != null;
    }
}

