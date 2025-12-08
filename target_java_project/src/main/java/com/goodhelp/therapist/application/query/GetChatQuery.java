package com.goodhelp.therapist.application.query;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Query to retrieve chat messages between therapist and a user.
 */
public record GetChatQuery(
    @NotNull(message = "Therapist ID is required")
    Long therapistId,
    
    @NotNull(message = "User ID is required")
    Long userId,
    
    @PositiveOrZero(message = "Page must be non-negative")
    int page,
    
    @Positive(message = "Size must be positive")
    int size
) {
    private static final int DEFAULT_PAGE_SIZE = 50;

    /**
     * Create query with default pagination.
     */
    public static GetChatQuery defaultQuery(Long therapistId, Long userId) {
        return new GetChatQuery(therapistId, userId, 0, DEFAULT_PAGE_SIZE);
    }
}
