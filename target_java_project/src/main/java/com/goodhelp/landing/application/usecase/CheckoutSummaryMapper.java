package com.goodhelp.landing.application.usecase;

import com.goodhelp.billing.domain.model.Checkout;
import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.common.service.DateLocalizedHelper;
import com.goodhelp.common.service.TimezoneHelper;
import com.goodhelp.landing.application.dto.CheckoutClientDto;
import com.goodhelp.landing.application.dto.CheckoutPriceDto;
import com.goodhelp.landing.application.dto.CheckoutSessionDto;
import com.goodhelp.landing.application.dto.CheckoutSummaryDto;
import com.goodhelp.landing.application.dto.CheckoutTherapistDto;
import com.goodhelp.therapist.domain.model.PriceType;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistPrice;
import com.goodhelp.therapist.domain.model.TherapistProfile;
import com.goodhelp.landing.application.dto.TherapistProfileData;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/**
 * Maps domain entities to checkout view DTOs.
 */
@Component
public class CheckoutSummaryMapper {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final DateLocalizedHelper dateLocalizedHelper;
    private final TimezoneHelper timezoneHelper;

    public CheckoutSummaryMapper(DateLocalizedHelper dateLocalizedHelper, TimezoneHelper timezoneHelper) {
        this.dateLocalizedHelper = dateLocalizedHelper;
        this.timezoneHelper = timezoneHelper;
    }

    public CheckoutSummaryDto toSummary(Checkout checkout,
                                        TherapistPrice price,
                                        ScheduleSlot slot,
                                        String timezone,
                                        Locale locale,
                                        Integer discountPercent,
                                        Integer discountedPrice,
                                        String promocodeCode) {
        Objects.requireNonNull(checkout, "Checkout is required");
        Objects.requireNonNull(price, "Therapist price is required");
        Objects.requireNonNull(slot, "Schedule slot is required");

        Locale effectiveLocale = locale != null ? locale : Locale.ENGLISH;
        int basePrice = Objects.requireNonNullElse(price.getPrice(), 0);
        int finalPrice = discountedPrice != null ? discountedPrice : basePrice;

        Therapist therapist = slot.getTherapist();
        TherapistProfile profile = therapist.getProfile();
        String photoUrl = profile != null && profile.getProfileTemplate() != null
            ? "/assets/landing/img/select-psiholog/" + profile.getProfileTemplate() + ".jpg"
            : "/assets/landing/img/placeholder-therapist.jpg";

        String sessionTypeLabelKey = mapPriceType(price.getType());
        int durationMinutes = price.getType() == PriceType.COUPLE ? 80 : 50;

        var therapistInfo = new CheckoutTherapistDto(
            therapist.getId(),
            profile != null ? profile.getFullName() : "GoodHelp therapist",
            resolveSinglePhrase(therapist.getId()),
            photoUrl,
            sessionTypeLabelKey,
            durationMinutes
        );

        String resolvedTimezone = resolveTimezone(timezone, therapist.getTimezone());
        var zonedDate = slot.getAvailableAt().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of(resolvedTimezone));
        int dayOfWeekNumber = zonedDate.getDayOfWeek().getValue(); // 1-7

        String dayShort = dateLocalizedHelper.getWeekDayShortNameByNumber(dayOfWeekNumber, effectiveLocale.getLanguage());
        String dayFull = dateLocalizedHelper.getWeekDayNameByNumber(dayOfWeekNumber, effectiveLocale.getLanguage()).toLowerCase(effectiveLocale);
        String monthLabel = dateLocalizedHelper.getMonthNameByNumberInclined(zonedDate.getMonthValue(), effectiveLocale.getLanguage())
            .toLowerCase(effectiveLocale);

        var sessionInfo = new CheckoutSessionDto(
            zonedDate.getDayOfMonth(),
            monthLabel,
            dayShort,
            dayFull,
            TIME_FORMATTER.format(zonedDate),
            timezoneHelper.getLabelForOffset(timezoneHelper.getOffsetForTimezone(resolvedTimezone), resolvedTimezone),
            resolvedTimezone
        );

        var priceInfo = new CheckoutPriceDto(
            basePrice,
            finalPrice,
            price.getCurrency(),
            discountPercent,
            promocodeCode
        );

        var clientInfo = new CheckoutClientDto(
            checkout.getName(),
            checkout.getEmail(),
            checkout.getPhone(),
            checkout.getAuthType()
        );

        return new CheckoutSummaryDto(
            checkout.getId(),
            checkout.getSlug(),
            price.getId(),
            slot.getId(),
            checkout.getGaClientId(),
            therapistInfo,
            sessionInfo,
            priceInfo,
            clientInfo
        );
    }

    private String resolveSinglePhrase(Long therapistId) {
        var profileInfo = TherapistProfileData.getProfileInfo(therapistId);
        return profileInfo != null ? profileInfo.singlePhrase() : "clinical_psiholog";
    }

    private String mapPriceType(PriceType type) {
        return switch (type) {
            case COUPLE -> "Couple";
            case TEENAGER -> "Teenager";
            case INDIVIDUAL -> "Individual";
        };
    }

    private String resolveTimezone(String requested, String therapistTimezone) {
        if (requested != null && timezoneHelper.isValidTimezone(requested)) {
            return requested;
        }
        if (timezoneHelper.isValidTimezone(therapistTimezone)) {
            return therapistTimezone;
        }
        return TimezoneHelper.DEFAULT_TIMEZONE;
    }
}

