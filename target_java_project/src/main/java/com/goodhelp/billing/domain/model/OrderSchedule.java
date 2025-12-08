package com.goodhelp.billing.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.time.LocalDateTime;

/**
 * Link between an order and a booked schedule slot.
 */
@Entity
@Table(name = "order_psiholog_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "psiholog_schedule_id", nullable = false)
    private Long scheduleSlotId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private OrderSchedule(Long orderId, Long scheduleSlotId) {
        this.orderId = Objects.requireNonNull(orderId, "Order id is required");
        this.scheduleSlotId = Objects.requireNonNull(scheduleSlotId, "Schedule slot id is required");
    }

    public static OrderSchedule create(Long orderId, Long scheduleSlotId) {
        return new OrderSchedule(orderId, scheduleSlotId);
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

