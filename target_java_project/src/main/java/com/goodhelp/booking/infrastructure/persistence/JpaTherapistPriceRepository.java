package com.goodhelp.booking.infrastructure.persistence;

import com.goodhelp.booking.domain.model.PriceState;
import com.goodhelp.booking.domain.model.PriceType;
import com.goodhelp.booking.domain.model.TherapistPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for TherapistPrice entity.
 * Manages pricing options for therapist consultations.
 */
@Repository("booking_jpaTherapistPriceRepository")
public interface JpaTherapistPriceRepository extends JpaRepository<TherapistPrice, Long> {

    /**
     * Find price by unique slug.
     */
    Optional<TherapistPrice> findBySlug(String slug);

    /**
     * Find all prices for a therapist.
     */
    @Query("SELECT p FROM TherapistPrice p WHERE p.therapist.id = :therapistId ORDER BY p.type, p.currency")
    List<TherapistPrice> findByTherapistId(@Param("therapistId") Long therapistId);

    /**
     * Find prices for a therapist with specific state.
     */
    @Query("SELECT p FROM TherapistPrice p WHERE p.therapist.id = :therapistId AND p.state = :state " +
           "ORDER BY p.type, p.currency")
    List<TherapistPrice> findByTherapistIdAndState(
        @Param("therapistId") Long therapistId,
        @Param("state") PriceState state
    );

    /**
     * Find specific price by therapist, currency, type, and state.
     */
    @Query("SELECT p FROM TherapistPrice p WHERE p.therapist.id = :therapistId " +
           "AND p.currency = :currency AND p.type = :type AND p.state = :state")
    Optional<TherapistPrice> findByTherapistIdAndCurrencyAndTypeAndState(
        @Param("therapistId") Long therapistId,
        @Param("currency") String currency,
        @Param("type") PriceType type,
        @Param("state") PriceState state
    );

    /**
     * Find current prices for a therapist sorted by type.
     * Used for displaying prices on landing page.
     */
    @Query("SELECT p FROM TherapistPrice p WHERE p.therapist.id = :therapistId " +
           "AND p.state = :state ORDER BY p.type ASC, p.price ASC")
    List<TherapistPrice> findCurrentPricesSorted(
        @Param("therapistId") Long therapistId,
        @Param("state") PriceState state
    );

    /**
     * Find all active prices (for admin overview).
     */
    @Query("SELECT p FROM TherapistPrice p WHERE p.state = :state")
    List<TherapistPrice> findAllByState(@Param("state") PriceState state);
}

