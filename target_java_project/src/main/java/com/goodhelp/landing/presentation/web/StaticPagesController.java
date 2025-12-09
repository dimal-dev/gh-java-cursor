package com.goodhelp.landing.presentation.web;

import com.goodhelp.landing.application.dto.TherapistListItemDto;
import com.goodhelp.landing.application.usecase.GetTherapistCatalogUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Controller for static informational pages.
 * Handles about, prices, conditions, privacy, terms, refund, and SEO landing pages.
 */
@Controller
public class StaticPagesController extends BaseLandingController {

    private final GetTherapistCatalogUseCase catalogUseCase;

    public StaticPagesController(GetTherapistCatalogUseCase catalogUseCase) {
        this.catalogUseCase = catalogUseCase;
    }

    // ========== About Page ==========

    @GetMapping({"/about", "/ru/about", "/en/about"})
    public String about(@PathVariable(value = "lang", required = false) String lang,
                       Model model,
                       HttpServletRequest request) {
        addCommonAttributes(model, request, "about");
        setMetaTags(model, 
            getMessage(request, "landing.about.title"),
            getMessage(request, "landing.about.description"));
        return "landing/static/about";
    }

    // ========== Prices Page ==========

    @GetMapping({"/prices", "/ru/prices", "/en/prices"})
    public String prices(@PathVariable(value = "lang", required = false) String lang,
                        Model model,
                        HttpServletRequest request) {
        addCommonAttributes(model, request, "prices");
        setMetaTags(model,
            getMessage(request, "landing.prices.title"),
            getMessage(request, "landing.prices.description"));
        return "landing/static/prices";
    }

    // ========== Consultation Conditions Page ==========

    @GetMapping({"/consultation-conditions", "/ru/consultation-conditions", "/en/consultation-conditions"})
    public String consultationConditions(@PathVariable(value = "lang", required = false) String lang,
                                       Model model,
                                       HttpServletRequest request) {
        addCommonAttributes(model, request, "consultation-conditions");
        setMetaTags(model,
            getMessage(request, "landing.conditions.title"),
            getMessage(request, "landing.conditions.description"));
        return "landing/static/conditions";
    }

    // ========== Privacy Policy Page ==========

    @GetMapping({"/privacy", "/ru/privacy", "/en/privacy"})
    public String privacy(@PathVariable(value = "lang", required = false) String lang,
                         Model model,
                         HttpServletRequest request) {
        addCommonAttributes(model, request, "privacy");
        setMetaTags(model,
            getMessage(request, "landing.privacy.title"),
            getMessage(request, "landing.privacy.description"));
        return "landing/static/privacy";
    }

    // ========== Terms of Use Page ==========

    @GetMapping({"/terms-of-use", "/ru/terms-of-use", "/en/terms-of-use"})
    public String termsOfUse(@PathVariable(value = "lang", required = false) String lang,
                            Model model,
                            HttpServletRequest request) {
        addCommonAttributes(model, request, "terms-of-use");
        setMetaTags(model,
            getMessage(request, "landing.terms.title"),
            getMessage(request, "landing.terms.description"));
        return "landing/static/terms";
    }

    // ========== Refund Policy Page ==========

    @GetMapping({"/refund-policy", "/ru/refund-policy", "/en/refund-policy"})
    public String refundPolicy(@PathVariable(value = "lang", required = false) String lang,
                              Model model,
                              HttpServletRequest request) {
        addCommonAttributes(model, request, "refund-policy");
        setMetaTags(model,
            getMessage(request, "landing.refund.title"),
            getMessage(request, "landing.refund.description"));
        return "landing/static/refund";
    }

    // ========== Family Therapist SEO Landing Page ==========

    @GetMapping({"/family-psiholog", "/ru/family-psiholog", "/en/family-psiholog"})
    public String familyTherapist(@PathVariable(value = "lang", required = false) String lang,
                                 Model model,
                                 HttpServletRequest request) {
        addCommonAttributes(model, request, "family-psiholog");
        setMetaTags(model,
            getMessage(request, "landing.family.title"),
            getMessage(request, "landing.family.description"));
        
        // Get therapists (filtered by family specialty in the future)
        // For now, show all therapists - can be enhanced when specialty filtering is implemented
        List<TherapistListItemDto> therapists = catalogUseCase.execute();
        // Limit to 6 therapists for display
        model.addAttribute("therapists", therapists.subList(0, Math.min(6, therapists.size())));
        
        return "landing/seo/family";
    }

    // ========== Teenage Therapist SEO Landing Page ==========

    @GetMapping({"/teenage-psiholog", "/ru/teenage-psiholog", "/en/teenage-psiholog"})
    public String teenageTherapist(@PathVariable(value = "lang", required = false) String lang,
                                  Model model,
                                  HttpServletRequest request) {
        addCommonAttributes(model, request, "teenage-psiholog");
        setMetaTags(model,
            getMessage(request, "landing.teenage.title"),
            getMessage(request, "landing.teenage.description"));
        
        // Get therapists (filtered by teenage specialty in the future)
        // For now, show all therapists - can be enhanced when specialty filtering is implemented
        List<TherapistListItemDto> therapists = catalogUseCase.execute();
        // Limit to 6 therapists for display
        model.addAttribute("therapists", therapists.subList(0, Math.min(6, therapists.size())));
        
        return "landing/seo/teenage";
    }

    // ========== Therapist Apply Page ==========

    @GetMapping({"/therapist-apply", "/ru/therapist-apply", "/en/therapist-apply"})
    public String therapistApply(@PathVariable(value = "lang", required = false) String lang,
                                Model model,
                                HttpServletRequest request) {
        addCommonAttributes(model, request, "therapist-apply");
        setMetaTags(model,
            getMessage(request, "landing.apply.title"),
            getMessage(request, "landing.apply.description"));
        return "landing/seo/apply";
    }

    /**
     * Helper method to get localized message.
     * Uses Spring's MessageSource via RequestContextUtils.
     */
    private String getMessage(HttpServletRequest request, String key) {
        try {
            org.springframework.context.MessageSource messageSource = 
                org.springframework.web.context.support.WebApplicationContextUtils
                    .getRequiredWebApplicationContext(request.getServletContext())
                    .getBean(org.springframework.context.MessageSource.class);
            java.util.Locale locale = org.springframework.web.servlet.support.RequestContextUtils.getLocale(request);
            String result = messageSource.getMessage(key, null, key, locale);
            return result != null ? result : key;
        } catch (Exception e) {
            return key; // Fallback to key if message not found
        }
    }
}

