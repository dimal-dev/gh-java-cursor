package com.goodhelp.landing.application.usecase;

import com.goodhelp.landing.presentation.web.form.BookingForm;

/**
 * Command for creating checkout from booking form.
 */
public record CreateCheckoutCommand(
    Long therapistId,
    Long priceId,
    Long slotId,
    String email,
    String phone,
    String name,
    String promocode,
    String authType,
    String timezone,
    String locale,
    String gaClientId,
    String gaClientIdOriginal
) {

    public static CreateCheckoutCommand from(BookingForm form,
                                             String timezone,
                                             String locale,
                                             String gaClientId,
                                             String gaClientIdOriginal) {
        return new CreateCheckoutCommand(
            form.getTherapistId(),
            form.getPriceId(),
            form.getSlotId(),
            form.getEmail(),
            form.getPhone(),
            form.getName(),
            form.getPromocode(),
            form.getAuthType(),
            timezone,
            locale,
            gaClientId,
            gaClientIdOriginal
        );
    }
}

