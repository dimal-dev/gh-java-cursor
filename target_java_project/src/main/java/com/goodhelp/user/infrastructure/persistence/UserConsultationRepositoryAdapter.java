package com.goodhelp.user.infrastructure.persistence;

import com.goodhelp.user.domain.model.ConsultationState;
import com.goodhelp.user.domain.model.UserConsultation;
import com.goodhelp.user.domain.repository.UserConsultationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the domain UserConsultationRepository interface
 * using Spring Data JPA.
 * 
 * <p>This class bridges the domain layer's repository interface with
 * the infrastructure layer's JPA implementation.</p>
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserConsultationRepositoryAdapter implements UserConsultationRepository {

    private final JpaUserConsultationRepository jpaRepository;

    @Override
    public Optional<UserConsultation> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<UserConsultation> findUpcomingByUserId(Long userId) {
        return jpaRepository.findUpcomingByUserId(
            userId,
            ConsultationState.CREATED.getValue(),
            LocalDateTime.now()
        );
    }

    @Override
    public List<UserConsultation> findUpcomingByUserIdOrderByStartTime(Long userId) {
        // The JPA query already orders by start time, so we can reuse it
        return findUpcomingByUserId(userId);
    }

    @Override
    public List<UserConsultation> findByUserIdAndTherapistId(Long userId, Long therapistId) {
        return jpaRepository.findByUserIdAndTherapistIdOrderByCreatedAtDesc(userId, therapistId);
    }

    @Override
    public Optional<UserConsultation> findClosestUpcomingByUserId(Long userId) {
        return jpaRepository.findClosestUpcomingByUserId(
            userId,
            ConsultationState.CREATED.getValue(),
            LocalDateTime.now()
        );
    }

    @Override
    public List<UserConsultation> findByUserId(Long userId) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public int countCompletedByUserId(Long userId) {
        return jpaRepository.countByUserIdAndState(userId, ConsultationState.COMPLETED);
    }

    @Override
    @Transactional
    public UserConsultation save(UserConsultation consultation) {
        return jpaRepository.save(consultation);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}

