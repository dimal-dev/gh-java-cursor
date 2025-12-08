package com.goodhelp.therapist.application.usecase;

import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.therapist.application.command.UpdateSettingsCommand;
import com.goodhelp.therapist.application.dto.TherapistDto;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistSettings;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import com.goodhelp.therapist.domain.repository.TherapistSettingsRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Use case for updating therapist settings.
 */
@Service
@Validated
@Transactional
public class UpdateSettingsUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateSettingsUseCase.class);

    private final TherapistRepository therapistRepository;
    private final TherapistSettingsRepository settingsRepository;

    public UpdateSettingsUseCase(
            TherapistRepository therapistRepository,
            TherapistSettingsRepository settingsRepository) {
        this.therapistRepository = therapistRepository;
        this.settingsRepository = settingsRepository;
    }

    /**
     * Execute settings update.
     * 
     * @param therapistId the therapist
     * @param command the update command
     * @return updated therapist DTO
     */
    public TherapistDto execute(Long therapistId, @Valid UpdateSettingsCommand command) {
        Therapist therapist = therapistRepository.findById(therapistId)
            .orElseThrow(() -> new ResourceNotFoundException("Therapist", therapistId));

        TherapistSettings settings = settingsRepository.findByTherapistId(therapistId)
            .orElseGet(() -> {
                TherapistSettings newSettings = TherapistSettings.createDefault(therapist);
                therapist.assignSettings(newSettings);
                return newSettings;
            });

        settings.updateTimezone(command.timezone());
        settingsRepository.save(settings);
        
        log.info("Updated settings for therapist {}: timezone={}", therapistId, command.timezone());

        return TherapistDto.fromEntity(therapist);
    }
}
