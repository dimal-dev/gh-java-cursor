package com.goodhelp.landing.application.dto;

public record CheckoutClientDto(
    String name,
    String email,
    String phone,
    String authType
) { }

