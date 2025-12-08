package com.goodhelp.landing.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO hierarchy for displaying available schedule slots on the booking page.
 */
public record AvailableSlotsDto(List<SlotDayDto> days) {

    public boolean hasSlots() {
        return days != null && !days.isEmpty();
    }

    public record SlotDayDto(
        LocalDate date,
        String dayName,
        List<SlotTimeDto> times
    ) { }

    public record SlotTimeDto(
        Long id,
        LocalTime time,
        String formatted
    ) { }
}

