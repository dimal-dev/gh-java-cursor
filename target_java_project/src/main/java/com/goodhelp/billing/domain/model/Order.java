package com.goodhelp.billing.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Payment order created for a checkout session.
 * Stores immutable snapshot of price, currency and customer contact data.
 */
@Entity
@Table(name = "billing_order", indexes = {
    @Index(name = "idx_billing_order_checkout_slug", columnList = "checkout_slug"),
    @Index(name = "idx_billing_order_user", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "billing_checkout_id")
    private Long checkoutId;

    @Column(name = "state", nullable = false)
    @Convert(converter = OrderStateConverter.class)
    private OrderState state = OrderState.CREATED;

    @Column(name = "ga_client_id", length = 50)
    private String gaClientId;

    @Column(name = "request_cookies")
    private String requestCookies;

    @Column(name = "checkout_slug", nullable = false, length = 32)
    private String checkoutSlug;

    /**
     * Price stored in minor units (cents).
     */
    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    @Column(name = "billing_product_id", nullable = false)
    private Integer billingProductId = 0;

    @Column(name = "psiholog_price_id")
    private Long therapistPriceId;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "locale", length = 10)
    private String locale;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "date_created", nullable = false)
    private LocalDateTime dateCreated;

    private Order(String checkoutSlug,
                  Integer price,
                  String currency,
                  Long therapistPriceId,
                  Long checkoutId,
                  Long userId,
                  String phone,
                  String email,
                  String clientName,
                  String timezone,
                  String locale,
                  String gaClientId,
                  String requestCookies) {
        this.checkoutSlug = Objects.requireNonNull(checkoutSlug, "Order reference is required");
        this.price = Objects.requireNonNull(price, "Price (cents) is required");
        this.currency = Objects.requireNonNull(currency, "Currency is required");
        this.therapistPriceId = therapistPriceId;
        this.checkoutId = checkoutId;
        this.userId = userId;
        this.phone = phone;
        this.email = email;
        this.clientName = clientName;
        this.timezone = timezone;
        this.locale = locale;
        this.gaClientId = gaClientId;
        this.requestCookies = requestCookies;
        this.dateCreated = LocalDateTime.now();
        this.state = OrderState.CREATED;
        this.billingProductId = 0;
    }

    public static Order create(String checkoutSlug,
                               Integer price,
                               String currency,
                               Long therapistPriceId,
                               Long checkoutId,
                               Long userId,
                               String phone,
                               String email,
                               String clientName,
                               String timezone,
                               String locale,
                               String gaClientId,
                               String requestCookies) {
        return new Order(
            checkoutSlug,
            price,
            currency,
            therapistPriceId,
            checkoutId,
            userId,
            phone,
            email,
            clientName,
            timezone,
            locale,
            gaClientId,
            requestCookies
        );
    }
}

