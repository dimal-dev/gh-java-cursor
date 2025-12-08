package com.goodhelp.landing.application.usecase;

import com.goodhelp.billing.domain.model.Checkout;
import com.goodhelp.billing.domain.repository.CheckoutRepository;
import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.booking.domain.repository.ScheduleSlotRepository;
import com.goodhelp.booking.domain.service.ScheduleDomainService;
import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.common.service.TimezoneHelper;
import com.goodhelp.landing.application.dto.CheckoutSummaryDto;
import com.goodhelp.user.domain.model.UserPromocode;
import com.goodhelp.user.domain.repository.UserPromocodeRepository;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistPrice;
import com.goodhelp.therapist.domain.model.TherapistSettings;
import com.goodhelp.therapist.domain.repository.TherapistPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.UUID;

/**
 * Creates a checkout session from the booking form.
 */
@Service
@Transactional
public class CreateCheckoutUseCase {

    private final TherapistPriceRepository priceRepository;
    private final ScheduleSlotRepository slotRepository;
    private final CheckoutRepository checkoutRepository;
    private final UserPromocodeRepository userPromocodeRepository;
    private final ApplyPromocodeUseCase applyPromocodeUseCase;
    private final ScheduleDomainService scheduleDomainService;
    private final CheckoutSummaryMapper checkoutSummaryMapper;
    private final TimezoneHelper timezoneHelper;

    public CreateCheckoutUseCase(TherapistPriceRepository priceRepository,
                                 ScheduleSlotRepository slotRepository,
                                 CheckoutRepository checkoutRepository,
                                 UserPromocodeRepository userPromocodeRepository,
                                 ApplyPromocodeUseCase applyPromocodeUseCase,
                                 ScheduleDomainService scheduleDomainService,
                                 CheckoutSummaryMapper checkoutSummaryMapper,
                                 TimezoneHelper timezoneHelper) {
        this.priceRepository = priceRepository;
        this.slotRepository = slotRepository;
        this.checkoutRepository = checkoutRepository;
        this.userPromocodeRepository = userPromocodeRepository;
        this.applyPromocodeUseCase = applyPromocodeUseCase;
        this.scheduleDomainService = scheduleDomainService;
        this.checkoutSummaryMapper = checkoutSummaryMapper;
        this.timezoneHelper = timezoneHelper;
    }

    public CheckoutSummaryDto execute(CreateCheckoutCommand command) {
        validateCommand(command);

        TherapistPrice price = priceRepository.findById(command.priceId())
            .orElseThrow(() -> new ResourceNotFoundException("Price not found"));
        if (!price.getTherapistId().equals(command.therapistId())) {
            throw new ResourceNotFoundException("Price does not belong to therapist");
        }

        ScheduleSlot slot = loadAndValidateSlot(command.slotId(), price.getTherapistId());
        Therapist therapist = slot.getTherapist();

        PromoContext promoContext = applyPromocodeIfNeeded(command, price);

        String slug = UUID.randomUUID().toString().replace("-", "");

        Checkout checkout = Checkout.create(
            slug,
            price.getId(),
            slot.getId(),
            promoContext.userPromocodeId(),
            null,
            command.authType(),
            normalizedOrNull(command.phone()),
            normalizedEmail(command.email()),
            normalizedOrNull(command.name()),
            command.gaClientId(),
            command.gaClientIdOriginal()
        );

        Checkout persisted = checkoutRepository.save(checkout);

        Locale locale = StringUtils.hasText(command.locale())
            ? Locale.forLanguageTag(command.locale())
            : Locale.ENGLISH;

        return checkoutSummaryMapper.toSummary(
            persisted,
            price,
            slot,
            resolveTimezone(command.timezone(), therapist.getTimezone()),
            locale,
            promoContext.discountPercent(),
            promoContext.discountedPrice(),
            promoContext.promocodeCode()
        );
    }

    private void validateCommand(CreateCheckoutCommand command) {
        if (command.slotId() == null || command.priceId() == null || command.therapistId() == null) {
            throw new IllegalArgumentException("Therapist, slot, and price are required");
        }
        if (!StringUtils.hasText(command.email())) {
            throw new IllegalArgumentException("Email is required");
        }
        String authType = command.authType() != null ? command.authType().toLowerCase(Locale.ROOT) : "new";
        if ("new".equals(authType)) {
            if (!StringUtils.hasText(command.name()) || !StringUtils.hasText(command.phone())) {
                throw new IllegalArgumentException("Name and phone are required for new users");
            }
        }
    }

    private ScheduleSlot loadAndValidateSlot(Long slotId, Long therapistId) {
        ScheduleSlot slot = slotRepository.findById(slotId)
            .orElseThrow(() -> new ResourceNotFoundException("Slot not found"));

        Therapist therapist = slot.getTherapist();
        if (therapist == null || !therapistId.equals(therapist.getId())) {
            throw new ResourceNotFoundException("Slot does not belong to therapist");
        }
        if (!therapist.isActive() || !therapist.canAcceptConsultations()) {
            throw new IllegalStateException("Therapist is not accepting consultations");
        }

        int timeCapHours = therapist.getSettingsOptional()
            .map(TherapistSettings::getScheduleTimeCapHours)
            .orElse(3);

        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
        if (!scheduleDomainService.isSlotBookable(slot, nowUtc, timeCapHours)) {
            throw new IllegalStateException("Selected slot is no longer bookable");
        }

        return slot;
    }

    private PromoContext applyPromocodeIfNeeded(CreateCheckoutCommand command, TherapistPrice price) {
        if (!StringUtils.hasText(command.promocode())) {
            return PromoContext.empty();
        }
        String code = command.promocode().trim();
        var result = applyPromocodeUseCase.execute(
            new ApplyPromocodeRequest(code, price.getId(), normalizedEmail(command.email()))
        );
        if (!result.valid()) {
            throw new IllegalArgumentException("Invalid promocode");
        }
        UserPromocode userPromocode = userPromocodeRepository.save(
            UserPromocode.applied(result.promocodeId(), normalizedEmail(command.email()), null)
        );

        return new PromoContext(result.discountPercent(), result.newPrice(), userPromocode.getId(), code);
    }

    private String normalizedEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizedOrNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String resolveTimezone(String userTimezone, String therapistTimezone) {
        if (userTimezone != null && timezoneHelper.isValidTimezone(userTimezone)) {
            return userTimezone;
        }
        if (timezoneHelper.isValidTimezone(therapistTimezone)) {
            return therapistTimezone;
        }
        return TimezoneHelper.DEFAULT_TIMEZONE;
    }

    private record PromoContext(Integer discountPercent,
                                Integer discountedPrice,
                                Long userPromocodeId,
                                String promocodeCode) {
        private static PromoContext empty() {
            return new PromoContext(null, null, null, null);
        }
    }
}

