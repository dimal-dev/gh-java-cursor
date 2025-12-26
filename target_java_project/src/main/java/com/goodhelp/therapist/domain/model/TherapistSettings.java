package com.goodhelp.therapist.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

import java.time.ZoneId;
import java.util.Objects;

/**
 * Entity representing a therapist's personal settings.
 * Belongs to the Therapist aggregate but stored separately for performance.
 * 
 * <p>Settings include:</p>
 * <ul>
 *   <li>Timezone for schedule display</li>
 *   <li>Telegram integration for notifications</li>
 *   <li>Schedule time cap (minimum booking advance)</li>
 * </ul>
 */
@Entity
@Table(name = "therapist_settings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TherapistSettings extends BaseEntity {

    private static final String DEFAULT_TIMEZONE = "Europe/Kiev";
    private static final String DEFAULT_SCHEDULE_TIME_CAP = "+3 hour";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timezone", nullable = false)
    private String timezone = DEFAULT_TIMEZONE;

    @Column(name = "telegram_chat_id")
    private String telegramChatId;

    @Column(name = "schedule_time_cap", nullable = false)
    private String scheduleTimeCap = DEFAULT_SCHEDULE_TIME_CAP;

    @Setter(AccessLevel.PACKAGE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;

    /**
     * Private constructor - use factory methods.
     */
    private TherapistSettings(Therapist therapist, String timezone) {
        this.therapist = Objects.requireNonNull(therapist, "Therapist is required");
        this.timezone = validateTimezone(timezone);
    }

    /**
     * Create settings with default values.
     */
    public static TherapistSettings createDefault(Therapist therapist) {
        return new TherapistSettings(therapist, DEFAULT_TIMEZONE);
    }

    /**
     * Create settings with specific timezone.
     */
    public static TherapistSettings create(Therapist therapist, String timezone) {
        return new TherapistSettings(therapist, timezone);
    }

    // ==================== Business Methods ====================

    /**
     * Get timezone as ZoneId for date/time operations.
     */
    public ZoneId getZoneId() {
        return ZoneId.of(timezone);
    }

    /**
     * Update the timezone.
     * 
     * @param newTimezone valid timezone ID (e.g., "Europe/Kiev")
     * @throws IllegalArgumentException if timezone is invalid
     */
    public void updateTimezone(String newTimezone) {
        this.timezone = validateTimezone(newTimezone);
    }

    /**
     * Link Telegram for notifications.
     * 
     * @param chatId the Telegram chat ID
     */
    public void linkTelegram(String chatId) {
        Objects.requireNonNull(chatId, "Chat ID is required");
        this.telegramChatId = chatId;
    }

    /**
     * Unlink Telegram.
     */
    public void unlinkTelegram() {
        this.telegramChatId = null;
    }

    /**
     * Check if Telegram is linked.
     */
    public boolean isTelegramLinked() {
        return telegramChatId != null && !telegramChatId.isBlank();
    }

    /**
     * Update the schedule time cap (minimum booking advance).
     * 
     * @param timeCap time expression like "+3 hour" or "+24 hour"
     */
    public void updateScheduleTimeCap(String timeCap) {
        Objects.requireNonNull(timeCap, "Time cap is required");
        this.scheduleTimeCap = timeCap;
    }

    /**
     * Parse schedule time cap to hours.
     * Supports format like "+3 hour", "+24 hour", etc.
     * 
     * @return number of hours for minimum booking advance
     */
    public int getScheduleTimeCapHours() {
        if (scheduleTimeCap == null || scheduleTimeCap.isBlank()) {
            return 3; // default
        }
        // Parse "+3 hour" format
        String cleaned = scheduleTimeCap.replace("+", "").replace("hour", "").trim();
        try {
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return 3; // default on parse error
        }
    }

    // ==================== Validation ====================

    private String validateTimezone(String timezone) {
        if (timezone == null || timezone.isBlank()) {
            return DEFAULT_TIMEZONE;
        }
        try {
            ZoneId.of(timezone);
            return timezone;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timezone: " + timezone, e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TherapistSettings that = (TherapistSettings) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
