package com.goodhelp.therapist.presentation.web;

import com.goodhelp.therapist.application.command.RequestLoginCommand;
import com.goodhelp.therapist.application.usecase.RequestLoginLinkUseCase;
import com.goodhelp.therapist.presentation.web.form.LoginForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

/**
 * Controller for therapist authentication pages.
 * 
 * <p>Authentication flow:</p>
 * <ol>
 *   <li>Therapist enters email on login page</li>
 *   <li>System sends login link via email/Telegram</li>
 *   <li>Therapist clicks link and gets authenticated</li>
 * </ol>
 * 
 * <p>Note: Auto-login via token is handled by {@code TherapistAutoLoginFilter}.</p>
 */
@Controller
@RequestMapping("/therapist")
@RequiredArgsConstructor
@Slf4j
public class TherapistLoginController {

    private final RequestLoginLinkUseCase requestLoginLinkUseCase;
    private final MessageSource messageSource;

    /**
     * Display the login page.
     */
    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "expired", required = false) String expired,
            Model model,
            Locale locale) {
        
        model.addAttribute("loginForm", new LoginForm());
        
        if (error != null) {
            if ("invalid_token".equals(error)) {
                model.addAttribute("errorMessage", 
                    messageSource.getMessage("therapist.login.invalidToken", null, locale));
            } else {
                model.addAttribute("errorMessage", 
                    messageSource.getMessage("therapist.login.error", null, locale));
            }
        }
        
        if (expired != null) {
            model.addAttribute("errorMessage", 
                messageSource.getMessage("therapist.login.sessionExpired", null, locale));
        }
        
        return "therapist/login";
    }

    /**
     * Process login form submission - sends login link.
     */
    @PostMapping("/request-login")
    public String processLoginRequest(
            @Valid LoginForm loginForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        
        if (bindingResult.hasErrors()) {
            return "therapist/login";
        }

        log.debug("Login link requested for: {}", loginForm.getEmail());
        
        var result = requestLoginLinkUseCase.execute(
            new RequestLoginCommand(loginForm.getEmail())
        );

        // Always show success message to prevent email enumeration
        String successMessage;
        if (result.sentByTelegram()) {
            successMessage = messageSource.getMessage(
                "therapist.login.telegramSent", null, locale);
        } else {
            successMessage = messageSource.getMessage(
                "therapist.login.emailSent", null, locale);
        }
        
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        return "redirect:/therapist/login";
    }

    /**
     * Handle auto-login endpoint.
     * Note: The actual authentication is handled by TherapistAutoLoginFilter.
     * This endpoint handles the case when filter passes through (already authenticated)
     * or any fallback scenarios.
     */
    @GetMapping("/auto-login")
    public String autoLogin(
            @RequestParam(value = "t", required = false) String token,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        
        // If we reach here, the filter already processed the authentication
        // Either successfully (redirect to schedule) or failed (redirect to login with error)
        
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                messageSource.getMessage("therapist.login.invalidToken", null, locale));
            return "redirect:/therapist/login?error=invalid_token";
        }
        
        // Default redirect (filter should handle most cases)
        return "redirect:/therapist/schedule";
    }

    /**
     * Handle logout success.
     */
    @GetMapping("/logout-success")
    public String logoutSuccess(RedirectAttributes redirectAttributes, Locale locale) {
        redirectAttributes.addFlashAttribute("successMessage",
            messageSource.getMessage("therapist.login.loggedOut", null, locale));
        return "redirect:/therapist/login";
    }
}

