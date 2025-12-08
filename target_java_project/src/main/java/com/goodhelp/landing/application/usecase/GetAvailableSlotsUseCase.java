package com.goodhelp.landing.application.usecase;

import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.booking.domain.model.SlotStatus;
import com.goodhelp.booking.domain.repository.ScheduleSlotRepository;
import com.goodhelp.booking.domain.service.ScheduleDomainService;
import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.common.service.TimezoneHelper;
import com.goodhelp.landing.application.dto.AvailableSlotsDto;
import com.goodhelp.landing.application.dto.AvailableSlotsDto.SlotDayDto;
import com.goodhelp.landing.application.dto.AvailableSlotsDto.SlotTimeDto;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistSettings;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Use case for loading available schedule slots for a therapist in the user's timezone.
 */
@Service
@Transactional(readOnly = true)
public class GetAvailableSlotsUseCase {

    private static final int DEFAULT_LOOKAHEAD_DAYS = 21;

    private final TherapistRepository therapistRepository;
    private final ScheduleSlotRepository slotRepository;
    private final ScheduleDomainService scheduleDomainService;
    private final TimezoneHelper timezoneHelper;

    public GetAvailableSlotsUseCase(
            TherapistRepository therapistRepository,
            ScheduleSlotRepository slotRepository,
            ScheduleDomainService scheduleDomainService,
            TimezoneHelper timezoneHelper) {
        this.therapistRepository = therapistRepository;
        this.slotRepository = slotRepository;
        this.scheduleDomainService = scheduleDomainService;
        this.timezoneHelper = timezoneHelper;
    }

    /**
     * Get available slots for a therapist in provided timezone.
     *
     * @param therapistId therapist identifier
     * @param userTimezone timezone identifier (IANA)
     */
    public AvailableSlotsDto execute(Long therapistId, String userTimezone) {
        Therapist therapist = therapistRepository.findById(therapistId)
            .orElseThrow(() -> new ResourceNotFoundException("Therapist not found"));

        String timezone = resolveTimezone(userTimezone, therapist.getTimezone());
        int timeCapHours = therapist.getSettingsOptional()
            .map(TherapistSettings::getScheduleTimeCapHours)
            .orElse(3);

        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime earliestBookable = nowUtc.plusHours(timeCapHours);
        LocalDateTime latest = earliestBookable.plusDays(DEFAULT_LOOKAHEAD_DAYS);

        List<ScheduleSlot> slots = slotRepository
            .findByTherapistIdAndAvailableAtAfterAndStatus(therapistId, earliestBookable, SlotStatus.AVAILABLE)
            .stream()
            .filter(slot -> slot.getAvailableAt().isBefore(latest))
            .toList();

        List<ScheduleSlot> bookable = scheduleDomainService.filterBookableSlots(slots, nowUtc, timeCapHours);

        Map<LocalDate, List<ScheduleSlot>> grouped = scheduleDomainService.groupSlotsByDate(bookable, timezone);

        Locale locale = LocaleContextHolder.getLocale();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<SlotDayDto> days = grouped.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> toSlotDayDto(entry.getKey(), entry.getValue(), timezone, locale, timeFormatter))
            .toList();

        return new AvailableSlotsDto(days);
    }

    private String resolveTimezone(String userTimezone, String therapistTimezone) {
        if (userTimezone != null && timezoneHelper.isValidTimezone(userTimezone)) {
            return userTimezone;
        }
        if (timezoneHelper.isValidTimezone(therapistTimezone)) {
            return therapistTimezone;
        }
        return TimezoneHelper.DEFAULT_TIMEZONE;
    }

    private SlotDayDto toSlotDayDto(
            LocalDate date,
            List<ScheduleSlot> slots,
            String timezone,
            Locale locale,
            DateTimeFormatter timeFormatter) {

        String dayName = capitalize(date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale));

        List<SlotTimeDto> times = slots.stream()
            .sorted(Comparator.comparing(ScheduleSlot::getAvailableAt))
            .map(slot -> {
                LocalTime localTime = slot.getAvailableAtInZone(timezone)
                    .toLocalTime()
                    .truncatedTo(ChronoUnit.MINUTES);
                return new SlotTimeDto(
                    slot.getId(),
                    localTime,
                    timeFormatter.format(localTime)
                );
            })
            .toList();

        return new SlotDayDto(date, dayName, times);
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }
}

