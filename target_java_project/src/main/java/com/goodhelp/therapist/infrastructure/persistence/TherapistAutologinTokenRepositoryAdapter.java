package com.goodhelp.therapist.infrastructure.persistence;

import com.goodhelp.therapist.domain.model.TherapistAutologinToken;
import com.goodhelp.therapist.domain.repository.TherapistAutologinTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Adapter implementing the domain TherapistAutologinTokenRepository interface
 * using Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TherapistAutologinTokenRepositoryAdapter implements TherapistAutologinTokenRepository {

    private final JpaTherapistAutologinTokenRepository jpaRepository;

    @Override
    public Optional<TherapistAutologinToken> findByToken(String token) {
        return jpaRepository.findByToken(token);
    }

    @Override
    public Optional<TherapistAutologinToken> findByTherapistId(Long therapistId) {
        return jpaRepository.findByTherapistId(therapistId);
    }

    @Override
    @Transactional
    public TherapistAutologinToken save(TherapistAutologinToken token) {
        return jpaRepository.save(token);
    }

    @Override
    @Transactional
    public void deleteByTherapistId(Long therapistId) {
        jpaRepository.deleteByTherapistId(therapistId);
    }
}

