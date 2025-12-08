package com.goodhelp.landing.application.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO for displaying detailed therapist profile page.
 * Contains all information needed to render the full therapist profile.
 */
public record TherapistProfileDto(
    Long id,
    String fullName,
    String photoUrl,
    String singlePhrase,
    Integer experienceYears,
    Integer profileCourses,
    boolean hasPsychologistDiploma,
    boolean isNotTherapist,
    List<String> therapyTypes,
    Map<String, String> about,
    List<String> worksWith,
    Map<String, List<WorksWithGroup>> worksWithGrouped,
    EducationInfo education,
    List<String> methods,
    List<LocalizedEntry> associationAndSupervision,
    List<ReviewDto> reviews,
    List<PriceDto> prices,
    Integer price,
    boolean priceFrom,
    String bookConsultationLink
) {
    /**
     * DTO for education information.
     */
    public record EducationInfo(
        int mainAmount,
        int additionalAmount,
        List<EducationEntry> list
    ) {}

    /**
     * DTO for a single education entry.
     */
    public record EducationEntry(
        String years,
        Map<String, String> name
    ) {}

    /**
     * DTO for localized text (used for supervision, etc.).
     */
    public record LocalizedEntry(
        Map<String, String> text
    ) {}

    /**
     * DTO for grouped "works with" topics.
     */
    public record WorksWithGroup(
        String groupKey,
        String groupTitle,
        List<String> topics
    ) {}

    /**
     * DTO for a review.
     */
    public record ReviewDto(
        String name,
        String body,
        String created
    ) {}

    /**
     * DTO for price information.
     */
    public record PriceDto(
        Long id,
        String slug,
        String typeName,
        String formatted,
        int amountCents
    ) {
        public static PriceDto from(Long id, String slug, String type, int price) {
            return new PriceDto(id, slug, type, price + " ₴", price * 100);
        }
    }
}

