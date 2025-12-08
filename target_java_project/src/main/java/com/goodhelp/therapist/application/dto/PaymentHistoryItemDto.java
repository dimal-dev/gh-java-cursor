package com.goodhelp.therapist.application.dto;

import java.time.LocalDateTime;

/**
 * DTO for payment history item.
 */
public record PaymentHistoryItemDto(
    Long consultationId,
    LocalDateTime consultationDate,
    String clientName,
    String consultationType,
    int priceAmount,
    String currency,
    int earnings,
    String status
) {
    /**
     * Get formatted price.
     */
    public String getFormattedPrice() {
        return String.format("%d %s", priceAmount, currency);
    }

    /**
     * Get formatted earnings.
     */
    public String getFormattedEarnings() {
        return String.format("%d %s", earnings, currency);
    }
}
