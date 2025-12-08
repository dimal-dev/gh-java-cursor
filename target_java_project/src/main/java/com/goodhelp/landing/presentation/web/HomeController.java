package com.goodhelp.landing.presentation.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controller for the landing home page.
 * Handles the main index page with all language variants.
 */
@Controller
public class HomeController extends BaseLandingController {

    // FAQ items for the home page
    private static final List<FaqItem> FAQ_ITEMS = List.of(
        new FaqItem("faq_main_title_1", "faq_main_body_1"),
        new FaqItem("faq_main_title_2", "faq_main_body_2"),
        new FaqItem("faq_main_title_3", "faq_main_body_3"),
        new FaqItem("faq_main_title_4", "faq_main_body_4"),
        new FaqItem("faq_main_title_5", "faq_main_body_5"),
        new FaqItem("faq_main_title_6", "faq_main_body_6"),
        new FaqItem("faq_main_title_7", "faq_main_body_7")
    );

    /**
     * Home page for Ukrainian (default) language.
     */
    @GetMapping({"", "/"})
    public String indexUk(Model model, HttpServletRequest request) {
        return renderIndex(model, request);
    }

    /**
     * Home page for Russian language.
     */
    @GetMapping({"/ru", "/ru/"})
    public String indexRu(Model model, HttpServletRequest request) {
        return renderIndex(model, request);
    }

    /**
     * Home page for English language.
     */
    @GetMapping({"/en", "/en/"})
    public String indexEn(Model model, HttpServletRequest request) {
        return renderIndex(model, request);
    }

    private String renderIndex(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request, "");
        
        // Header without shadow for hero section
        model.addAttribute("headerWithoutShadow", true);
        
        // Split FAQ into two columns
        int splitIndex = (int) Math.ceil(FAQ_ITEMS.size() / 2.0);
        model.addAttribute("faqColumn1", FAQ_ITEMS.subList(0, splitIndex));
        model.addAttribute("faqColumn2", FAQ_ITEMS.subList(splitIndex, FAQ_ITEMS.size()));
        
        return "landing/index";
    }

    /**
     * Record for FAQ items.
     */
    public record FaqItem(String title, String body) {}
}

