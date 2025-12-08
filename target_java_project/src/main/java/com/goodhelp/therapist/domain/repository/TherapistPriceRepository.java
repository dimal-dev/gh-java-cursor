package com.goodhelp.therapist.domain.repository;

import com.goodhelp.therapist.domain.model.PriceState;
import com.goodhelp.therapist.domain.model.TherapistPrice;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TherapistPrice entity.
 * Defines domain-level persistence operations for pricing.
 */
public interface TherapistPriceRepository {

    /**
     * Find price by ID.
     */
    Optional<TherapistPrice> findById(Long id);

    /**
     * Find all active prices for a therapist.
     */
    List<TherapistPrice> findByTherapistIdAndState(Long therapistId, PriceState state);

    /**
     * Find all active prices for a therapist.
     */
    default List<TherapistPrice> findActiveByTherapistId(Long therapistId) {
        return findByTherapistIdAndState(therapistId, PriceState.ACTIVE);
    }

    /**
     * Find all active prices for multiple therapists.
     */
    List<TherapistPrice> findByTherapistIdInAndState(List<Long> therapistIds, PriceState state);

    /**
     * Find all active prices for multiple therapists.
     */
    default List<TherapistPrice> findActiveByTherapistIds(List<Long> therapistIds) {
        return findByTherapistIdInAndState(therapistIds, PriceState.ACTIVE);
    }

    /**
     * Find minimum price for a therapist.
     */
    Optional<Integer> findMinPriceByTherapistIdAndState(Long therapistId, PriceState state);

    /**
     * Save a price.
     */
    TherapistPrice save(TherapistPrice price);
}

