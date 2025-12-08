package com.goodhelp.landing.presentation.web;

import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.common.service.TimezoneHelper;
import com.goodhelp.landing.application.dto.BookingTherapistDto;
import com.goodhelp.landing.application.usecase.GetAvailableSlotsUseCase;
import com.goodhelp.landing.application.usecase.GetTherapistForBookingUseCase;
import com.goodhelp.landing.presentation.web.form.BookingForm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the book consultation page.
 */
@Controller
public class BookConsultationController extends BaseLandingController {

    private final GetTherapistForBookingUseCase bookingInfoUseCase;
    private final GetAvailableSlotsUseCase slotsUseCase;
    private final TimezoneHelper timezoneHelper;

    public BookConsultationController(
            GetTherapistForBookingUseCase bookingInfoUseCase,
            GetAvailableSlotsUseCase slotsUseCase,
            TimezoneHelper timezoneHelper) {
        this.bookingInfoUseCase = bookingInfoUseCase;
        this.slotsUseCase = slotsUseCase;
        this.timezoneHelper = timezoneHelper;
    }

    @GetMapping("/book-consultation/{id}")
    public String bookUk(@PathVariable Long id,
                         @RequestParam(name = "tft", required = false) String priceSlug,
                         Model model,
                         HttpServletRequest request) {
        return render(id, priceSlug, model, request);
    }

    @GetMapping("/ru/book-consultation/{id}")
    public String bookRu(@PathVariable Long id,
                         @RequestParam(name = "tft", required = false) String priceSlug,
                         Model model,
                         HttpServletRequest request) {
        return render(id, priceSlug, model, request);
    }

    @GetMapping("/en/book-consultation/{id}")
    public String bookEn(@PathVariable Long id,
                         @RequestParam(name = "tft", required = false) String priceSlug,
                         Model model,
                         HttpServletRequest request) {
        return render(id, priceSlug, model, request);
    }

    private String render(Long id,
                          String priceSlug,
                          Model model,
                          HttpServletRequest request) {
        BookingTherapistDto therapist = bookingInfoUseCase.execute(id, priceSlug)
            .orElseThrow(() -> new ResourceNotFoundException("Therapist not found"));

        String userTimezone = resolveUserTimezone(request);
        var slots = slotsUseCase.execute(id, userTimezone);

        addCommonAttributes(model, request, "book-consultation/" + id);

        BookingForm form = new BookingForm();
        form.setTherapistId(id);
        form.setPriceId(therapist.defaultPriceId());
        form.setAuthType("new");
        model.addAttribute("bookingForm", form);

        model.addAttribute("therapist", therapist);
        model.addAttribute("prices", therapist.prices());
        model.addAttribute("selectedPriceId", therapist.defaultPriceId());
        model.addAttribute("slots", slots);
        model.addAttribute("hasSchedule", slots.hasSlots());
        model.addAttribute("userTimezone", userTimezone);
        model.addAttribute("timezoneLabel", timezoneHelper.getLabelForOffset(
            timezoneHelper.getOffsetForTimezone(userTimezone),
            userTimezone
        ));
        model.addAttribute("requestConsultationLink",
            buildLocalizedUrl("request-therapist", getLocaleFromPath(request)));

        return "landing/book-consultation";
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
}

