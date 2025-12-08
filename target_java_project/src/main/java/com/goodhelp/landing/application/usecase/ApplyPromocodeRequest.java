package com.goodhelp.landing.application.usecase;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for promocode application.
 */
public record ApplyPromocodeRequest(
    @NotBlank String code,
    @NotNull Long priceId,
    String email
) { }

