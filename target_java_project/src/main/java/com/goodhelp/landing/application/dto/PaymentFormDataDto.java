package com.goodhelp.landing.application.dto;

/**
 * Data needed to render WayForPay payment form.
 */
public record PaymentFormDataDto(
    String actionUrl,
    String merchantAccount,
    String merchantDomain,
    String orderReference,
    long orderDate,
    String amount,
    String currency,
    String productName,
    String productPrice,
    String returnUrl,
    String serviceUrl,
    String signature
) { }

