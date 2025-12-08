package com.goodhelp.landing.application.usecase;

import com.goodhelp.landing.application.dto.BookingPriceOptionDto;
import com.goodhelp.landing.application.dto.BookingTherapistDto;
import com.goodhelp.landing.application.dto.TherapistProfileData;
import com.goodhelp.therapist.domain.model.PriceType;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistPrice;
import com.goodhelp.therapist.domain.model.TherapistProfile;
import com.goodhelp.therapist.domain.repository.TherapistPriceRepository;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Use case for assembling therapist data for the book consultation page.
 */
@Service
@Transactional(readOnly = true)
public class GetTherapistForBookingUseCase {

    private static final String DEFAULT_SINGLE_PHRASE = "clinical_psiholog";

    private final TherapistRepository therapistRepository;
    private final TherapistPriceRepository priceRepository;

    public GetTherapistForBookingUseCase(
            TherapistRepository therapistRepository,
            TherapistPriceRepository priceRepository) {
        this.therapistRepository = therapistRepository;
        this.priceRepository = priceRepository;
    }

    /**
     * Fetch therapist summary along with price options for booking.
     *
     * @param therapistId therapist identifier
     * @param priceSlug optional slug to preselect price option
     * @return booking DTO or empty if therapist inactive/unknown
     */
    public Optional<BookingTherapistDto> execute(Long therapistId, String priceSlug) {
        Optional<Therapist> therapistOpt = therapistRepository.findById(therapistId);
        if (therapistOpt.isEmpty()) {
            return Optional.empty();
        }

        Therapist therapist = therapistOpt.get();
        TherapistProfile profile = therapist.getProfile();
        if (!therapist.isActive() || profile == null || !therapist.canAcceptConsultations()) {
            return Optional.empty();
        }

        List<TherapistPrice> prices = priceRepository.findActiveByTherapistId(therapistId);
        if (prices.isEmpty()) {
            return Optional.empty();
        }

        List<BookingPriceOptionDto> options = prices.stream()
            .map(this::toPriceOption)
            .sorted(Comparator.comparingInt(BookingPriceOptionDto::price))
            .toList();

        Long defaultPriceId = resolveDefaultPriceId(options, priceSlug);

        TherapistProfileData.ProfileInfo profileInfo = TherapistProfileData.getProfileInfo(therapistId);
        String singlePhraseKey = profileInfo != null ? profileInfo.singlePhrase() : DEFAULT_SINGLE_PHRASE;

        String photoUrl = profile.getProfileTemplate() != null
            ? "/assets/landing/img/select-psiholog/" + profile.getProfileTemplate() + ".jpg"
            : "/assets/landing/img/placeholder-therapist.jpg";

        return Optional.of(new BookingTherapistDto(
            therapist.getId(),
            profile.getFullName(),
            singlePhraseKey,
            photoUrl,
            options,
            defaultPriceId
        ));
    }

    private BookingPriceOptionDto toPriceOption(TherapistPrice price) {
        Objects.requireNonNull(price, "Price is required");
        String type = price.getType().name().toLowerCase();
        String typeLabelKey = switch (price.getType()) {
            case COUPLE -> "Couple";
            case TEENAGER -> "Teenager";
            case INDIVIDUAL -> "Individual";
        };
        int duration = price.getType() == PriceType.COUPLE ? 80 : 50;

        return new BookingPriceOptionDto(
            price.getId(),
            type,
            typeLabelKey,
            duration,
            price.getPrice(),
            price.getCurrency(),
            price.getSlug()
        );
    }

    private Long resolveDefaultPriceId(List<BookingPriceOptionDto> options, String priceSlug) {
        if (options.isEmpty()) {
            return null;
        }

        if (priceSlug != null && !priceSlug.isBlank()) {
            return options.stream()
                .filter(opt -> priceSlug.equalsIgnoreCase(opt.slug()))
                .map(BookingPriceOptionDto::id)
                .findFirst()
                .orElse(options.get(0).id());
        }

        return options.get(0).id();
    }
}

