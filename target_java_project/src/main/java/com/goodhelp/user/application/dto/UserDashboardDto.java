package com.goodhelp.user.application.dto;

import java.util.List;

/**
 * DTO for user dashboard data.
 */
public record UserDashboardDto(
    String userName,
    boolean profileComplete,
    boolean rulesRead,
    Integer walletBalance,
    String walletCurrency,
    ConsultationDto nextConsultation,
    List<ConsultationDto> upcomingConsultations,
    Integer unreadMessages,
    String timezone,
    int timezoneOffset,
    String timezoneLabel,
    LatestTherapistDto latestTherapist,
    boolean showSuccessfullyBookedMessage
) {}

