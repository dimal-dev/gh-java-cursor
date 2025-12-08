package com.goodhelp.landing.presentation.web.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Form backing object for booking consultation.
 */
@Data
public class BookingForm {

    @NotNull
    private Long therapistId;

    @NotNull
    private Long priceId;

    @NotNull
    private Long slotId;

    @Email
    @NotBlank
    private String email;

    private String phone;

    private String name;

    private String promocode;

    @NotBlank
    private String authType;  // "new" or "existing"
}

