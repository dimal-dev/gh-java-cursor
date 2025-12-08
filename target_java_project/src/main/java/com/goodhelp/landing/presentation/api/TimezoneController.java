package com.goodhelp.landing.presentation.api;

import com.goodhelp.common.service.TimezoneHelper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

/**
 * Stores user's timezone in a cookie for later use on landing pages.
 */
@RestController
@RequestMapping("/api")
public class TimezoneController {

    private final TimezoneHelper timezoneHelper;

    public TimezoneController(TimezoneHelper timezoneHelper) {
        this.timezoneHelper = timezoneHelper;
    }

    @PostMapping("/save-timezone")
    public ResponseEntity<Void> saveTimezone(@RequestBody Map<String, String> payload, HttpServletResponse response) {
        String tz = payload.get("tz");
        if (tz == null || !timezoneHelper.isValidTimezone(tz)) {
            return ResponseEntity.badRequest().build();
        }

        ResponseCookie cookie = ResponseCookie.from("user_timezone", tz)
            .path("/")
            .maxAge(Duration.ofDays(365))
            .httpOnly(false)
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }
}

