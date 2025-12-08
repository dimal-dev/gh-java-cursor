package com.goodhelp.booking.domain.repository;

import com.goodhelp.booking.domain.model.PriceState;
import com.goodhelp.booking.domain.model.PriceType;
import com.goodhelp.booking.domain.model.TherapistPrice;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TherapistPrice.
 * Manages pricing options for therapist consultations.
 */
public interface TherapistPriceRepository {

    /**
     * Find price by ID.
     */
    Optional<TherapistPrice> findById(Long id);

    /**
     * Find price by unique slug.
     * Slugs are used for direct booking links.
     */
    Optional<TherapistPrice> findBySlug(String slug);

    /**
     * Find all prices for a therapist.
     */
    List<TherapistPrice> findByTherapistId(Long therapistId);

    /**
     * Find prices for a therapist with specific state.
     */
    List<TherapistPrice> findByTherapistIdAndState(Long therapistId, PriceState state);

    /**
     * Find current (active) prices for a therapist.
     */
    default List<TherapistPrice> findCurrentByTherapistId(Long therapistId) {
        return findByTherapistIdAndState(therapistId, PriceState.CURRENT);
    }

    /**
     * Find specific price by therapist, currency, type, and state.
     */
    Optional<TherapistPrice> findByTherapistIdAndCurrencyAndTypeAndState(
        Long therapistId, 
        String currency, 
        PriceType type, 
        PriceState state
    );

    /**
     * Find current individual price for a therapist in specific currency.
     */
    default Optional<TherapistPrice> findCurrentIndividualPrice(Long therapistId, String currency) {
        return findByTherapistIdAndCurrencyAndTypeAndState(
            therapistId, currency, PriceType.INDIVIDUAL, PriceState.CURRENT
        );
    }

    /**
     * Find current couple price for a therapist in specific currency.
     */
    default Optional<TherapistPrice> findCurrentCouplePrice(Long therapistId, String currency) {
        return findByTherapistIdAndCurrencyAndTypeAndState(
            therapistId, currency, PriceType.COUPLE, PriceState.CURRENT
        );
    }

    /**
     * Save a price (create or update).
     */
    TherapistPrice save(TherapistPrice price);
}
