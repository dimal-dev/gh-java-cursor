package com.goodhelp.user.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

/**
 * Records promocode usage by a user or email.
 */
@Entity
@Table(name = "user_promocode")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPromocode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "promocode_id", nullable = false)
    private Long promocodeId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", nullable = false, length = 500)
    private String email;

    @Column(name = "state", nullable = false)
    @Convert(converter = UserPromocodeStateConverter.class)
    private UserPromocodeState state = UserPromocodeState.APPLIED;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    private UserPromocode(Long promocodeId, String email, Long userId) {
        this.promocodeId = Objects.requireNonNull(promocodeId, "Promocode ID is required");
        this.email = normalizeEmail(email);
        this.userId = userId;
        this.appliedAt = LocalDateTime.now();
        this.state = UserPromocodeState.APPLIED;
    }

    public static UserPromocode applied(Long promocodeId, String email, Long userId) {
        return new UserPromocode(promocodeId, email, userId);
    }

    private String normalizeEmail(String value) {
        Objects.requireNonNull(value, "Email is required");
        return value.trim().toLowerCase(Locale.ROOT);
    }

    public boolean isUsed() {
        return state == UserPromocodeState.USED;
    }
}

