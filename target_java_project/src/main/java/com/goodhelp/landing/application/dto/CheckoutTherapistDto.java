package com.goodhelp.landing.application.dto;

public record CheckoutTherapistDto(
    Long id,
    String fullName,
    String singlePhraseKey,
    String photoUrl,
    String sessionTypeLabelKey,
    int sessionDurationMinutes
) { }

