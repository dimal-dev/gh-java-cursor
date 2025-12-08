package com.goodhelp.landing.presentation.web;

import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.landing.application.dto.TherapistProfileDto;
import com.goodhelp.landing.application.usecase.GetTherapistProfileUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the therapist profile page.
 * Displays detailed information about a specific therapist.
 */
@Controller
public class TherapistProfileController extends BaseLandingController {

    private final GetTherapistProfileUseCase profileUseCase;

    public TherapistProfileController(GetTherapistProfileUseCase profileUseCase) {
        this.profileUseCase = profileUseCase;
    }

    /**
     * Profile page for Ukrainian (default) language.
     */
    @GetMapping("/profile/{id}")
    public String profileUk(
            @PathVariable Long id,
            @RequestParam(name = "tft", required = false) String priceSlug,
            Model model,
            HttpServletRequest request) {
        return renderProfile(id, priceSlug, model, request);
    }

    /**
     * Profile page for Russian language.
     */
    @GetMapping("/ru/profile/{id}")
    public String profileRu(
            @PathVariable Long id,
            @RequestParam(name = "tft", required = false) String priceSlug,
            Model model,
            HttpServletRequest request) {
        return renderProfile(id, priceSlug, model, request);
    }

    /**
     * Profile page for English language.
     */
    @GetMapping("/en/profile/{id}")
    public String profileEn(
            @PathVariable Long id,
            @RequestParam(name = "tft", required = false) String priceSlug,
            Model model,
            HttpServletRequest request) {
        return renderProfile(id, priceSlug, model, request);
    }

    private String renderProfile(Long id, String priceSlug, Model model, HttpServletRequest request) {
        // Fetch profile
        TherapistProfileDto profile = profileUseCase.execute(id, priceSlug)
            .orElseThrow(() -> new ResourceNotFoundException("Therapist not found"));

        // Add common attributes
        addCommonAttributes(model, request, "profile/" + id);
        
        // Add profile-specific attributes
        model.addAttribute("therapist", profile);
        model.addAttribute("psihologProfile", profile);
        model.addAttribute("psihologProfileInfo", profile);
        model.addAttribute("price", profile.price());
        model.addAttribute("priceFrom", profile.priceFrom());
        model.addAttribute("bookConsultationLink", profile.bookConsultationLink());
        model.addAttribute("topicsGrouped", profile.worksWithGrouped());
        model.addAttribute("reviews", profile.reviews());

        // Set meta tags
        setMetaTags(model, 
            profile.fullName() + " | GoodHelp",
            "Book a consultation with " + profile.fullName() + " - experienced therapist"
        );

        return "landing/therapist-profile";
    }
}

