package com.goodhelp.landing.presentation.web;

import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.common.service.TimezoneHelper;
import com.goodhelp.landing.application.dto.OrderStatusDto;
import com.goodhelp.landing.application.usecase.GetOrderStatusUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.util.StringUtils;

/**
 * Controller for the thank you page after checkout.
 */
@Controller
public class CheckoutThankYouController extends BaseLandingController {

    private static final Logger log = LoggerFactory.getLogger(CheckoutThankYouController.class);

    private final GetOrderStatusUseCase getOrderStatusUseCase;
    private final TimezoneHelper timezoneHelper;

    public CheckoutThankYouController(GetOrderStatusUseCase getOrderStatusUseCase,
                                      TimezoneHelper timezoneHelper) {
        this.getOrderStatusUseCase = getOrderStatusUseCase;
        this.timezoneHelper = timezoneHelper;
    }

    @GetMapping({"/checkout/thank-you/{slug}", "/{lang}/checkout/thank-you/{slug}"})
    public String thankYou(@PathVariable(value = "lang", required = false) String lang,
                           @PathVariable String slug,
                           Model model,
                           HttpServletRequest request) {
        String locale = determineLocale(lang, request);
        String userTimezone = resolveUserTimezone(request);

        try {
            OrderStatusDto orderStatus = getOrderStatusUseCase.execute(slug, userTimezone, locale);
            addCommonAttributes(model, request, "checkout/thank-you/" + slug);
            model.addAttribute("order", orderStatus);
            model.addAttribute("checkoutSlug", slug);
            return "landing/checkout-thank-you";
        } catch (ResourceNotFoundException ex) {
            log.warn("Order not found for checkout slug: {}", slug);
            return "redirect:" + buildLocalizedUrl("", locale);
        }
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

    private String determineLocale(String lang, HttpServletRequest request) {
        if (StringUtils.hasText(lang)) {
            return lang;
        }
        return getLocaleFromPath(request);
    }
}

