package com.goodhelp.landing.application.usecase;

import com.goodhelp.landing.application.dto.TherapistListItemDto;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistPrice;
import com.goodhelp.therapist.domain.repository.TherapistPriceRepository;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Use case for fetching therapist catalog for landing page.
 * Returns a list of therapists with their minimum prices.
 */
@Service
@Transactional(readOnly = true)
public class GetTherapistCatalogUseCase {

    private final TherapistRepository therapistRepository;
    private final TherapistPriceRepository priceRepository;

    public GetTherapistCatalogUseCase(
            TherapistRepository therapistRepository,
            TherapistPriceRepository priceRepository) {
        this.therapistRepository = therapistRepository;
        this.priceRepository = priceRepository;
    }

    /**
     * Get all active therapists for the catalog.
     */
    public List<TherapistListItemDto> execute() {
        // Get all active real therapists
        List<Therapist> therapists = therapistRepository.findAllActiveRealTherapists();
        
        if (therapists.isEmpty()) {
            return List.of();
        }

        // Get therapist IDs
        List<Long> therapistIds = therapists.stream()
            .map(Therapist::getId)
            .toList();

        // Fetch all active prices for these therapists
        List<TherapistPrice> allPrices = priceRepository.findActiveByTherapistIds(therapistIds);

        // Group prices by therapist ID
        Map<Long, List<TherapistPrice>> pricesByTherapist = allPrices.stream()
            .collect(Collectors.groupingBy(TherapistPrice::getTherapistId));

        // Build DTOs
        return therapists.stream()
            .map(therapist -> {
                List<TherapistPrice> prices = pricesByTherapist.getOrDefault(therapist.getId(), List.of());
                
                // Find minimum price
                Integer minPrice = prices.stream()
                    .map(TherapistPrice::getPrice)
                    .min(Integer::compareTo)
                    .orElse(null);
                
                boolean hasMultiplePrices = prices.size() > 1;
                
                return TherapistListItemDto.fromEntity(therapist, minPrice, hasMultiplePrices);
            })
            .toList();
    }

    /**
     * Get therapists filtered by topic/specialty.
     * TODO: Implement topic filtering when specialty data is added to the domain model.
     */
    public List<TherapistListItemDto> execute(String topic) {
        // For now, return all therapists - topic filtering to be implemented
        // when specialty/topic data is properly modeled
        return execute();
    }
}

