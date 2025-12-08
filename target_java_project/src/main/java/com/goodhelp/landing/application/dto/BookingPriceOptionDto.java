package com.goodhelp.landing.application.dto;

/**
 * Price option displayed on the book consultation page.
 */
public record BookingPriceOptionDto(
    Long id,
    String type,               // e.g. "individual", "couple"
    String typeLabelKey,       // message key to render the label
    int durationMinutes,
    int price,
    String currency,
    String slug
) {

    public String formattedPrice() {
        return price + " \u20b4";
    }
}

