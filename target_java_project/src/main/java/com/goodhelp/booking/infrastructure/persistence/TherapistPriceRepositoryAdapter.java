package com.goodhelp.booking.infrastructure.persistence;

import com.goodhelp.booking.domain.model.PriceState;
import com.goodhelp.booking.domain.model.PriceType;
import com.goodhelp.booking.domain.model.TherapistPrice;
import com.goodhelp.booking.domain.repository.TherapistPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the domain TherapistPriceRepository interface
 * using Spring Data JPA.
 */
@Repository("booking_therapistPriceRepositoryAdapter")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TherapistPriceRepositoryAdapter implements TherapistPriceRepository {

    private final JpaTherapistPriceRepository jpaRepository;

    @Override
    public Optional<TherapistPrice> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<TherapistPrice> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug);
    }

    @Override
    public List<TherapistPrice> findByTherapistId(Long therapistId) {
        return jpaRepository.findByTherapistId(therapistId);
    }

    @Override
    public List<TherapistPrice> findByTherapistIdAndState(Long therapistId, PriceState state) {
        return jpaRepository.findByTherapistIdAndState(therapistId, state);
    }

    @Override
    public Optional<TherapistPrice> findByTherapistIdAndCurrencyAndTypeAndState(
            Long therapistId, 
            String currency, 
            PriceType type, 
            PriceState state) {
        return jpaRepository.findByTherapistIdAndCurrencyAndTypeAndState(
            therapistId, currency, type, state
        );
    }

    @Override
    @Transactional
    public TherapistPrice save(TherapistPrice price) {
        return jpaRepository.save(price);
    }
}

