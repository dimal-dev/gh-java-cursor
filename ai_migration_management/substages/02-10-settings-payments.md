# Sub-Stage 2.10: Settings & Payments

## Goal
Implement therapist settings page and payments/earnings history.

---

## Settings Endpoints

| Method | Route | Handler |
|--------|-------|---------|
| GET | /therapist/settings | `showSettings()` |
| POST | /therapist/settings | `saveSettings()` |

### Settings Controller
```java
@Controller
@RequestMapping("/therapist/settings")
public class TherapistSettingsController {
    
    @GetMapping
    public String showSettings(Model model, @AuthenticationPrincipal TherapistUserDetails user) {
        var settings = getSettingsUseCase.execute(user.getId());
        model.addAttribute("settings", settings);
        model.addAttribute("timezones", TimezoneService.getAvailableTimezones());
        return "therapist/settings";
    }
    
    @PostMapping
    public String saveSettings(@Valid SettingsForm form,
                              @AuthenticationPrincipal TherapistUserDetails user,
                              RedirectAttributes redirectAttributes) {
        saveSettingsUseCase.execute(new SaveSettingsCommand(user.getId(), form.getTimezone()));
        redirectAttributes.addFlashAttribute("success", "Settings saved");
        return "redirect:/therapist/settings";
    }
}
```

### Settings Features
- Timezone selection (dropdown with common timezones)
- Telegram integration link with setup instructions
- Schedule time cap display (how far ahead users can book)
- Read-only display of email

---

## Payments Endpoints

| Method | Route | Handler |
|--------|-------|---------|
| GET | /therapist/payments | `paymentsOverview()` |
| GET | /therapist/payments/history | `paymentsHistory()` |
| GET | /therapist/payments/history/items | `getHistoryItems()` |

### Payments Controller
```java
@Controller
@RequestMapping("/therapist/payments")
public class TherapistPaymentsController {
    
    @GetMapping
    public String paymentsOverview(Model model, @AuthenticationPrincipal TherapistUserDetails user) {
        var summary = getPaymentsSummaryUseCase.execute(user.getId());
        model.addAttribute("summary", summary);
        return "therapist/payments/index";
    }
    
    @GetMapping("/history")
    public String paymentsHistory() {
        return "therapist/payments/history";
    }
    
    @GetMapping("/history/items")
    @ResponseBody
    public PaymentHistoryResponse getHistoryItems(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal TherapistUserDetails user) {
        return getPaymentHistoryUseCase.execute(user.getId(), page);
    }
}
```

---

## DTOs

```java
public record SettingsDto(
    String email,
    String timezone,
    String telegramSetupLink,
    boolean telegramConnected,
    String scheduleTimeCap
) {}

public record PaymentsSummaryDto(
    Money totalEarnings,
    Money pendingPayout,
    Money lastPayout,
    int totalConsultations,
    int completedConsultations
) {}

public record PaymentHistoryItemDto(
    LocalDateTime date,
    String clientName,
    String consultationType,
    Money amount,
    String status
) {}
```

---

## Telegram Setup

The settings page includes a Telegram setup link:
1. Therapist clicks "Connect Telegram"
2. Opens Telegram with a bot link containing their token
3. Sends `/start` with token to the bot
4. Webhook receives message, links Telegram chat ID to therapist
5. Settings page shows "Connected" status

---

## Templates

```
templates/therapist/
├── settings.html
└── payments/
    ├── index.html      # Overview with earnings
    └── history.html    # Detailed history table
```

---

## Verification
- [ ] Settings page shows current values
- [ ] Can change and save timezone
- [ ] Telegram setup instructions displayed
- [ ] Payments overview shows earnings
- [ ] History loads with pagination
- [ ] Amounts displayed correctly with currency

---

## Next
Proceed to **2.11: Thymeleaf Templates**

