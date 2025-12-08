package com.goodhelp.therapist.application.usecase;

import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.booking.domain.repository.ScheduleSlotRepository;
import com.goodhelp.common.exception.BusinessException;
import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Use case for toggling a schedule slot by datetime.
 * Creates new slots or toggles existing ones between available/unavailable.
 * 
 * This is used by the weekly schedule settings grid where therapists
 * click on time slots to mark them as available or unavailable.
 */
@Service
@Transactional
public class ToggleSlotByTimeUseCase {

    private static final Logger log = LoggerFactory.getLogger(ToggleSlotByTimeUseCase.class);

    // Action constants (matching PHP)
    public static final int ACTION_ADD = 1;
    public static final int ACTION_REMOVE = 2;

    private final TherapistRepository therapistRepository;
    private final ScheduleSlotRepository slotRepository;

    public ToggleSlotByTimeUseCase(
            TherapistRepository therapistRepository,
            ScheduleSlotRepository slotRepository) {
        this.therapistRepository = therapistRepository;
        this.slotRepository = slotRepository;
    }

    /**
     * Command to toggle a slot.
     */
    public record Command(
        Long therapistId,
        LocalDateTime timeInTherapistTz,
        int action
    ) {}

    /**
     * Result of the toggle operation.
     */
    public record Result(boolean succeeded, String message) {
        public static Result ok() {
            return new Result(true, null);
        }

        public static Result failed(String message) {
            return new Result(false, message);
        }
    }

    /**
     * Execute the slot toggle operation.
     */
    public Result execute(Command command) {
        Therapist therapist = therapistRepository.findById(command.therapistId())
            .orElseThrow(() -> new ResourceNotFoundException("Therapist", command.therapistId()));

        // Convert therapist's local time to UTC for storage
        ZonedDateTime localZoned = command.timeInTherapistTz()
            .atZone(ZoneId.of(therapist.getTimezone()));
        LocalDateTime utcTime = localZoned.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

        // Find existing slot
        Optional<ScheduleSlot> existingSlot = slotRepository.findByTherapistIdAndAvailableAt(
            command.therapistId(),
            utcTime
        );

        if (command.action() == ACTION_ADD) {
            return handleAdd(existingSlot, therapist, utcTime);
        } else if (command.action() == ACTION_REMOVE) {
            return handleRemove(existingSlot);
        } else {
            return Result.failed("Invalid action");
        }
    }

    private Result handleAdd(Optional<ScheduleSlot> existingSlot, Therapist therapist, LocalDateTime utcTime) {
        if (existingSlot.isPresent()) {
            ScheduleSlot slot = existingSlot.get();
            if (slot.isUnavailable()) {
                // Reactivate unavailable slot
                slot.markAvailable();
                slotRepository.save(slot);
                log.info("Reactivated slot {} for therapist {}", slot.getId(), therapist.getId());
            }
            // If already available or booked, do nothing
        } else {
            // Create new available slot
            ScheduleSlot newSlot = ScheduleSlot.createAvailable(therapist, utcTime);
            slotRepository.save(newSlot);
            log.info("Created new available slot at {} for therapist {}", utcTime, therapist.getId());
        }
        return Result.ok();
    }

    private Result handleRemove(Optional<ScheduleSlot> existingSlot) {
        if (existingSlot.isEmpty()) {
            // Nothing to remove
            return Result.ok();
        }

        ScheduleSlot slot = existingSlot.get();
        
        if (slot.isBooked()) {
            // Cannot remove booked slots from the grid - must cancel via consultation management
            return Result.failed("Cannot remove booked slot. Use consultation cancellation instead.");
        }

        // Mark as unavailable
        slot.markUnavailable();
        slotRepository.save(slot);
        log.info("Marked slot {} as unavailable", slot.getId());
        
        return Result.ok();
    }
}

