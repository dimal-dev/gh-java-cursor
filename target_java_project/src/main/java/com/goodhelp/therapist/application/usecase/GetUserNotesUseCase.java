package com.goodhelp.therapist.application.usecase;

import com.goodhelp.therapist.application.dto.UserNotesDto;
import com.goodhelp.therapist.domain.repository.TherapistUserNotesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for retrieving therapist's notes about a client.
 */
@Service
@Transactional(readOnly = true)
public class GetUserNotesUseCase {

    private final TherapistUserNotesRepository notesRepository;

    public GetUserNotesUseCase(TherapistUserNotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    /**
     * Get notes for a specific user.
     * 
     * @param therapistId the therapist
     * @param userId the user to get notes for
     * @param clientName default client name if notes don't exist
     * @return notes DTO (empty if none exist)
     */
    public UserNotesDto execute(Long therapistId, Long userId, String clientName) {
        return notesRepository.findByTherapistIdAndUserId(therapistId, userId)
            .map(UserNotesDto::fromEntity)
            .orElse(UserNotesDto.empty(userId, clientName));
    }
}
