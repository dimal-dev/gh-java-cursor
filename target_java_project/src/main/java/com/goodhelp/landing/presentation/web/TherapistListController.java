package com.goodhelp.landing.presentation.web;

import com.goodhelp.landing.application.dto.TherapistListItemDto;
import com.goodhelp.landing.application.usecase.GetTherapistCatalogUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/therapist-list")
    public String listUk(
            @RequestParam(required = false) String topic,
            Model model,
            HttpServletRequest request) {
        return renderList(model, request, topic);
    }

    /**
     * Therapist list for Russian language.
     */
    @GetMapping("/ru/therapist-list")
    public String listRu(
            @RequestParam(required = false) String topic,
            Model model,
            HttpServletRequest request) {
        return renderList(model, request, topic);
    }

    /**
     * Therapist list for English language.
     */
    @GetMapping("/en/therapist-list")
    public String listEn(
            @RequestParam(required = false) String topic,
            Model model,
            HttpServletRequest request) {
        return renderList(model, request, topic);
    }

    private String renderList(Model model, HttpServletRequest request, String topic) {
        addCommonAttributes(model, request, "therapist-list");
        
        // Fetch therapists
        List<TherapistListItemDto> therapists;
        if (topic != null && !topic.isBlank()) {
            therapists = catalogUseCase.execute(topic);
            model.addAttribute("selectedTopic", topic);
        } else {
            therapists = catalogUseCase.execute();
        }
        
        model.addAttribute("therapistList", therapists);

        return "landing/therapist-list";
    }

    @GetMapping("/json/therapist-list")
    public ResponseEntity<List<TherapistListItemDto>> listJson(
            @RequestParam(required = false) String topic,
            Model model,
            HttpServletRequest request) {
        return ResponseEntity.ok(_renderList(model, request, topic));
    }

    private List<TherapistListItemDto> _renderList(Model model, HttpServletRequest request, String topic) {
        addCommonAttributes(model, request, "therapist-list");

        // Fetch therapists
        List<TherapistListItemDto> therapists;
        if (topic != null && !topic.isBlank()) {
            therapists = catalogUseCase.execute(topic);
            model.addAttribute("selectedTopic", topic);
        } else {
            therapists = catalogUseCase.execute();
        }

        model.addAttribute("therapistList", therapists);

        return therapists;
    }
}

