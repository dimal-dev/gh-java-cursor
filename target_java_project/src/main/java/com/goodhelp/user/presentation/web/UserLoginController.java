package com.goodhelp.user.presentation.web;

import com.goodhelp.user.application.command.RequestLoginCommand;
import com.goodhelp.user.application.usecase.RequestUserLoginLinkUseCase;
import com.goodhelp.user.presentation.web.form.UserLoginForm;
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
 * Controller for user authentication pages.
 * 
 * <p>Authentication flow:</p>
 * <ol>
 *   <li>User enters email on login page</li>
 *   <li>System sends login link via email</li>
 *   <li>User clicks link and gets authenticated</li>
 * </ol>
 * 
 * <p>Note: Auto-login via token is handled by {@code UserAutoLoginFilter}.</p>
 */
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserLoginController {

    private final RequestUserLoginLinkUseCase requestLoginLinkUseCase;
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
        
        model.addAttribute("form", new UserLoginForm());
        
        if (error != null) {
            if ("invalid_token".equals(error)) {
                model.addAttribute("errorMessage", 
                    messageSource.getMessage("user.login.invalidToken", null, locale));
            } else {
                model.addAttribute("errorMessage", 
                    messageSource.getMessage("user.login.error", null, locale));
            }
        }
        
        if (expired != null) {
            model.addAttribute("errorMessage", 
                messageSource.getMessage("user.login.sessionExpired", null, locale));
        }
        
        return "user/login";
    }

    /**
     * Process login form submission - sends login link.
     */
    @PostMapping("/login")
    public String processLogin(
            @Valid UserLoginForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        
        if (bindingResult.hasErrors()) {
            return "user/login";
        }

        log.debug("Login link requested for user: {}", form.getEmail());
        
        requestLoginLinkUseCase.execute(
            new RequestLoginCommand(form.getEmail())
        );

        // Always show success message to prevent email enumeration
        String successMessage = messageSource.getMessage(
            "user.login.emailSent", null, locale);
        
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        return "redirect:/user/login";
    }

    /**
     * Handle auto-login endpoint.
     * Note: The actual authentication is handled by UserAutoLoginFilter.
     * This endpoint handles the case when filter passes through (already authenticated)
     * or any fallback scenarios.
     */
    @GetMapping("/auto-login")
    public String autoLogin(
            @RequestParam(value = "t", required = false) String token,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        
        // If we reach here, the filter already processed the authentication
        // Either successfully (redirect to dashboard) or failed (redirect to login with error)
        
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                messageSource.getMessage("user.login.invalidToken", null, locale));
            return "redirect:/user/login?error=invalid_token";
        }
        
        // Default redirect (filter should handle most cases)
        return "redirect:/user/";
    }

    /**
     * Handle logout success.
     */
    @GetMapping("/logout-success")
    public String logoutSuccess(RedirectAttributes redirectAttributes, Locale locale) {
        redirectAttributes.addFlashAttribute("successMessage",
            messageSource.getMessage("user.login.loggedOut", null, locale));
        return "redirect:/user/login";
    }
}

