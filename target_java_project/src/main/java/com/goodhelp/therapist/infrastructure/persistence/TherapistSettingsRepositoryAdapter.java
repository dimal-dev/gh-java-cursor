package com.goodhelp.therapist.infrastructure.persistence;

import com.goodhelp.therapist.domain.model.TherapistSettings;
import com.goodhelp.therapist.domain.repository.TherapistSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Adapter implementing the domain TherapistSettingsRepository interface
 * using Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TherapistSettingsRepositoryAdapter implements TherapistSettingsRepository {

    private final JpaTherapistSettingsRepository jpaRepository;

    @Override
    public Optional<TherapistSettings> findByTherapistId(Long therapistId) {
        return jpaRepository.findByTherapistId(therapistId);
    }

    @Override
    @Transactional
    public TherapistSettings save(TherapistSettings settings) {
        return jpaRepository.save(settings);
    }
}

