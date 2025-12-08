package com.goodhelp.therapist.application.usecase;

import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.booking.domain.model.SlotStatus;
import com.goodhelp.booking.domain.repository.ScheduleSlotRepository;
import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.therapist.application.dto.TimeSlotSettingDto;
import com.goodhelp.therapist.application.dto.TimeSlotSettingDto.SlotState;
import com.goodhelp.therapist.application.dto.WeekGridDto;
import com.goodhelp.therapist.application.dto.WeekGridDto.DayHeaderDto;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Use case for getting the weekly schedule settings grid.
 * Returns a grid of time slots (48 per day - every 30 minutes) for 7 days.
 */
@Service
@Transactional(readOnly = true)
public class GetWeekScheduleSettingsUseCase {

    private static final int SLOTS_PER_DAY = 48; // 24 hours * 2 (30-minute slots)
    private static final int DAYS_IN_WEEK = 7;
    private static final int SLOT_DURATION_MINUTES = 30;

    private final TherapistRepository therapistRepository;
    private final ScheduleSlotRepository slotRepository;

    public GetWeekScheduleSettingsUseCase(
            TherapistRepository therapistRepository,
            ScheduleSlotRepository slotRepository) {
        this.therapistRepository = therapistRepository;
        this.slotRepository = slotRepository;
    }

    /**
     * Query for getting week schedule.
     */
    public record Query(Long therapistId, LocalDate mondayDate) {}

    /**
     * Execute the use case to get the weekly grid.
     */
    public WeekGridDto execute(Query query) {
        Therapist therapist = therapistRepository.findById(query.therapistId())
            .orElseThrow(() -> new ResourceNotFoundException("Therapist", query.therapistId()));

        String therapistTimezone = therapist.getTimezone();
        ZoneId therapistZone = ZoneId.of(therapistTimezone);

        LocalDateTime mondayStart = query.mondayDate().atStartOfDay();
        LocalDateTime sundayEnd = query.mondayDate().plusDays(7).atStartOfDay();

        // Convert therapist's local time to UTC for database query
        ZonedDateTime mondayUtc = mondayStart.atZone(therapistZone).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime sundayUtc = sundayEnd.atZone(therapistZone).withZoneSameInstant(ZoneId.of("UTC"));

        // Fetch existing slots from database
        List<ScheduleSlot> existingSlots = slotRepository.findByTherapistIdAndAvailableAtBetween(
            query.therapistId(),
            mondayUtc.toLocalDateTime(),
            sundayUtc.toLocalDateTime()
        );

        // Index slots by their local datetime for quick lookup
        Map<LocalDateTime, ScheduleSlot> slotsByTime = new HashMap<>();
        for (ScheduleSlot slot : existingSlots) {
            ZonedDateTime localTime = slot.getAvailableAt()
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(therapistZone);
            slotsByTime.put(localTime.toLocalDateTime(), slot);
        }

        // Current time in therapist's timezone
        ZonedDateTime nowLocal = ZonedDateTime.now(therapistZone);

        // Build the grid
        List<List<TimeSlotSettingDto>> timeRows = new ArrayList<>();
        for (int halfHour = 0; halfHour < SLOTS_PER_DAY; halfHour++) {
            List<TimeSlotSettingDto> row = new ArrayList<>();
            for (int day = 0; day < DAYS_IN_WEEK; day++) {
                LocalDateTime slotTime = mondayStart
                    .plusDays(day)
                    .plusMinutes((long) halfHour * SLOT_DURATION_MINUTES);

                TimeSlotSettingDto dto = createSlotDto(
                    slotTime, 
                    slotsByTime.get(slotTime), 
                    nowLocal.toLocalDateTime(),
                    therapistZone
                );
                row.add(dto);
            }
            timeRows.add(row);
        }

        // Build day headers
        List<DayHeaderDto> dayHeaders = new ArrayList<>();
        for (int day = 0; day < DAYS_IN_WEEK; day++) {
            LocalDate date = query.mondayDate().plusDays(day);
            dayHeaders.add(new DayHeaderDto(
                date,
                formatDateLabel(date),
                date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("uk"))
            ));
        }

        return new WeekGridDto(timeRows, dayHeaders);
    }

    private TimeSlotSettingDto createSlotDto(
            LocalDateTime slotTime,
            ScheduleSlot existingSlot,
            LocalDateTime nowLocal,
            ZoneId therapistZone) {

        long timestamp = slotTime.atZone(therapistZone).toEpochSecond();
        String timeLabel = slotTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        Long timestampUtc = null;

        SlotState state;
        if (slotTime.isBefore(nowLocal)) {
            state = SlotState.PASSED;
        } else if (existingSlot == null) {
            state = SlotState.UNUSED;
        } else {
            SlotStatus slotStatus = existingSlot.getStatus();
            if (slotStatus == SlotStatus.AVAILABLE) {
                state = SlotState.AVAILABLE;
            } else if (slotStatus == SlotStatus.BOOKED) {
                state = SlotState.BOOKED;
                timestampUtc = existingSlot.getAvailableAt()
                    .atZone(ZoneId.of("UTC"))
                    .toEpochSecond();
            } else if (slotStatus == SlotStatus.DONE) {
                state = SlotState.DONE;
            } else {
                state = SlotState.UNUSED;
            }
        }

        return new TimeSlotSettingDto(state, timestamp, timeLabel, slotTime, timestampUtc);
    }

    private String formatDateLabel(LocalDate date) {
        int day = date.getDayOfMonth();
        String month = getMonthNameInclined(date.getMonthValue());
        return day + " " + month;
    }

    private String getMonthNameInclined(int month) {
        return switch (month) {
            case 1 -> "січ";
            case 2 -> "лют";
            case 3 -> "бер";
            case 4 -> "кві";
            case 5 -> "тра";
            case 6 -> "чер";
            case 7 -> "лип";
            case 8 -> "сер";
            case 9 -> "вер";
            case 10 -> "жов";
            case 11 -> "лис";
            case 12 -> "гру";
            default -> "";
        };
    }
}

