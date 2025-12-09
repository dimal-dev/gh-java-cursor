package com.goodhelp.landing.application.dto;

/**
 * Order status information for thank you page.
 */
public record OrderStatusDto(
    String status,  // "pending", "approved", "failed"
    String therapistName,
    String consultationDate,
    String consultationTime,
    String errorMessage,  // null unless failed
    String loginUrl,  // Auto-login URL for approved orders
    Integer price  // Price in cents for GA tracking
) {
}

