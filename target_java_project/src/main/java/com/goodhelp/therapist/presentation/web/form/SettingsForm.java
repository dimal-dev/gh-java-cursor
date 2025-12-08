package com.goodhelp.therapist.presentation.web.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Form for therapist settings submission.
 */
@Data
public class SettingsForm {

    @NotBlank(message = "{validation.timezone.required}")
    private String newTimezone;
}

