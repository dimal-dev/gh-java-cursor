package com.goodhelp.therapist.application.usecase;

import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.therapist.application.command.SaveUserNotesCommand;
import com.goodhelp.therapist.application.dto.UserNotesDto;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistUserNotes;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import com.goodhelp.therapist.domain.repository.TherapistUserNotesRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * Use case for saving therapist's private notes about a client.
 */
@Service
@Validated
@Transactional
public class SaveUserNotesUseCase {

    private static final Logger log = LoggerFactory.getLogger(SaveUserNotesUseCase.class);

    private final TherapistRepository therapistRepository;
    private final TherapistUserNotesRepository notesRepository;

    public SaveUserNotesUseCase(
            TherapistRepository therapistRepository,
            TherapistUserNotesRepository notesRepository) {
        this.therapistRepository = therapistRepository;
        this.notesRepository = notesRepository;
    }

    /**
     * Execute save notes operation.
     * 
     * @param therapistId the therapist saving notes
     * @param command the save command
     * @return the saved notes DTO
     */
    public UserNotesDto execute(Long therapistId, @Valid SaveUserNotesCommand command) {
        Therapist therapist = therapistRepository.findById(therapistId)
            .orElseThrow(() -> new ResourceNotFoundException("Therapist", therapistId));

        Optional<TherapistUserNotes> existingNotes = notesRepository
            .findByTherapistIdAndUserId(therapistId, command.userId());

        TherapistUserNotes notes;
        if (existingNotes.isPresent()) {
            // Update existing notes
            notes = existingNotes.get();
            if (command.notes() != null) {
                notes.updateNotes(command.notes());
            }
            if (command.clientName() != null) {
                notes.updateClientName(command.clientName());
            }
        } else {
            // Create new notes
            notes = TherapistUserNotes.create(
                therapist, 
                command.userId(),
                command.clientName()
            );
            notes.updateNotes(command.notes() != null ? command.notes() : "");
        }

        TherapistUserNotes saved = notesRepository.save(notes);
        log.info("Saved notes for therapist {} about user {}", therapistId, command.userId());

        return UserNotesDto.fromEntity(saved);
    }
}
