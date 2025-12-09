package com.goodhelp.user.presentation.web.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Form object for user login.
 * Used to capture email address for sending login link.
 */
@Data
public class UserLoginForm {

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;
}

