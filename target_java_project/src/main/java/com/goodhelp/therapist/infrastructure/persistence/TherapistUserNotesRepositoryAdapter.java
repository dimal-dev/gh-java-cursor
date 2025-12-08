package com.goodhelp.therapist.infrastructure.persistence;

import com.goodhelp.therapist.domain.model.TherapistUserNotes;
import com.goodhelp.therapist.domain.repository.TherapistUserNotesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the domain TherapistUserNotesRepository interface
 * using Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TherapistUserNotesRepositoryAdapter implements TherapistUserNotesRepository {

    private final JpaTherapistUserNotesRepository jpaRepository;

    @Override
    public Optional<TherapistUserNotes> findByTherapistIdAndUserId(Long therapistId, Long userId) {
        return jpaRepository.findByTherapistIdAndUserId(therapistId, userId);
    }

    @Override
    public List<TherapistUserNotes> findByTherapistId(Long therapistId) {
        return jpaRepository.findByTherapistId(therapistId);
    }

    @Override
    @Transactional
    public TherapistUserNotes save(TherapistUserNotes notes) {
        return jpaRepository.save(notes);
    }

    @Override
    @Transactional
    public void delete(TherapistUserNotes notes) {
        jpaRepository.delete(notes);
    }
}

