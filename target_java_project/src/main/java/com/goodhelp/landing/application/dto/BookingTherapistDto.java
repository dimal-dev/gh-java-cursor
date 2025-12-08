package com.goodhelp.landing.application.dto;

import java.util.List;
import java.util.Optional;

/**
 * Therapist summary used on the book consultation page.
 */
public record BookingTherapistDto(
    Long id,
    String fullName,
    String singlePhraseKey,
    String photoUrl,
    List<BookingPriceOptionDto> prices,
    Long defaultPriceId
) {

    public Optional<BookingPriceOptionDto> priceById(Long priceId) {
        if (priceId == null) {
            return Optional.empty();
        }
        return prices.stream()
            .filter(p -> priceId.equals(p.id()))
            .findFirst();
    }
}

