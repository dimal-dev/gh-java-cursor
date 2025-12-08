package com.goodhelp.therapist.application.dto;

/**
 * Response DTO for unread messages count AJAX request.
 */
public record UnreadCountResponse(
    int amount
) {}

