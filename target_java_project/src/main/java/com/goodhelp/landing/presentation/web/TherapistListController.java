package com.goodhelp.landing.presentation.web;

import com.goodhelp.landing.application.dto.TherapistListItemDto;
import com.goodhelp.landing.application.usecase.GetTherapistCatalogUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller for the therapist listing page.
 * Displays the catalog of available therapists.
 */
@Controller
public class TherapistListController extends BaseLandingController {

    private final GetTherapistCatalogUseCase catalogUseCase;

    public TherapistListController(GetTherapistCatalogUseCase catalogUseCase) {
        this.catalogUseCase = catalogUseCase;
    }

    /**
     * Therapist list for Ukrainian (default) language.
     */
    @GetMapping("/psiholog-list")
    public String listUk(
            @RequestParam(required = false) String topic,
            Model model,
            HttpServletRequest request) {
        return renderList(model, request, topic);
    }

    /**
     * Therapist list for Russian language.
     */
    @GetMapping("/ru/psiholog-list")
    public String listRu(
            @RequestParam(required = false) String topic,
            Model model,
            HttpServletRequest request) {
        return renderList(model, request, topic);
    }

    /**
     * Therapist list for English language.
     */
    @GetMapping("/en/psiholog-list")
    public String listEn(
            @RequestParam(required = false) String topic,
            Model model,
            HttpServletRequest request) {
        return renderList(model, request, topic);
    }

    private String renderList(Model model, HttpServletRequest request, String topic) {
        addCommonAttributes(model, request, "psiholog-list");
        
        // Fetch therapists
        List<TherapistListItemDto> therapists;
        if (topic != null && !topic.isBlank()) {
            therapists = catalogUseCase.execute(topic);
            model.addAttribute("selectedTopic", topic);
        } else {
            therapists = catalogUseCase.execute();
        }
        
        model.addAttribute("psihologList", therapists);
        
        return "landing/psiholog-list";
    }
}

