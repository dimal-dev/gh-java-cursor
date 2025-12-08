package com.goodhelp.landing.application.dto;

/**
 * View model for the checkout page.
 */
public record CheckoutSummaryDto(
    Long checkoutId,
    String slug,
    Long therapistPriceId,
    Long scheduleSlotId,
    String gaClientId,
    CheckoutTherapistDto therapist,
    CheckoutSessionDto session,
    CheckoutPriceDto price,
    CheckoutClientDto client
) {
    public boolean hasDiscount() {
        return price != null && price.hasDiscount();
    }
}

