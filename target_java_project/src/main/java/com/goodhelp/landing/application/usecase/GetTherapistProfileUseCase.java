package com.goodhelp.landing.application.usecase;

import com.goodhelp.landing.application.dto.TherapistProfileData;
import com.goodhelp.landing.application.dto.TherapistProfileDto;
import com.goodhelp.landing.application.dto.TherapistProfileDto.*;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistPrice;
import com.goodhelp.therapist.domain.model.TherapistProfile;
import com.goodhelp.therapist.domain.repository.TherapistPriceRepository;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Use case for fetching detailed therapist profile for the landing page.
 * Returns comprehensive profile information including bio, education, methods, and pricing.
 */
@Service
@Transactional(readOnly = true)
public class GetTherapistProfileUseCase {

    private final TherapistRepository therapistRepository;
    private final TherapistPriceRepository priceRepository;

    public GetTherapistProfileUseCase(
            TherapistRepository therapistRepository,
            TherapistPriceRepository priceRepository) {
        this.therapistRepository = therapistRepository;
        this.priceRepository = priceRepository;
    }

    /**
     * Get therapist profile by ID.
     * 
     * @param therapistId the therapist ID
     * @param priceSlug optional price slug to highlight specific price
     * @return Optional containing profile DTO, or empty if therapist not found/inactive
     */
    public Optional<TherapistProfileDto> execute(Long therapistId, String priceSlug) {
        // Find active therapist
        Optional<Therapist> therapistOpt = therapistRepository.findById(therapistId);
        
        if (therapistOpt.isEmpty() || !therapistOpt.get().isActive()) {
            return Optional.empty();
        }

        Therapist therapist = therapistOpt.get();
        TherapistProfile profile = therapist.getProfile();
        
        if (profile == null) {
            return Optional.empty();
        }

        // Get extended profile data (from static data provider for now)
        TherapistProfileData.ProfileInfo profileInfo = TherapistProfileData.getProfileInfo(therapistId);
        
        // Get prices
        List<TherapistPrice> prices = priceRepository.findActiveByTherapistIds(List.of(therapistId));
        
        // Find specific price if slug provided
        TherapistPrice selectedPrice = null;
        if (priceSlug != null && !priceSlug.isBlank()) {
            selectedPrice = prices.stream()
                .filter(p -> priceSlug.equals(p.getSlug()))
                .findFirst()
                .orElse(null);
        }
        
        // Calculate display price
        Integer displayPrice = prices.stream()
            .map(TherapistPrice::getPrice)
            .min(Integer::compareTo)
            .orElse(null);
        boolean priceFrom = prices.size() > 1;

        // Build profile DTO
        return Optional.of(buildProfileDto(
            therapist, 
            profile, 
            profileInfo, 
            prices, 
            selectedPrice,
            displayPrice, 
            priceFrom
        ));
    }

    /**
     * Get therapist profile by ID (without price slug).
     */
    public Optional<TherapistProfileDto> execute(Long therapistId) {
        return execute(therapistId, null);
    }

    private TherapistProfileDto buildProfileDto(
            Therapist therapist,
            TherapistProfile profile,
            TherapistProfileData.ProfileInfo profileInfo,
            List<TherapistPrice> prices,
            TherapistPrice selectedPrice,
            Integer displayPrice,
            boolean priceFrom) {

        // Build photo URL
        String photoUrl = profile.getProfileTemplate() != null
            ? "/assets/landing/img/select-psiholog/" + profile.getProfileTemplate() + ".jpg"
            : "/assets/landing/img/placeholder-therapist.jpg";

        // Convert prices to DTOs
        List<PriceDto> priceDtos = prices.stream()
            .map(p -> PriceDto.from(p.getId(), p.getSlug(), p.getType().name(), p.getPrice()))
            .toList();

        // Build education info
        EducationInfo educationInfo = null;
        if (profileInfo != null && profileInfo.education() != null) {
            TherapistProfileData.Education edu = profileInfo.education();
            List<EducationEntry> educationEntries = edu.list().stream()
                .map(e -> new EducationEntry(e.years(), e.name()))
                .toList();
            educationInfo = new EducationInfo(edu.mainAmount(), edu.additionalAmount(), educationEntries);
        }

        // Build association/supervision entries
        List<LocalizedEntry> associationEntries = new ArrayList<>();
        if (profileInfo != null && profileInfo.associationAndSupervision() != null) {
            associationEntries = profileInfo.associationAndSupervision().stream()
                .map(LocalizedEntry::new)
                .toList();
        }

        // Build reviews
        List<ReviewDto> reviews = new ArrayList<>();
        if (profileInfo != null && profileInfo.reviews() != null) {
            reviews = profileInfo.reviews().stream()
                .map(r -> new ReviewDto(r.name(), r.body(), r.created()))
                .toList();
        }

        // Group topics
        List<String> worksWith = profileInfo != null ? profileInfo.worksWith() : List.of();
        Map<String, List<String>> groupedTopics = TherapistProfileData.groupTopics(worksWith);
        
        // Convert to WorksWithGroup format
        Map<String, List<WorksWithGroup>> worksWithGrouped = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : groupedTopics.entrySet()) {
            worksWithGrouped.put(entry.getKey(), List.of(
                new WorksWithGroup(entry.getKey(), entry.getKey(), entry.getValue())
            ));
        }

        // Build book consultation link
        String bookLink = "/book-consultation/" + therapist.getId();
        if (selectedPrice != null && selectedPrice.getSlug() != null) {
            bookLink += "?tft=" + selectedPrice.getSlug();
        }

        return new TherapistProfileDto(
            therapist.getId(),
            profile.getFullName(),
            photoUrl,
            profileInfo != null ? profileInfo.singlePhrase() : "clinical_psiholog",
            profile.getYearsOfExperience(),
            profileInfo != null ? profileInfo.profileCourses() : null,
            profileInfo == null || profileInfo.hasPsychologistDiploma(),
            profileInfo != null && profileInfo.isNotTherapist(),
            profileInfo != null ? profileInfo.therapyTypes() : List.of("Individual"),
            profileInfo != null ? profileInfo.about() : Map.of(),
            worksWith,
            worksWithGrouped,
            educationInfo,
            profileInfo != null ? profileInfo.methods() : List.of(),
            associationEntries,
            reviews,
            priceDtos,
            displayPrice,
            priceFrom,
            bookLink
        );
    }
}

