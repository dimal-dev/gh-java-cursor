package com.goodhelp.therapist.application.usecase;

import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.booking.domain.repository.ScheduleSlotRepository;
import com.goodhelp.common.exception.BusinessException;
import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.therapist.application.command.ToggleSlotCommand;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Use case for toggling a schedule slot between available and unavailable.
 */
@Service
@Validated
@Transactional
public class ToggleSlotUseCase {

    private static final Logger log = LoggerFactory.getLogger(ToggleSlotUseCase.class);

    private final ScheduleSlotRepository slotRepository;

    public ToggleSlotUseCase(ScheduleSlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    /**
     * Result of toggle operation.
     */
    public record Result(Long slotId, boolean isNowAvailable, String status) {}

    /**
     * Execute slot toggle.
     * 
     * @param therapistId the therapist who owns the slot (for authorization)
     * @param command the toggle command
     * @return result with new slot status
     * @throws ResourceNotFoundException if slot not found
     * @throws BusinessException if slot cannot be toggled
     */
    public Result execute(Long therapistId, @Valid ToggleSlotCommand command) {
        ScheduleSlot slot = slotRepository.findById(command.slotId())
            .orElseThrow(() -> new ResourceNotFoundException("ScheduleSlot", command.slotId()));

        // Authorization check
        if (!slot.getTherapist().getId().equals(therapistId)) {
            throw new BusinessException("Not authorized to modify this slot");
        }

        try {
            boolean isAvailable = slot.toggleAvailability();
            slotRepository.save(slot);
            
            log.info("Toggled slot {} to {}", slot.getId(), slot.getStatus());
            
            return new Result(slot.getId(), isAvailable, slot.getStatus().name());
        } catch (IllegalStateException e) {
            throw new BusinessException("Cannot toggle slot: " + e.getMessage());
        }
    }
}
