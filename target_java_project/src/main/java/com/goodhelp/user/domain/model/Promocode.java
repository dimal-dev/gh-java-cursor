package com.goodhelp.user.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Promocode definition.
 */
@Entity
@Table(name = "promocode")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Promocode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @Column(name = "state", nullable = false)
    @Convert(converter = PromocodeStateConverter.class)
    private PromocodeState state = PromocodeState.ACTIVE;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @Column(name = "max_use_number")
    private Integer maxUseNumber = 1;

    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    public boolean isActive() {
        return state == PromocodeState.ACTIVE;
    }

    public boolean isExpired(LocalDateTime now) {
        return expireAt != null && expireAt.isBefore(now);
    }

    public boolean canBeUsed(String email, int alreadyUsed) {
        if (!isActive()) {
            return false;
        }
        if (expireAt != null && expireAt.isBefore(LocalDateTime.now())) {
            return false;
        }
        if (maxUseNumber == null || maxUseNumber < 1) {
            return true;
        }
        return alreadyUsed < maxUseNumber;
    }

    public int applyDiscount(int price) {
        Objects.requireNonNull(discountPercent, "Discount percent is required");
        int discount = Math.max(0, Math.min(discountPercent, 100));
        return Math.max(0, price - (price * discount / 100));
    }
}

