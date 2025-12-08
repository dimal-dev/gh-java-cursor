package com.goodhelp.shared.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Value Object representing a time slot with start time and duration.
 * Used for scheduling consultations - ensures time logic is encapsulated.
 */
@Embeddable
public record TimeSlot(
    @Column(name = "start_time") LocalDateTime startTime,
    @Column(name = "duration_minutes") int durationMinutes
) {

    /**
     * Compact constructor for validation.
     */
    public TimeSlot {
        Objects.requireNonNull(startTime, "Start time cannot be null");
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be positive: " + durationMinutes);
        }
    }

    /**
     * Create a time slot with specified duration in minutes.
     */
    public static TimeSlot of(LocalDateTime startTime, int durationMinutes) {
        return new TimeSlot(startTime, durationMinutes);
    }

    /**
     * Create a 50-minute individual session slot.
     */
    public static TimeSlot individualSession(LocalDateTime startTime) {
        return new TimeSlot(startTime, 50);
    }

    /**
     * Create an 80-minute couple session slot.
     */
    public static TimeSlot coupleSession(LocalDateTime startTime) {
        return new TimeSlot(startTime, 80);
    }

    /**
     * Get duration as Duration object.
     */
    public Duration duration() {
        return Duration.ofMinutes(durationMinutes);
    }

    /**
     * Calculate end time.
     */
    public LocalDateTime endTime() {
        return startTime.plusMinutes(durationMinutes);
    }

    /**
     * Check if this time slot overlaps with another.
     * Two slots overlap if one starts before the other ends.
     */
    public boolean overlaps(TimeSlot other) {
        return this.startTime.isBefore(other.endTime()) && 
               other.startTime.isBefore(this.endTime());
    }

    /**
     * Check if this slot contains a specific time.
     */
    public boolean contains(LocalDateTime time) {
        return !time.isBefore(startTime) && time.isBefore(endTime());
    }

    /**
     * Check if this slot is in the past relative to given time.
     */
    public boolean isPast(LocalDateTime now) {
        return startTime.isBefore(now);
    }

    /**
     * Check if this slot is in the future relative to given time.
     */
    public boolean isFuture(LocalDateTime now) {
        return startTime.isAfter(now);
    }

    /**
     * Check if this slot starts within the given hours from now.
     */
    public boolean startsWithinHours(LocalDateTime now, int hours) {
        return startTime.isAfter(now) && startTime.isBefore(now.plusHours(hours));
    }

    /**
     * Convert start time to a specific timezone for display.
     */
    public ZonedDateTime startTimeInZone(String timezone) {
        return startTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of(timezone));
    }

    /**
     * Convert end time to a specific timezone for display.
     */
    public ZonedDateTime endTimeInZone(String timezone) {
        return endTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of(timezone));
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d min)", startTime, endTime(), durationMinutes);
    }
}

