package com.goodhelp.therapist.application.query;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Query to retrieve payment history for a therapist.
 */
public record GetPaymentsHistoryQuery(
    @NotNull(message = "Therapist ID is required")
    Long therapistId,
    
    @PositiveOrZero(message = "Page must be non-negative")
    int page,
    
    @Positive(message = "Size must be positive")
    int size
) {
    private static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Create query with default pagination.
     */
    public static GetPaymentsHistoryQuery defaultQuery(Long therapistId) {
        return new GetPaymentsHistoryQuery(therapistId, 0, DEFAULT_PAGE_SIZE);
    }
}
