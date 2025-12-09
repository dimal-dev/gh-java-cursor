package com.goodhelp.user.application.dto;

import java.time.LocalDateTime;

/**
 * DTO for consultation display in dashboard.
 */
public record ConsultationDto(
    Long id,
    String therapistName,
    String therapistPhotoUrl,
    LocalDateTime scheduledAt,
    String scheduledAtFormatted,
    boolean canBeCancelled,
    String status,
    long scheduledAtTimestamp
) {}

