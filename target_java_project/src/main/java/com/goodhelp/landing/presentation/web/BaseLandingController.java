package com.goodhelp.landing.presentation.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Locale;

/**
 * Base controller for all landing pages.
 * Provides common functionality for language handling, SEO meta, and URL generation.
 */
public abstract class BaseLandingController {

    /**
     * Adds common model attributes needed by all landing pages.
     * Call this in every controller method.
     */
    protected void addCommonAttributes(Model model, HttpServletRequest request, String pagePath) {
        Locale locale = RequestContextUtils.getLocale(request);
        String lang = locale.getLanguage();
        
        // Add current language
        model.addAttribute("lang", lang);
        
        // Add language-specific URLs for language switcher
        addLanguageUrls(model, pagePath);
        
        // Add timezone detection flag (check if cookie exists)
        boolean timezoneDetected = request.getCookies() != null && 
            java.util.Arrays.stream(request.getCookies())
                .anyMatch(c -> "user_timezone".equals(c.getName()));
        model.addAttribute("timezoneDetected", timezoneDetected);
    }

    /**
     * Generates URLs for all supported languages.
     */
    protected void addLanguageUrls(Model model, String pagePath) {
        // Ukrainian is the default language (no prefix)
        String ukUrl = "/" + pagePath;
        String ruUrl = "/ru/" + pagePath;
        String enUrl = "/en/" + pagePath;
        
        // Clean up double slashes if pagePath is empty
        if (pagePath == null || pagePath.isEmpty()) {
            ukUrl = "/";
            ruUrl = "/ru/";
            enUrl = "/en/";
        }
        
        model.addAttribute("ukUrl", ukUrl);
        model.addAttribute("ruUrl", ruUrl);
        model.addAttribute("enUrl", enUrl);
        model.addAttribute("canonicalUrl", ukUrl); // Default canonical is Ukrainian
    }

    /**
     * Sets SEO meta tags.
     */
    protected void setMetaTags(Model model, String title, String description) {
        model.addAttribute("metaTitle", title);
        model.addAttribute("metaDescription", description);
    }

    /**
     * Sets canonical URL (for SEO).
     */
    protected void setCanonicalUrl(Model model, String canonicalUrl) {
        model.addAttribute("canonicalUrl", canonicalUrl);
    }

    /**
     * Extracts locale code from request path.
     * Returns "uk" if no locale prefix found.
     */
    protected String getLocaleFromPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/ru/") || path.equals("/ru")) {
            return "ru";
        } else if (path.startsWith("/en/") || path.equals("/en")) {
            return "en";
        }
        return "uk";
    }

    /**
     * Builds a localized URL.
     */
    protected String buildLocalizedUrl(String pagePath, String locale) {
        if ("uk".equals(locale) || locale == null) {
            return "/" + pagePath;
        }
        return "/" + locale + "/" + pagePath;
    }
}

