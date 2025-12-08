package com.goodhelp.therapist.infrastructure.persistence;

import com.goodhelp.therapist.domain.model.PriceState;
import com.goodhelp.therapist.domain.model.TherapistPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for TherapistPrice entity.
 */
@Repository
public interface JpaTherapistPriceRepository extends JpaRepository<TherapistPrice, Long> {

    List<TherapistPrice> findByTherapistIdAndState(Long therapistId, PriceState state);

    List<TherapistPrice> findByTherapistIdInAndState(List<Long> therapistIds, PriceState state);

    @Query("SELECT MIN(p.price) FROM TherapistPrice p WHERE p.therapistId = :therapistId AND p.state = :state")
    Optional<Integer> findMinPriceByTherapistIdAndState(
        @Param("therapistId") Long therapistId, 
        @Param("state") PriceState state
    );
}

