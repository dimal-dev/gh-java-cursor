package com.goodhelp.landing.presentation.web;

import com.goodhelp.landing.application.command.SubmitRequestCommand;
import com.goodhelp.landing.application.service.TopicService;
import com.goodhelp.landing.application.usecase.SubmitRequestUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Locale;
import java.util.Map;

/**
 * Controller for the "Help me find a therapist" request form.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class RequestTherapistController extends BaseLandingController {

    private final SubmitRequestUseCase submitRequestUseCase;
    private final TopicService topicService;

    /**
     * Show the request form.
     */
    @GetMapping({"/request-therapist", "/ru/request-therapist", "/en/request-therapist"})
    public String showForm(
            @PathVariable(value = "lang", required = false) String lang,
            Model model,
            HttpServletRequest request) {
        addCommonAttributes(model, request, "request-therapist");
        setMetaTags(model,
                getMessage(request, "landing.request.title"),
                getMessage(request, "landing.request.description"));

        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RequestTherapistForm());
        }

        // Add topics grouped by category
        Map<String, java.util.List<String>> topicsGrouped = topicService.getAllTopicsGrouped();
        model.addAttribute("topicsGrouped", topicsGrouped);

        return "landing/request-therapist";
    }

    /**
     * Submit the request form.
     */
    @PostMapping({"/request-therapist", "/ru/request-therapist", "/en/request-therapist"})
    public String submitForm(
            @PathVariable(value = "lang", required = false) String lang,
            @Valid RequestTherapistForm form,
            BindingResult result,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            addCommonAttributes(model, request, "request-therapist");
            Map<String, java.util.List<String>> topicsGrouped = topicService.getAllTopicsGrouped();
            model.addAttribute("topicsGrouped", topicsGrouped);
            return "landing/request-therapist";
        }

        try {
            Locale locale = RequestContextUtils.getLocale(request);
            String localeCode = locale.getLanguage();

            // Build command from form
            SubmitRequestCommand command = SubmitRequestCommand.from(
                    form.getName(),
                    form.getEmail(),
                    form.getPhone(),
                    form.getTopics(),
                    form.getMessage(),
                    form.getPreferredTime(),
                    form.getSex(),
                    form.getChannel(),
                    form.getType(),
                    form.getPrice(),
                    form.getPromocode(),
                    form.getTherapistId(),
                    form.getLgbtq(),
                    localeCode
            );

            // Submit request
            Long requestId = submitRequestUseCase.execute(command);

            log.info("Therapist request submitted successfully with ID: {}", requestId);

            // Redirect to success page
            String langPrefix = "uk".equals(localeCode) ? "" : "/" + localeCode;
            return "redirect:" + langPrefix + "/request-therapist/success";

        } catch (Exception e) {
            log.error("Error submitting therapist request", e);
            addCommonAttributes(model, request, "request-therapist");
            Map<String, java.util.List<String>> topicsGrouped = topicService.getAllTopicsGrouped();
            model.addAttribute("topicsGrouped", topicsGrouped);
            model.addAttribute("error", true);
            return "landing/request-therapist";
        }
    }

    /**
     * Show success page after form submission.
     */
    @GetMapping({"/request-therapist/success", "/ru/request-therapist/success", "/en/request-therapist/success"})
    public String success(
            @PathVariable(value = "lang", required = false) String lang,
            Model model,
            HttpServletRequest request) {
        addCommonAttributes(model, request, "request-therapist/success");
        setMetaTags(model,
                getMessage(request, "landing.request.success.title"),
                getMessage(request, "landing.request.success.description"));
        return "landing/request-therapist-success";
    }

    /**
     * Helper method to get localized message.
     */
    private String getMessage(HttpServletRequest request, String key) {
        try {
            org.springframework.context.MessageSource messageSource =
                    org.springframework.web.context.support.WebApplicationContextUtils
                            .getRequiredWebApplicationContext(request.getServletContext())
                            .getBean(org.springframework.context.MessageSource.class);
            Locale locale = RequestContextUtils.getLocale(request);
            String result = messageSource.getMessage(key, null, key, locale);
            return result != null ? result : key;
        } catch (Exception e) {
            return key;
        }
    }
}

