package com.goodhelp.landing.application.dto;

public record CheckoutPriceDto(
    int basePrice,
    int finalPrice,
    String currency,
    Integer discountPercent,
    String promocodeCode
) {
    public boolean hasDiscount() {
        return discountPercent != null && discountPercent > 0 && finalPrice < basePrice;
    }

    public String currencySymbol() {
        return "UAH".equalsIgnoreCase(currency) ? "₴" : currency;
    }
}

