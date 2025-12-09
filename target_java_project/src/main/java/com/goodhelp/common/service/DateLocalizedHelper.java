package com.goodhelp.common.service;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Helper service for localized date formatting.
 * Migrated from PHP: App\Common\Service\DateLocalizedHelper
 */
@Service
public class DateLocalizedHelper {
    
    private final MessageSource messageSource;
    
    // Week day keys for translation
    private static final String[] WEEKDAY_KEYS = {
        "", // 0 - not used (days start from 1)
        "weekday.monday",
        "weekday.tuesday",
        "weekday.wednesday",
        "weekday.thursday",
        "weekday.friday",
        "weekday.saturday",
        "weekday.sunday"
    };
    
    private static final String[] WEEKDAY_SHORT_KEYS = {
        "",
        "weekday.short.monday",
        "weekday.short.tuesday",
        "weekday.short.wednesday",
        "weekday.short.thursday",
        "weekday.short.friday",
        "weekday.short.saturday",
        "weekday.short.sunday"
    };
    
    private static final String[] MONTH_KEYS = {
        "", // 0 - not used
        "month.january",
        "month.february",
        "month.march",
        "month.april",
        "month.may",
        "month.june",
        "month.july",
        "month.august",
        "month.september",
        "month.october",
        "month.november",
        "month.december"
    };
    
    private static final String[] MONTH_INCLINED_KEYS = {
        "",
        "month.inclined.january",
        "month.inclined.february",
        "month.inclined.march",
        "month.inclined.april",
        "month.inclined.may",
        "month.inclined.june",
        "month.inclined.july",
        "month.inclined.august",
        "month.inclined.september",
        "month.inclined.october",
        "month.inclined.november",
        "month.inclined.december"
    };
    
    public DateLocalizedHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    public String getWeekDayNameByNumber(int dayOfWeek, String localeCode) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("Day of week must be between 1 and 7");
        }
        return messageSource.getMessage(WEEKDAY_KEYS[dayOfWeek], null, getLocale(localeCode));
    }
    
    public String getWeekDayShortNameByNumber(int dayOfWeek, String localeCode) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("Day of week must be between 1 and 7");
        }
        return messageSource.getMessage(WEEKDAY_SHORT_KEYS[dayOfWeek], null, getLocale(localeCode));
    }
    
    public String getMonthNameByNumber(int month, String localeCode) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        return messageSource.getMessage(MONTH_KEYS[month], null, getLocale(localeCode));
    }
    
    public String getMonthNameByNumberInclined(int month, String localeCode) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        return messageSource.getMessage(MONTH_INCLINED_KEYS[month], null, getLocale(localeCode));
    }
    
    /**
     * Format a datetime in a "good looking" format.
     * Format: "{Weekday} на {time}, {day} {Month}"
     * Example: "Понедельник на 14:30, 15 Января" (Monday at 14:30, 15 January)
     * 
     * @param dateTime the datetime to format
     * @param localeCode locale code (ua, ru, en)
     * @return formatted string
     */
    public String getDateTimeGoodLookingLabel(LocalDateTime dateTime, String localeCode) {
        DateTimeGoodLookingParts parts = getDateTimeGoodLookingParts(dateTime, localeCode);
        // Format: "{Weekday} на {time}, {day} {Month}"
        return String.format("%s на %s, %d %s",
            parts.dayOfWeekFull(),
            parts.time(),
            parts.dayOfMonth(),
            parts.month()
        );
    }
    
    /**
     * Get parts of a datetime formatted for display.
     * 
     * @param dateTime the datetime to format
     * @param localeCode locale code (ua, ru, en)
     * @return parts object with time, dayOfWeekFull, dayOfMonth, month, year
     */
    public DateTimeGoodLookingParts getDateTimeGoodLookingParts(LocalDateTime dateTime, String localeCode) {
        int dayOfWeekNumber = dateTime.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
        int dayOfMonth = dateTime.getDayOfMonth();
        String dayOfWeekFull = getWeekDayNameByNumber(dayOfWeekNumber, localeCode);
        String month = getMonthNameByNumberInclined(dateTime.getMonthValue(), localeCode);
        String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        int year = dateTime.getYear();
        
        return new DateTimeGoodLookingParts(time, dayOfWeekFull, dayOfMonth, month, year);
    }
    
    /**
     * Record for datetime parts.
     */
    public record DateTimeGoodLookingParts(
        String time,
        String dayOfWeekFull,
        int dayOfMonth,
        String month,
        int year
    ) {}
    
    private Locale getLocale(String localeCode) {
        if (localeCode == null || localeCode.isEmpty()) {
            return Locale.ENGLISH;
        }
        // Map "ua" to "uk" (Ukrainian language code)
        if ("ua".equalsIgnoreCase(localeCode)) {
            localeCode = "uk";
        }
        return Locale.forLanguageTag(localeCode);
    }
}
