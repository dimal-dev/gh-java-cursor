package com.goodhelp.therapist.application.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for the weekly schedule settings grid.
 */
public record WeekGridDto(
    List<List<TimeSlotSettingDto>> timeRows,
    List<DayHeaderDto> dayHeaders
) {
    /**
     * Day header for the weekly grid table.
     */
    public record DayHeaderDto(
        LocalDate date,
        String dateLabel,
        String dayLabel
    ) {}
}

