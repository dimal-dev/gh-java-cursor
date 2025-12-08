package com.goodhelp.therapist.application.query;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Query to retrieve schedule slots for a therapist within a date range.
 */
public record GetScheduleQuery(
    @NotNull(message = "Therapist ID is required")
    Long therapistId,
    
    @NotNull(message = "Start date is required")
    LocalDate from,
    
    @NotNull(message = "End date is required")
    LocalDate to
) {
    /**
     * Create query for a single week starting from the given date.
     */
    public static GetScheduleQuery forWeek(Long therapistId, LocalDate weekStart) {
        return new GetScheduleQuery(therapistId, weekStart, weekStart.plusDays(6));
    }

    /**
     * Create query for the current week.
     */
    public static GetScheduleQuery currentWeek(Long therapistId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        return forWeek(therapistId, weekStart);
    }
}
