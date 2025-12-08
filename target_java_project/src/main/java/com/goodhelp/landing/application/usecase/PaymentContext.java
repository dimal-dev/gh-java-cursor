package com.goodhelp.landing.application.usecase;

import java.util.Locale;

/**
 * Contextual data required to prepare a payment form.
 */
public record PaymentContext(
    Locale locale,
    String baseUrl,
    String returnPath,
    String servicePath,
    String requestCookies
) { }

