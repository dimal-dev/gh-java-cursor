package com.goodhelp.therapist.infrastructure.persistence;

import com.goodhelp.therapist.domain.model.PriceState;
import com.goodhelp.therapist.domain.model.TherapistPrice;
import com.goodhelp.therapist.domain.repository.TherapistPriceRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the domain repository interface using Spring Data JPA.
 */
@Component
public class TherapistPriceRepositoryAdapter implements TherapistPriceRepository {

    private final JpaTherapistPriceRepository jpaRepository;

    public TherapistPriceRepositoryAdapter(JpaTherapistPriceRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<TherapistPrice> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<TherapistPrice> findByTherapistIdAndState(Long therapistId, PriceState state) {
        return jpaRepository.findByTherapistIdAndState(therapistId, state);
    }

    @Override
    public List<TherapistPrice> findByTherapistIdInAndState(List<Long> therapistIds, PriceState state) {
        if (therapistIds == null || therapistIds.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findByTherapistIdInAndState(therapistIds, state);
    }

    @Override
    public Optional<Integer> findMinPriceByTherapistIdAndState(Long therapistId, PriceState state) {
        return jpaRepository.findMinPriceByTherapistIdAndState(therapistId, state);
    }

    @Override
    public TherapistPrice save(TherapistPrice price) {
        return jpaRepository.save(price);
    }
}

