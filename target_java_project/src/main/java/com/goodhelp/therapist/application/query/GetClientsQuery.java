package com.goodhelp.therapist.application.query;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Query to retrieve a paginated list of clients for a therapist.
 */
public record GetClientsQuery(
    @NotNull(message = "Therapist ID is required")
    Long therapistId,
    
    @PositiveOrZero(message = "Page must be non-negative")
    int page,
    
    @Positive(message = "Size must be positive")
    int size,
    
    String search
) {
    private static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Create query with default pagination.
     */
    public static GetClientsQuery defaultQuery(Long therapistId) {
        return new GetClientsQuery(therapistId, 0, DEFAULT_PAGE_SIZE, null);
    }

    /**
     * Create query with search term.
     */
    public static GetClientsQuery withSearch(Long therapistId, String search) {
        return new GetClientsQuery(therapistId, 0, DEFAULT_PAGE_SIZE, search);
    }

    /**
     * Check if this query has a search term.
     */
    public boolean hasSearch() {
        return search != null && !search.isBlank();
    }
}
