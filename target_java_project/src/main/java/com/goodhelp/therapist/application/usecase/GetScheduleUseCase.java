package com.goodhelp.therapist.application.usecase;

import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.booking.domain.repository.ScheduleSlotRepository;
import com.goodhelp.booking.domain.service.ScheduleDomainService;
import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.therapist.application.dto.ScheduleSlotDto;
import com.goodhelp.therapist.application.query.GetScheduleQuery;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Use case for retrieving therapist schedule.
 */
@Service
@Validated
@Transactional(readOnly = true)
public class GetScheduleUseCase {

    private final TherapistRepository therapistRepository;
    private final ScheduleSlotRepository slotRepository;
    private final ScheduleDomainService scheduleDomainService;

    public GetScheduleUseCase(
            TherapistRepository therapistRepository,
            ScheduleSlotRepository slotRepository,
            ScheduleDomainService scheduleDomainService) {
        this.therapistRepository = therapistRepository;
        this.slotRepository = slotRepository;
        this.scheduleDomainService = scheduleDomainService;
    }

    /**
     * Result containing schedule data organized by date.
     */
    public record Result(
        Map<LocalDate, List<ScheduleSlotDto>> slotsByDate,
        int totalSlots,
        int availableSlots,
        int bookedSlots
    ) {}

    /**
     * Execute schedule retrieval.
     */
    public Result execute(@Valid GetScheduleQuery query) {
        Therapist therapist = therapistRepository.findById(query.therapistId())
            .orElseThrow(() -> new ResourceNotFoundException("Therapist", query.therapistId()));

        String timezone = therapist.getTimezone();
        
        // Convert dates to datetime range
        LocalDateTime from = query.from().atStartOfDay();
        LocalDateTime to = query.to().atTime(LocalTime.MAX);

        // Fetch slots
        List<ScheduleSlot> slots = slotRepository.findByTherapistIdAndAvailableAtBetween(
            query.therapistId(), from, to
        );

        // Convert to DTOs
        List<ScheduleSlotDto> slotDtos = slots.stream()
            .map(slot -> ScheduleSlotDto.fromEntity(slot, timezone))
            .toList();

        // Group by date
        Map<LocalDate, List<ScheduleSlotDto>> slotsByDate = slotDtos.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                dto -> dto.startTimeLocal().toLocalDate()
            ));

        // Calculate stats
        int available = (int) slots.stream().filter(ScheduleSlot::isAvailable).count();
        int booked = (int) slots.stream().filter(ScheduleSlot::isBooked).count();

        return new Result(slotsByDate, slots.size(), available, booked);
    }
}
