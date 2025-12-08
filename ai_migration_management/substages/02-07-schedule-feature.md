# Sub-Stage 2.7: Schedule Feature

## Goal
Implement complete schedule management: viewing, setting availability, weekly templates.

---

## Endpoints

| Method | Route | Handler | Description |
|--------|-------|---------|-------------|
| GET | /therapist/schedule | `viewSchedule()` | Schedule page |
| GET | /therapist/schedule/list | `getScheduleData()` | AJAX: slots data |
| GET | /therapist/schedule/settings | `scheduleSettings()` | Daily settings |
| GET | /therapist/schedule/settings/week | `weeklySettings()` | Weekly template |
| POST | /therapist/schedule/book | `toggleSlot()` | AJAX: book/unbook |
| POST | /therapist/consultation/{id}/cancel | `cancelConsultation()` | Cancel booking |

---

## Controller

```java
@Controller
@RequestMapping("/therapist/schedule")
public class TherapistScheduleController {
    
    private final GetScheduleUseCase getScheduleUseCase;
    private final UpdateScheduleSlotUseCase updateSlotUseCase;
    private final CancelConsultationUseCase cancelUseCase;
    
    @GetMapping
    public String viewSchedule(Model model, @AuthenticationPrincipal TherapistUserDetails user) {
        // Load basic page data, JS fetches slots via AJAX
        return "therapist/schedule";
    }
    
    @GetMapping("/list")
    @ResponseBody
    public ScheduleResponse getScheduleData(
            @RequestParam @DateTimeFormat(iso = DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DATE) LocalDate to,
            @AuthenticationPrincipal TherapistUserDetails user) {
        var query = new GetScheduleQuery(user.getId(), from, to);
        return getScheduleUseCase.execute(query);
    }
    
    @PostMapping("/book")
    @ResponseBody
    public ResponseEntity<?> toggleSlot(
            @RequestBody UpdateSlotRequest request,
            @AuthenticationPrincipal TherapistUserDetails user) {
        updateSlotUseCase.execute(new UpdateSlotCommand(
            request.slotId(), 
            request.available() ? SlotStatus.AVAILABLE : SlotStatus.UNAVAILABLE
        ));
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/consultation/{id}/cancel")
    public String cancelConsultation(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            RedirectAttributes redirectAttributes) {
        cancelUseCase.execute(new CancelConsultationCommand(id, reason));
        redirectAttributes.addFlashAttribute("message", "Consultation cancelled");
        return "redirect:/therapist/schedule";
    }
}
```

---

## Schedule Data Structure

```java
public record ScheduleResponse(
    List<ScheduleDay> days
) {}

public record ScheduleDay(
    LocalDate date,
    String dayName,
    List<ScheduleSlotDto> slots
) {}

public record ScheduleSlotDto(
    Long id,
    LocalTime time,
    String status,      // "available", "booked", "unavailable", "done"
    ClientInfo client   // null if not booked
) {}

public record ClientInfo(
    Long userId,
    String name,
    Long consultationId
) {}
```

---

## PHP Reference

| PHP Controller | Notes |
|----------------|-------|
| `ScheduleController.php` | Main schedule page |
| `ScheduleListAjaxController.php` | Returns slots as JSON |
| `ScheduleSettingsController.php` | Daily availability |
| `ScheduleSettingsWeekController.php` | Weekly recurring |
| `ScheduleSettingsBookTimeAjaxController.php` | Toggle slot |

---

## Templates

```
templates/therapist/
├── schedule.html           # Main schedule view
├── schedule-settings.html  # Daily settings
└── schedule-settings-week.html  # Weekly template
```

Key features:
- Calendar grid showing slots
- Color coding: green=available, blue=booked, gray=unavailable
- Click to toggle availability
- View client info for booked slots
- Cancel consultation button

---

## JavaScript

```javascript
// schedule.js
async function loadSchedule(from, to) {
    const response = await fetch(`/therapist/schedule/list?from=${from}&to=${to}`);
    const data = await response.json();
    renderSchedule(data.days);
}

async function toggleSlot(slotId, available) {
    await fetch('/therapist/schedule/book', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ slotId, available })
    });
    refreshSlot(slotId);
}
```

---

## Verification
- [x] Schedule page loads with calendar
- [x] AJAX fetches slots correctly
- [x] Can toggle slot availability
- [x] Booked slots show client info
- [ ] Can cancel consultation (partial - needs billing/notification modules)
- [x] Weekly settings template works
- [x] Time displayed in therapist's timezone

---

## Implementation Status: COMPLETED (2024-12-08)

### Components Created:

**Application Layer:**
- `ConsultationDto.java` - DTO for consultation display
- `WeekInfoDto.java` - DTO for week information
- `TimeSlotSettingDto.java` - DTO for schedule grid slots
- `WeekGridDto.java` - DTO for the weekly grid
- `GetUpcomingConsultationsUseCase.java` - Use case for fetching consultations
- `GetWeekScheduleSettingsUseCase.java` - Use case for weekly schedule grid
- `ToggleSlotByTimeUseCase.java` - Use case for toggling slot availability

**Presentation Layer:**
- `TherapistScheduleController.java` - Controller with all endpoints

**Templates:**
- `schedule.html` - Main schedule page (consultation list)
- `schedule-settings.html` - Schedule settings with week selector
- `fragments/schedule-settings-week.html` - AJAX-loaded weekly grid

**Styles:**
- `static/css/therapist/cabinet.css` - Complete cabinet styling
- `static/js/therapist/cabinet.js` - Common JavaScript functionality

**i18n:**
- Added schedule-related messages to all three language files (EN, UK, RU)

### Notes:
- Consultation cancellation is partially implemented - full cancellation requires
  billing (wallet refunds) and notification (email/Telegram) modules which will
  be implemented in later stages.
- The schedule feature follows the PHP reference for feature parity but uses
  native Java/Spring patterns and DDD architecture.

---

## Next
Proceed to **2.8: Clients Feature**

