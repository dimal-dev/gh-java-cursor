package com.goodhelp.landing.presentation.web;

import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.common.service.TimezoneHelper;
import com.goodhelp.landing.application.dto.OrderStatusDto;
import com.goodhelp.landing.application.usecase.GetOrderStatusUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API controller for checking order status (AJAX polling).
 */
@RestController
@RequestMapping("/api/checkout")
public class CheckoutStatusApiController {

    private static final Logger log = LoggerFactory.getLogger(CheckoutStatusApiController.class);

    private final GetOrderStatusUseCase getOrderStatusUseCase;
    private final TimezoneHelper timezoneHelper;

    public CheckoutStatusApiController(GetOrderStatusUseCase getOrderStatusUseCase,
                                       TimezoneHelper timezoneHelper) {
        this.getOrderStatusUseCase = getOrderStatusUseCase;
        this.timezoneHelper = timezoneHelper;
    }

    @GetMapping("/status/{slug}")
    public ResponseEntity<Map<String, Object>> checkStatus(@PathVariable String slug,
                                                           HttpServletRequest request) {
        String locale = getLocaleFromRequest(request);
        String userTimezone = resolveUserTimezone(request);

        try {
            OrderStatusDto status = getOrderStatusUseCase.execute(slug, userTimezone, locale);
            Map<String, Object> response = new HashMap<>();
            
            // Map status string to numeric state for compatibility with PHP frontend
            int state = mapStatusToNumericState(status.status());
            response.put("state", state);
            
            if ("approved".equals(status.status()) && status.loginUrl() != null) {
                response.put("l", status.loginUrl());
            }
            
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
            log.warn("Order not found for checkout slug: {}", slug);
            Map<String, Object> response = new HashMap<>();
            response.put("state", 2); // Order not found
            return ResponseEntity.ok(response);
        }
    }

    private int mapStatusToNumericState(String status) {
        return switch (status) {
            case "pending" -> 5; // Processing/pending
            case "approved" -> 4; // Approved
            case "failed" -> 3; // Failed
            default -> 1; // Unknown/created
        };
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

    private String getLocaleFromRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/en/")) {
            return "en";
        } else if (path.startsWith("/ru/")) {
            return "ru";
        }
        return "ua";
    }
}

