package com.goodhelp.therapist.application.dto;

import java.math.BigDecimal;

/**
 * DTO for payments overview summary.
 * Contains aggregated payment information for the current payout period.
 */
public record PaymentsSummaryDto(
    BigDecimal totalPendingPayout,
    String currency,
    String periodStart,
    String periodEnd,
    String nextPayoutDate,
    int completedConsultationsCount,
    java.util.List<PayoutItemDto> items
) {

    /**
     * Individual payout item (completed consultation).
     */
    public record PayoutItemDto(
        Long consultationId,
        String clientName,
        String dateTime,
        BigDecimal amount,
        String status
    ) {}

    /**
     * Create an empty summary (no pending payouts).
     */
    public static PaymentsSummaryDto empty(String currency) {
        return new PaymentsSummaryDto(
            BigDecimal.ZERO,
            currency,
            null,
            null,
            null,
            0,
            java.util.List.of()
        );
    }
}

