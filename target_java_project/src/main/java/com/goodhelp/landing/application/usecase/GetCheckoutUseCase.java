package com.goodhelp.landing.application.usecase;

import com.goodhelp.billing.domain.model.Checkout;
import com.goodhelp.billing.domain.repository.CheckoutRepository;
import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.booking.domain.repository.ScheduleSlotRepository;
import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.common.service.TimezoneHelper;
import com.goodhelp.landing.application.dto.CheckoutSummaryDto;
import com.goodhelp.user.domain.model.UserPromocode;
import com.goodhelp.user.domain.repository.PromocodeRepository;
import com.goodhelp.user.domain.repository.UserPromocodeRepository;
import com.goodhelp.therapist.domain.model.TherapistPrice;
import com.goodhelp.therapist.domain.repository.TherapistPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * Loads an existing checkout session by slug.
 */
@Service
@Transactional(readOnly = true)
public class GetCheckoutUseCase {

    private final CheckoutRepository checkoutRepository;
    private final TherapistPriceRepository priceRepository;
    private final ScheduleSlotRepository slotRepository;
    private final UserPromocodeRepository userPromocodeRepository;
    private final PromocodeRepository promocodeRepository;
    private final CheckoutSummaryMapper checkoutSummaryMapper;
    private final TimezoneHelper timezoneHelper;

    public GetCheckoutUseCase(CheckoutRepository checkoutRepository,
                              TherapistPriceRepository priceRepository,
                              ScheduleSlotRepository slotRepository,
                              UserPromocodeRepository userPromocodeRepository,
                              PromocodeRepository promocodeRepository,
                              CheckoutSummaryMapper checkoutSummaryMapper,
                              TimezoneHelper timezoneHelper) {
        this.checkoutRepository = checkoutRepository;
        this.priceRepository = priceRepository;
        this.slotRepository = slotRepository;
        this.userPromocodeRepository = userPromocodeRepository;
        this.promocodeRepository = promocodeRepository;
        this.checkoutSummaryMapper = checkoutSummaryMapper;
        this.timezoneHelper = timezoneHelper;
    }

    public CheckoutSummaryDto execute(String slug, String userTimezone, Locale locale) {
        Checkout checkout = checkoutRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Checkout not found"));

        TherapistPrice price = priceRepository.findById(checkout.getTherapistPriceId())
            .orElseThrow(() -> new ResourceNotFoundException("Price not found"));

        ScheduleSlot slot = slotRepository.findById(checkout.getScheduleSlotId())
            .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

        PromoData promoData = resolvePromo(checkout, price);

        Locale effectiveLocale = locale != null ? locale : Locale.ENGLISH;

        return checkoutSummaryMapper.toSummary(
            checkout,
            price,
            slot,
            resolveTimezone(userTimezone, slot.getTherapist().getTimezone()),
            effectiveLocale,
            promoData.discountPercent(),
            promoData.discountedPrice(),
            promoData.promocodeCode()
        );
    }

    private PromoData resolvePromo(Checkout checkout, TherapistPrice price) {
        if (checkout.getUserPromocodeId() == null) {
            return PromoData.empty();
        }
        UserPromocode userPromocode = userPromocodeRepository.findById(checkout.getUserPromocodeId())
            .orElse(null);
        if (userPromocode == null || userPromocode.getPromocodeId() == null) {
            return PromoData.empty();
        }

        return promocodeRepository.findById(userPromocode.getPromocodeId())
            .map(promo -> new PromoData(
                promo.getDiscountPercent(),
                promo.applyDiscount(price.getPrice()),
                promo.getName()
            ))
            .orElse(PromoData.empty());
    }

    private String resolveTimezone(String userTimezone, String therapistTimezone) {
        if (StringUtils.hasText(userTimezone) && timezoneHelper.isValidTimezone(userTimezone)) {
            return userTimezone;
        }
        if (timezoneHelper.isValidTimezone(therapistTimezone)) {
            return therapistTimezone;
        }
        return TimezoneHelper.DEFAULT_TIMEZONE;
    }

    private record PromoData(Integer discountPercent, Integer discountedPrice, String promocodeCode) {
        private static PromoData empty() {
            return new PromoData(null, null, null);
        }
    }
}

