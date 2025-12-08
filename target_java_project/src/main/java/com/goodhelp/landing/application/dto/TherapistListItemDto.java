package com.goodhelp.landing.application.dto;

import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistProfile;

import java.util.List;

/**
 * DTO for displaying therapist in the catalog/list page.
 * Contains all information needed to render a therapist card.
 */
public record TherapistListItemDto(
    Long id,
    String fullName,
    String photoUrl,
    String singlePhrase,
    Integer experienceYears,
    Integer price,
    boolean priceFrom,
    String currency,
    List<String> specialties
) {
    /**
     * Create DTO from domain entity with price information.
     */
    public static TherapistListItemDto fromEntity(Therapist therapist, Integer minPrice, boolean hasMultiplePrices) {
        TherapistProfile profile = therapist.getProfile();
        
        // Build photo URL from profile template
        String photoUrl = profile != null && profile.getProfileTemplate() != null
            ? "/assets/landing/img/select-psiholog/" + profile.getProfileTemplate() + ".jpg"
            : "/assets/landing/img/placeholder-therapist.jpg";
        
        return new TherapistListItemDto(
            therapist.getId(),
            therapist.getFullName(),
            photoUrl,
            "gestalt_therapist_and_lgbt_friendly_psiholog", // Placeholder - would come from extended profile
            profile != null ? profile.getYearsOfExperience() : null,
            minPrice,
            hasMultiplePrices,
            "₴",
            List.of("Crisis_and_trauma", "Interpersonal_relations", "Sexology") // Placeholder - would come from extended profile
        );
    }

    /**
     * Get formatted price string.
     */
    public String getFormattedPrice() {
        if (price == null) {
            return "";
        }
        return price + " " + currency;
    }
}

