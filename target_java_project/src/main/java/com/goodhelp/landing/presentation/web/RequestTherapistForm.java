package com.goodhelp.landing.presentation.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * Form object for the "Help me find a therapist" request form.
 */
@Data
public class RequestTherapistForm {

    @NotBlank(message = "{validation.name.required}")
    private String name;

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    private String phone;

    @NotEmpty(message = "{validation.topics.required}")
    private List<String> topics; // Selected topic slugs

    @Size(max = 2000, message = "{validation.message.tooLong}")
    private String message;

    private String preferredTime; // "morning", "afternoon", "evening"

    // Additional fields from PHP form
    private String sex = "both"; // "both", "female", "male"
    private String channel; // "telegram", "viber", "whatsapp"
    private String type; // "individual", "couple", "teenager"
    private String price; // Price value or price ID
    private String promocode;
    private Long therapistId; // Optional: if requesting specific therapist
    private Integer lgbtq = 0; // 0 or 1
}

