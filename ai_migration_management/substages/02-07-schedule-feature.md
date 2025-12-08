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
- [ ] Schedule page loads with calendar
- [ ] AJAX fetches slots correctly
- [ ] Can toggle slot availability
- [ ] Booked slots show client info
- [ ] Can cancel consultation
- [ ] Weekly settings template works
- [ ] Time displayed in therapist's timezone

---

## Next
Proceed to **2.8: Clients Feature**

