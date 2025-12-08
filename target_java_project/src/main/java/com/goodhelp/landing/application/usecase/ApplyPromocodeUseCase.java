package com.goodhelp.landing.application.usecase;

import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.therapist.domain.model.TherapistPrice;
import com.goodhelp.therapist.domain.repository.TherapistPriceRepository;
import com.goodhelp.user.domain.model.Promocode;
import com.goodhelp.user.domain.model.UserPromocodeState;
import com.goodhelp.user.domain.repository.PromocodeRepository;
import com.goodhelp.user.domain.repository.UserPromocodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Applies promocodes for the book consultation flow.
 */
@Service
@Transactional(readOnly = true)
public class ApplyPromocodeUseCase {

    private final PromocodeRepository promocodeRepository;
    private final UserPromocodeRepository userPromocodeRepository;
    private final TherapistPriceRepository priceRepository;

    public ApplyPromocodeUseCase(
            PromocodeRepository promocodeRepository,
            UserPromocodeRepository userPromocodeRepository,
            TherapistPriceRepository priceRepository) {
        this.promocodeRepository = promocodeRepository;
        this.userPromocodeRepository = userPromocodeRepository;
        this.priceRepository = priceRepository;
    }

    public PromocodeResult execute(ApplyPromocodeRequest request) {
        TherapistPrice price = priceRepository.findById(request.priceId())
            .orElseThrow(() -> new ResourceNotFoundException("Price not found"));

        String code = request.code().trim().toLowerCase();
        if (code.isEmpty()) {
            return PromocodeResult.invalid("Please_enter_promocode");
        }

        Promocode promocode = promocodeRepository.findActiveByName(code)
            .orElse(null);

        if (promocode == null || !promocode.isActive()) {
            return PromocodeResult.invalid("Promocode_doesnt_exist");
        }

        if (promocode.isExpired(LocalDateTime.now())) {
            return PromocodeResult.invalid("Promocode_doesnt_exist");
        }

        if (promocode.getMaxUseNumber() != null
            && promocode.getMaxUseNumber() > 0
            && request.email() != null
            && !request.email().isBlank()) {
            long used = userPromocodeRepository.countByEmailAndPromocodeIdAndState(
                request.email().toLowerCase(),
                promocode.getId(),
                UserPromocodeState.USED
            );
            if (used >= promocode.getMaxUseNumber()) {
                return PromocodeResult.invalid("You_have_already_applied_this_promo");
            }
        }

        int newPrice = promocode.applyDiscount(price.getPrice());
        return PromocodeResult.valid(promocode.getId(), promocode.getDiscountPercent(), newPrice, "Promocode_applied");
    }
}

