package com.goodhelp.therapist.application.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * DTO for therapist dashboard summary data.
 */
public record TherapistDashboardDto(
    int upcomingConsultations,
    int todayConsultations,
    int unreadMessages,
    ZonedDateTime nextConsultation,
    String nextConsultationClientName
) {
    /**
     * Create an empty dashboard (no data).
     */
    public static TherapistDashboardDto empty() {
        return new TherapistDashboardDto(0, 0, 0, null, null);
    }

    /**
     * Check if there are any upcoming consultations.
     */
    public boolean hasUpcomingConsultations() {
        return upcomingConsultations > 0;
    }

    /**
     * Check if there are unread messages.
     */
    public boolean hasUnreadMessages() {
        return unreadMessages > 0;
    }

    /**
     * Check if there are consultations today.
     */
    public boolean hasConsultationsToday() {
        return todayConsultations > 0;
    }
}
