# Sub-Stage 4.7: User Consultations

## Goal
Implement consultation viewing and cancellation.

---

## Endpoints

| Method | Route | Handler |
|--------|-------|---------|
| GET | /user/consultations | `listConsultations()` |
| GET | /user/consultation/{id} | `viewConsultation()` |
| POST | /user/consultation/{id}/cancel | `cancelConsultation()` |

---

## Controller

```java
@Controller
@RequestMapping("/user")
public class UserConsultationsController {
    
    @GetMapping("/consultations")
    public String listConsultations(Model model, 
                                   @AuthenticationPrincipal GoodHelpUserDetails user) {
        var consultations = getConsultationsUseCase.execute(user.getId());
        model.addAttribute("consultations", consultations);
        return "user/consultations";
    }
    
    @PostMapping("/consultation/{id}/cancel")
    public String cancelConsultation(
            @PathVariable Long id,
            @AuthenticationPrincipal GoodHelpUserDetails user,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        try {
            cancelConsultationUseCase.execute(
                new CancelConsultationCommand(id, user.getId(), CancelledBy.USER));
            redirectAttributes.addFlashAttribute("success", 
                messageSource.getMessage("user.consultation.cancelled", null, locale));
        } catch (CannotCancelException e) {
            redirectAttributes.addFlashAttribute("error", 
                messageSource.getMessage("user.consultation.cannotCancel", null, locale));
        }
        return "redirect:/user/";
    }
}
```

---

## Cancellation Rules

From PHP `ConsultationAllowedCancelTimeChecker`:
- User can cancel **free** if more than 24 hours before session
- User can cancel **with penalty** if less than 24 hours
- User **cannot cancel** if less than 1 hour before

```java
public class CancellationPolicy {
    private static final Duration FREE_CANCELLATION_WINDOW = Duration.ofHours(24);
    private static final Duration MINIMUM_CANCELLATION_WINDOW = Duration.ofHours(1);
    
    public CancellationResult checkCancellation(UserConsultation consultation, LocalDateTime now) {
        LocalDateTime startTime = consultation.getSlot().getStartTime();
        Duration timeUntil = Duration.between(now, startTime);
        
        if (timeUntil.compareTo(MINIMUM_CANCELLATION_WINDOW) < 0) {
            return CancellationResult.NOT_ALLOWED;
        } else if (timeUntil.compareTo(FREE_CANCELLATION_WINDOW) >= 0) {
            return CancellationResult.FREE_CANCELLATION;
        } else {
            return CancellationResult.WITH_PENALTY;
        }
    }
}
```

---

## DTO

```java
public record UserConsultationDto(
    Long id,
    String therapistName,
    String therapistPhotoUrl,
    LocalDateTime scheduledAt,
    String formattedDateTime,
    String type,           // Individual or Couple
    String priceFormatted,
    String status,
    boolean canBeCancelled,
    String cancellationNote  // "Free cancellation" or "Penalty applies"
) {}
```

---

## Template Features

- List of all consultations (upcoming and past)
- Filter by status (upcoming/completed/cancelled)
- Cancellation confirmation modal
- Shows penalty warning if applicable

---

## PHP Reference
- `original_php_project/src/User/Controller/CancelConsultationController.php`
- `original_php_project/src/Common/Service/ConsultationAllowedCancelTimeChecker.php`

---

## Verification
- [ ] All consultations listed
- [ ] Status filtering works
- [ ] Free cancellation works
- [ ] Penalty cancellation shows warning
- [ ] Cannot cancel within 1 hour
- [ ] Success/error messages displayed

---

## Next
Proceed to **4.8: Settings**

