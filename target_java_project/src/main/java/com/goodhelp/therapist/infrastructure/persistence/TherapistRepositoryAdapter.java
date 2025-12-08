package com.goodhelp.therapist.infrastructure.persistence;

import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistRole;
import com.goodhelp.therapist.domain.model.TherapistStatus;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the domain TherapistRepository interface
 * using Spring Data JPA.
 * 
 * This class bridges the domain layer's repository interface with
 * the infrastructure layer's JPA implementation.
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TherapistRepositoryAdapter implements TherapistRepository {

    private final JpaTherapistRepository jpaRepository;

    @Override
    public Optional<Therapist> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Therapist> findByEmail(String email) {
        return jpaRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public List<Therapist> findAllByStatus(TherapistStatus status) {
        return jpaRepository.findByStatus(status);
    }

    @Override
    public List<Therapist> findAllByStatusAndRoleIn(TherapistStatus status, List<TherapistRole> roles) {
        return jpaRepository.findByStatusAndRoleIn(status, roles);
    }

    @Override
    @Transactional
    public Therapist save(Therapist therapist) {
        return jpaRepository.save(therapist);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}

