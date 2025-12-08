package com.goodhelp.landing.application.dto;

public record CheckoutSessionDto(
    int dayOfMonth,
    String monthLabel,
    String dayShort,
    String dayFull,
    String time,
    String timezoneLabel,
    String timezoneId
) { }

