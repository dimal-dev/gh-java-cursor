package com.goodhelp.therapist.presentation.web;

import com.goodhelp.therapist.application.dto.ConsultationDto;
import com.goodhelp.therapist.application.dto.WeekGridDto;
import com.goodhelp.therapist.application.dto.WeekInfoDto;
import com.goodhelp.therapist.application.usecase.GetUpcomingConsultationsUseCase;
import com.goodhelp.therapist.application.usecase.GetWeekScheduleSettingsUseCase;
import com.goodhelp.therapist.application.usecase.ToggleSlotByTimeUseCase;
import com.goodhelp.therapist.infrastructure.security.TherapistUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for therapist schedule management.
 * 
 * Provides endpoints for:
 * - Viewing upcoming consultations
 * - Managing schedule availability
 * - Weekly schedule settings grid
 */
@Controller
@RequestMapping("/therapist/schedule")
public class TherapistScheduleController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Minimum time before consultation for penalty-free cancellation (24 hours in seconds)
    private static final int MINIMUM_CANCEL_DELTA_SECONDS = 24 * 60 * 60;

    private final GetUpcomingConsultationsUseCase getConsultationsUseCase;
    private final GetWeekScheduleSettingsUseCase getWeekSettingsUseCase;
    private final ToggleSlotByTimeUseCase toggleSlotUseCase;

    public TherapistScheduleController(
            GetUpcomingConsultationsUseCase getConsultationsUseCase,
            GetWeekScheduleSettingsUseCase getWeekSettingsUseCase,
            ToggleSlotByTimeUseCase toggleSlotUseCase) {
        this.getConsultationsUseCase = getConsultationsUseCase;
        this.getWeekSettingsUseCase = getWeekSettingsUseCase;
        this.toggleSlotUseCase = toggleSlotUseCase;
    }

    /**
     * Display the schedule page with upcoming consultations.
     * GET /therapist/schedule
     */
    @GetMapping
    public String viewSchedule(
            Model model,
            @AuthenticationPrincipal TherapistUserDetails user) {
        
        model.addAttribute("minimumCancelDelta", MINIMUM_CANCEL_DELTA_SECONDS);
        return "therapist/schedule";
    }

    /**
     * AJAX endpoint to get consultation list data.
     * GET /therapist/schedule/list
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<ScheduleListResponse> getScheduleList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int perpage,
            @AuthenticationPrincipal TherapistUserDetails user) {

        var params = GetUpcomingConsultationsUseCase.QueryParams.of(
            user.getId(), page, perpage
        );
        var result = getConsultationsUseCase.execute(params);

        return ResponseEntity.ok(new ScheduleListResponse(
            result.consultations(),
            new MetaInfo(page, perpage, result.total())
        ));
    }

    /**
     * Display the schedule settings page with week selector.
     * GET /therapist/schedule/settings
     */
    @GetMapping("/settings")
    public String scheduleSettings(
            Model model,
            @AuthenticationPrincipal TherapistUserDetails user) {
        
        LocalDate today = LocalDate.now();
        
        model.addAttribute("zeroWeek", WeekInfoDto.forWeekOffset(0, today));
        model.addAttribute("oneWeek", WeekInfoDto.forWeekOffset(1, today));
        model.addAttribute("twoWeek", WeekInfoDto.forWeekOffset(2, today));
        model.addAttribute("threeWeek", WeekInfoDto.forWeekOffset(3, today));
        
        return "therapist/schedule-settings";
    }

    /**
     * AJAX endpoint to get the weekly schedule grid.
     * GET /therapist/schedule/settings/week
     */
    @GetMapping("/settings/week")
    public String getWeekSchedule(
            @RequestParam String weekDayFirst,
            Model model,
            @AuthenticationPrincipal TherapistUserDetails user) {

        LocalDateTime mondayDateTime = LocalDateTime.parse(weekDayFirst, DATE_FORMATTER);
        LocalDate mondayDate = mondayDateTime.toLocalDate();

        var query = new GetWeekScheduleSettingsUseCase.Query(user.getId(), mondayDate);
        WeekGridDto grid = getWeekSettingsUseCase.execute(query);

        model.addAttribute("timeList", grid.timeRows());
        model.addAttribute("dayList", grid.dayHeaders());
        model.addAttribute("slotStates", SlotStateConstants.asMap());

        return "therapist/fragments/schedule-settings-week";
    }

    /**
     * AJAX endpoint to toggle a time slot.
     * POST /therapist/schedule/book
     */
    @PostMapping("/book")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleSlot(
            @RequestBody ToggleSlotRequest request,
            @AuthenticationPrincipal TherapistUserDetails user) {

        System.out.println("📇 Entering a great thing!");

        LocalDateTime time = LocalDateTime.parse(request.time(), DATE_FORMATTER);
        
        var command = new ToggleSlotByTimeUseCase.Command(
            user.getId(),
            time,
            request.action()
        );
        
        var result = toggleSlotUseCase.execute(command);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.succeeded());
        if (!result.succeeded()) {
            response.put("message", result.message());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel a consultation.
     * POST /therapist/schedule/consultation/{id}/cancel
     */
    @PostMapping("/consultation/{id}/cancel")
    public String cancelConsultation(
            @PathVariable Long id,
            @RequestParam(required = false) String redirectTo,
            @AuthenticationPrincipal TherapistUserDetails user) {
        
        // TODO: Implement consultation cancellation use case
        // This requires the billing and notification modules which aren't built yet
        // For now, we'll redirect back to the schedule page
        
        if (redirectTo != null && !redirectTo.isBlank()) {
            return "redirect:" + redirectTo;
        }
        return "redirect:/therapist/schedule";
    }

    // ==================== Request/Response Records ====================

    /**
     * Response for schedule list AJAX endpoint.
     */
    public record ScheduleListResponse(
        List<ConsultationDto> data,
        MetaInfo meta
    ) {}

    /**
     * Pagination meta info.
     */
    public record MetaInfo(int page, int perpage, int total) {}

    /**
     * Request body for toggling a slot.
     */
    public record ToggleSlotRequest(String time, int action) {}

    /**
     * Constants for slot states (used in templates).
     */
    public static class SlotStateConstants {
        public static final int STATE_UNUSED = 1;
        public static final int STATE_AVAILABLE = 2;
        public static final int STATE_BOOKED = 3;
        public static final int STATE_DONE = 4;
        public static final int STATE_PASSED = 15;

        public static Map<String, Integer> asMap() {
            Map<String, Integer> map = new HashMap<>();
            map.put("unused", STATE_UNUSED);
            map.put("available", STATE_AVAILABLE);
            map.put("booked", STATE_BOOKED);
            map.put("done", STATE_DONE);
            map.put("passed", STATE_PASSED);
            return map;
        }
    }
}

