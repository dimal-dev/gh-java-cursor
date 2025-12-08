package com.goodhelp.landing.presentation.web;

import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.common.service.TimezoneHelper;
import com.goodhelp.landing.application.dto.CheckoutSummaryDto;
import com.goodhelp.landing.application.dto.PaymentFormDataDto;
import com.goodhelp.landing.application.usecase.CreateCheckoutCommand;
import com.goodhelp.landing.application.usecase.CreateCheckoutUseCase;
import com.goodhelp.landing.application.usecase.GetCheckoutUseCase;
import com.goodhelp.landing.application.usecase.PaymentContext;
import com.goodhelp.landing.application.usecase.PreparePaymentUseCase;
import com.goodhelp.landing.presentation.web.form.BookingForm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Handles checkout creation and rendering.
 */
@Controller
public class CheckoutController extends BaseLandingController {

    private static final Logger log = LoggerFactory.getLogger(CheckoutController.class);
    private static final Pattern GA_PREFIX = Pattern.compile("GA\\d+\\.\\d+\\.");

    private final CreateCheckoutUseCase createCheckoutUseCase;
    private final GetCheckoutUseCase getCheckoutUseCase;
    private final PreparePaymentUseCase preparePaymentUseCase;
    private final TimezoneHelper timezoneHelper;

    public CheckoutController(CreateCheckoutUseCase createCheckoutUseCase,
                              GetCheckoutUseCase getCheckoutUseCase,
                              PreparePaymentUseCase preparePaymentUseCase,
                              TimezoneHelper timezoneHelper) {
        this.createCheckoutUseCase = createCheckoutUseCase;
        this.getCheckoutUseCase = getCheckoutUseCase;
        this.preparePaymentUseCase = preparePaymentUseCase;
        this.timezoneHelper = timezoneHelper;
    }

    @PostMapping({"/checkout", "/{lang}/checkout"})
    public String createCheckout(@PathVariable(value = "lang", required = false) String lang,
                                 @Valid @ModelAttribute("bookingForm") BookingForm form,
                                 BindingResult bindingResult,
                                 Model model,
                                 HttpServletRequest request) {
        String locale = determineLocale(lang, request);
        if (bindingResult.hasErrors() || hasClientSideValidationErrors(form, bindingResult)) {
            return redirectToBooking(form, locale);
        }

        try {
            CheckoutSummaryDto checkout = createCheckoutUseCase.execute(
                CreateCheckoutCommand.from(
                    form,
                    resolveUserTimezone(request),
                    locale,
                    extractGaClientId(request),
                    extractGaClientIdOriginal(request)
                )
            );
            return renderCheckoutPage(checkout, model, request, locale);
        } catch (IllegalArgumentException | ResourceNotFoundException ex) {
            log.warn("Failed to create checkout: {}", ex.getMessage());
            return redirectToBooking(form, locale);
        }
    }

    @GetMapping({"/checkout/{slug}", "/{lang}/checkout/{slug}"})
    public String resumeCheckout(@PathVariable(value = "lang", required = false) String lang,
                                 @PathVariable String slug,
                                 Model model,
                                 HttpServletRequest request) {
        String locale = determineLocale(lang, request);
        try {
            CheckoutSummaryDto checkout = getCheckoutUseCase.execute(
                slug,
                resolveUserTimezone(request),
                Locale.forLanguageTag(locale)
            );
            return renderCheckoutPage(checkout, model, request, locale);
        } catch (ResourceNotFoundException ex) {
            log.warn("Checkout not found for slug {}", slug);
            return "redirect:" + buildLocalizedUrl("", locale);
        }
    }

    private String renderCheckoutPage(CheckoutSummaryDto checkout,
                                      Model model,
                                      HttpServletRequest request,
                                      String locale) {
        addCommonAttributes(model, request, "checkout/" + checkout.slug());
        PaymentFormDataDto payment = preparePaymentUseCase.execute(
            checkout,
            buildPaymentContext(request, locale)
        );

        model.addAttribute("checkout", checkout);
        model.addAttribute("payment", payment);
        return "landing/checkout";
    }

    private PaymentContext buildPaymentContext(HttpServletRequest request, String locale) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String returnPath = buildLocalizedUrl("checkout/thank-you", locale);
        String servicePath = "/billing/webhook";
        return new PaymentContext(
            Locale.forLanguageTag(locale),
            baseUrl,
            returnPath,
            servicePath,
            request.getHeader("Cookie")
        );
    }

    private boolean hasClientSideValidationErrors(BookingForm form, BindingResult bindingResult) {
        String authType = form.getAuthType() != null ? form.getAuthType().toLowerCase(Locale.ROOT) : "new";
        if ("new".equals(authType)) {
            if (!StringUtils.hasText(form.getName())) {
                bindingResult.rejectValue("name", "NotBlank");
            }
            if (!StringUtils.hasText(form.getPhone())) {
                bindingResult.rejectValue("phone", "NotBlank");
            }
        }
        return bindingResult.hasErrors();
    }

    private String resolveUserTimezone(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("user_timezone".equals(cookie.getName()) && timezoneHelper.isValidTimezone(cookie.getValue())) {
                    return cookie.getValue();
                }
            }
        }
        return TimezoneHelper.DEFAULT_TIMEZONE;
    }

    private String extractGaClientId(HttpServletRequest request) {
        Cookie gaCookie = findCookie(request, "_ga");
        if (gaCookie == null) {
            return null;
        }
        String value = gaCookie.getValue();
        return GA_PREFIX.matcher(value).replaceFirst("");
    }

    private String extractGaClientIdOriginal(HttpServletRequest request) {
        Cookie gaCookie = findCookie(request, "_ga");
        return gaCookie != null ? gaCookie.getValue() : null;
    }

    private String redirectToBooking(BookingForm form, String locale) {
        String therapistId = form.getTherapistId() != null ? form.getTherapistId().toString() : "";
        return "redirect:" + buildLocalizedUrl("book-consultation/" + therapistId, locale);
    }

    private Cookie findCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
            .filter(c -> name.equals(c.getName()))
            .findFirst()
            .orElse(null);
    }

    private String determineLocale(String lang, HttpServletRequest request) {
        if (StringUtils.hasText(lang)) {
            return lang;
        }
        return getLocaleFromPath(request);
    }
}

