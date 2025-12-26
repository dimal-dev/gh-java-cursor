package com.goodhelp.booking.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import com.goodhelp.therapist.domain.model.Therapist;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Entity representing a time slot in a therapist's schedule.
 * 
 * <p>A schedule slot represents a specific hour that can be:</p>
 * <ul>
 *   <li>Available - open for booking</li>
 *   <li>Booked - reserved for a consultation</li>
 *   <li>Unavailable - blocked by therapist</li>
 *   <li>Done/Failed/Expired - terminal states</li>
 * </ul>
 * 
 * <p>Business rules:</p>
 * <ul>
 *   <li>Times are stored in UTC</li>
 *   <li>Only available slots can be booked</li>
 *   <li>Booked slots can be cancelled (released)</li>
 *   <li>Past slots cannot be modified</li>
 * </ul>
 */
@Entity
@Table(name = "therapist_schedule", indexes = {
    @Index(name = "idx_schedule_therapist_time", columnList = "therapist_id, available_at"),
    @Index(name = "idx_schedule_state", columnList = "state")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleSlot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;

    @Column(name = "available_at", nullable = false)
    private LocalDateTime availableAt;

    @Column(name = "state", nullable = false)
    @Convert(converter = SlotStatusConverter.class)
    private SlotStatus status;

    /**
     * Private constructor - use factory methods.
     */
    private ScheduleSlot(Therapist therapist, LocalDateTime availableAt, SlotStatus status) {
        this.therapist = Objects.requireNonNull(therapist, "Therapist is required");
        this.availableAt = Objects.requireNonNull(availableAt, "Available time is required");
        this.status = Objects.requireNonNull(status, "Status is required");
    }

    /**
     * Create an available slot for a therapist.
     */
    public static ScheduleSlot createAvailable(Therapist therapist, LocalDateTime availableAt) {
        return new ScheduleSlot(therapist, availableAt, SlotStatus.AVAILABLE);
    }

    /**
     * Create an unavailable (blocked) slot.
     */
    public static ScheduleSlot createUnavailable(Therapist therapist, LocalDateTime availableAt) {
        return new ScheduleSlot(therapist, availableAt, SlotStatus.UNAVAILABLE);
    }

    // ==================== Query Methods ====================

    /**
     * Check if this slot can be booked.
     */
    public boolean isBookable() {
        return status.isBookable() && !isPast();
    }

    /**
     * Check if this slot is currently available.
     */
    public boolean isAvailable() {
        return status == SlotStatus.AVAILABLE;
    }

    /**
     * Check if this slot is booked.
     */
    public boolean isBooked() {
        return status == SlotStatus.BOOKED;
    }

    /**
     * Check if this slot is unavailable (blocked).
     */
    public boolean isUnavailable() {
        return status == SlotStatus.UNAVAILABLE;
    }

    /**
     * Check if this slot's time has passed.
     */
    public boolean isPast() {
        return availableAt.isBefore(LocalDateTime.now());
    }

    /**
     * Check if this slot is in the future.
     */
    public boolean isFuture() {
        return availableAt.isAfter(LocalDateTime.now());
    }

    /**
     * Check if this slot starts within the given hours from now.
     */
    public boolean startsWithinHours(int hours) {
        LocalDateTime now = LocalDateTime.now();
        return availableAt.isAfter(now) && availableAt.isBefore(now.plusHours(hours));
    }

    /**
     * Get the available time in a specific timezone.
     */
    public ZonedDateTime getAvailableAtInZone(String timezone) {
        return availableAt.atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ZoneId.of(timezone));
    }

    /**
     * Get the available time in therapist's timezone.
     */
    public ZonedDateTime getAvailableAtInTherapistZone() {
        return getAvailableAtInZone(therapist.getTimezone());
    }

    // ==================== State Transition Methods ====================

    /**
     * Book this slot for a consultation.
     * 
     * @throws IllegalStateException if slot cannot be booked
     */
    public void book() {
        if (!isBookable()) {
            throw new IllegalStateException(
                "Cannot book slot " + id + ": status=" + status + ", isPast=" + isPast()
            );
        }
        this.status = SlotStatus.BOOKED;
    }

    /**
     * Release a booked slot back to available.
     * Used when a consultation is cancelled.
     * 
     * @throws IllegalStateException if slot cannot be released
     */
    public void release() {
        if (!status.canBeReleased()) {
            throw new IllegalStateException(
                "Cannot release slot " + id + ": status=" + status
            );
        }
        this.status = SlotStatus.AVAILABLE;
    }

    /**
     * Mark slot as unavailable (blocked by therapist).
     * 
     * @throws IllegalStateException if slot is already booked
     */
    public void markUnavailable() {
        if (status == SlotStatus.BOOKED) {
            throw new IllegalStateException(
                "Cannot mark booked slot as unavailable: " + id
            );
        }
        this.status = SlotStatus.UNAVAILABLE;
    }

    /**
     * Mark slot as available.
     */
    public void markAvailable() {
        if (status.isTerminal()) {
            throw new IllegalStateException(
                "Cannot mark terminal slot as available: " + id + ", status=" + status
            );
        }
        this.status = SlotStatus.AVAILABLE;
    }

    /**
     * Mark consultation as completed successfully.
     * 
     * @throws IllegalStateException if slot is not booked
     */
    public void markDone() {
        if (status != SlotStatus.BOOKED) {
            throw new IllegalStateException(
                "Cannot mark non-booked slot as done: " + id + ", status=" + status
            );
        }
        this.status = SlotStatus.DONE;
    }

    /**
     * Mark consultation as failed.
     * 
     * @throws IllegalStateException if slot is not booked
     */
    public void markFailed() {
        if (status != SlotStatus.BOOKED) {
            throw new IllegalStateException(
                "Cannot mark non-booked slot as failed: " + id + ", status=" + status
            );
        }
        this.status = SlotStatus.FAILED;
    }

    /**
     * Mark available slot as expired (time passed without booking).
     */
    public void expire() {
        if (status == SlotStatus.AVAILABLE) {
            this.status = SlotStatus.EXPIRED;
        }
    }

    // ==================== Toggle Method (for UI) ====================

    /**
     * Toggle availability state (used by schedule settings UI).
     * Switches between AVAILABLE and UNAVAILABLE.
     * 
     * @return true if slot is now available, false if unavailable
     * @throws IllegalStateException if slot cannot be toggled
     */
    public boolean toggleAvailability() {
        if (status == SlotStatus.AVAILABLE) {
            this.status = SlotStatus.UNAVAILABLE;
            return false;
        } else if (status == SlotStatus.UNAVAILABLE) {
            this.status = SlotStatus.AVAILABLE;
            return true;
        } else {
            throw new IllegalStateException(
                "Cannot toggle slot with status: " + status
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleSlot that = (ScheduleSlot) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("ScheduleSlot[id=%d, therapist=%d, at=%s, status=%s]",
            id, therapist.getId(), availableAt, status);
    }
}
