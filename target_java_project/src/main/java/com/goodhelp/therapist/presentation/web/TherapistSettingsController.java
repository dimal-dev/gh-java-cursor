package com.goodhelp.therapist.presentation.web;

import com.goodhelp.common.service.TimezoneService;
import com.goodhelp.therapist.application.command.UpdateSettingsCommand;
import com.goodhelp.therapist.application.dto.SettingsDto;
import com.goodhelp.therapist.application.usecase.GetSettingsUseCase;
import com.goodhelp.therapist.application.usecase.UpdateSettingsUseCase;
import com.goodhelp.therapist.infrastructure.security.TherapistUserDetails;
import com.goodhelp.therapist.presentation.web.form.SettingsForm;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for therapist settings management.
 * Handles timezone configuration and Telegram integration.
 */
@Controller
@RequestMapping("/therapist/settings")
public class TherapistSettingsController {

    private static final Logger log = LoggerFactory.getLogger(TherapistSettingsController.class);

    private final GetSettingsUseCase getSettingsUseCase;
    private final UpdateSettingsUseCase updateSettingsUseCase;
    private final TimezoneService timezoneService;

    public TherapistSettingsController(
            GetSettingsUseCase getSettingsUseCase,
            UpdateSettingsUseCase updateSettingsUseCase,
            TimezoneService timezoneService) {
        this.getSettingsUseCase = getSettingsUseCase;
        this.updateSettingsUseCase = updateSettingsUseCase;
        this.timezoneService = timezoneService;
    }

    @GetMapping
    public String showSettings(
            Model model,
            @AuthenticationPrincipal TherapistUserDetails user) {
        
        SettingsDto settings = getSettingsUseCase.execute(user.getId());
        
        SettingsForm form = new SettingsForm();
        form.setNewTimezone(settings.timezone());
        
        model.addAttribute("settings", settings);
        model.addAttribute("settingsForm", form);
        model.addAttribute("timezones", timezoneService.getAvailableTimezones());
        model.addAttribute("currentUser", user);
        
        return "therapist/settings";
    }

    /**
     * Save settings.
     */
    @PostMapping
    public String saveSettings(
            @Valid @ModelAttribute("settingsForm") SettingsForm form,
            BindingResult bindingResult,
            @AuthenticationPrincipal TherapistUserDetails user,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            SettingsDto settings = getSettingsUseCase.execute(user.getId());
            model.addAttribute("settings", settings);
            model.addAttribute("timezones", timezoneService.getAvailableTimezones());
            model.addAttribute("currentUser", user);
            return "therapist/settings";
        }
        
        try {
            UpdateSettingsCommand command = new UpdateSettingsCommand(form.getNewTimezone());
            updateSettingsUseCase.execute(user.getId(), command);
            
            log.info("Settings updated for therapist {}: timezone={}", user.getId(), form.getNewTimezone());
            redirectAttributes.addFlashAttribute("successMessage", "settings.saved");
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid timezone {} for therapist {}", form.getNewTimezone(), user.getId());
            redirectAttributes.addFlashAttribute("errorMessage", "settings.invalidTimezone");
        }
        
        return "redirect:/therapist/settings";
    }
}

