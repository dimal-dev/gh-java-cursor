package com.goodhelp.therapist.application.dto;

import java.util.List;

/**
 * Response for payment history items (AJAX endpoint).
 * Compatible with KTDatatable format used in the frontend.
 */
public record PaymentHistoryResponse(
    List<PaymentHistoryItemDto> data,
    Meta meta
) {

    /**
     * Pagination metadata.
     */
    public record Meta(
        int page,
        int pages,
        int perpage,
        int total
    ) {}

    /**
     * Create response for KTDatatable.
     */
    public static PaymentHistoryResponse of(List<PaymentHistoryItemDto> items, int page, int totalPages, int perPage, int total) {
        return new PaymentHistoryResponse(
            items,
            new Meta(page, totalPages, perPage, total)
        );
    }

    /**
     * Create empty response.
     */
    public static PaymentHistoryResponse empty() {
        return new PaymentHistoryResponse(
            List.of(),
            new Meta(0, 0, 10, 0)
        );
    }
}

