package com.goodhelp.therapist.application.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * DTO for week information in schedule settings.
 */
public record WeekInfoDto(
    LocalDate monday,
    LocalDate sunday,
    String startDateLabel,
    String endDateLabel,
    String startDateFull
) {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Create week info for a given week offset (0 = current week, 1 = next week, etc.)
     */
    public static WeekInfoDto forWeekOffset(int weekOffset, LocalDate today) {
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .plusWeeks(weekOffset);
        LocalDate sunday = monday.plusDays(6);
        
        return new WeekInfoDto(
            monday,
            sunday,
            formatDateLabel(monday),
            formatDateLabel(sunday),
            monday.atStartOfDay().format(DATE_FORMATTER)
        );
    }

    private static String formatDateLabel(LocalDate date) {
        int day = date.getDayOfMonth();
        String month = getMonthNameInclined(date.getMonthValue());
        return day + " " + month;
    }

    private static String getMonthNameInclined(int month) {
        return switch (month) {
            case 1 -> "січня";
            case 2 -> "лютого";
            case 3 -> "березня";
            case 4 -> "квітня";
            case 5 -> "травня";
            case 6 -> "червня";
            case 7 -> "липня";
            case 8 -> "серпня";
            case 9 -> "вересня";
            case 10 -> "жовтня";
            case 11 -> "листопада";
            case 12 -> "грудня";
            default -> "";
        };
    }
}

