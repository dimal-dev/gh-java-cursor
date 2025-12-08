package com.goodhelp.landing.application.usecase;

/**
 * Response returned by promocode application endpoint.
 */
public record PromocodeResult(
    boolean valid,
    Integer discountPercent,
    Integer newPrice,
    Long promocodeId,
    String message
) {

    public static PromocodeResult invalid(String message) {
        return new PromocodeResult(false, null, null, null, message);
    }

    public static PromocodeResult valid(Long id, Integer discountPercent, Integer newPrice, String message) {
        return new PromocodeResult(true, discountPercent, newPrice, id, message);
    }
}

