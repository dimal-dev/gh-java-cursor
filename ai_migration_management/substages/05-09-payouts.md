# Sub-Stage 5.9: Payouts Management

## Goal
Implement therapist payout recording and history.

---

## Endpoints

| Method | Route | Handler |
|--------|-------|---------|
| GET | /staff/payouts | `showPayouts()` |
| POST | /staff/payouts | `createPayout()` |
| GET | /staff/payouts/list | `getPayoutsData()` (AJAX) |

---

## Controller

```java
@Controller
@RequestMapping("/staff/payouts")
@PreAuthorize("hasRole('STAFF_SUPERUSER')")
public class StaffPayoutsController {
    
    @GetMapping
    public String showPayouts(Model model) {
        model.addAttribute("form", new CreatePayoutForm());
        model.addAttribute("therapists", getTherapistsWithEarningsUseCase.execute());
        return "staff/payouts/index";
    }
    
    @PostMapping
    public String createPayout(@Valid CreatePayoutForm form,
                              BindingResult result,
                              @AuthenticationPrincipal StaffUserDetails staff,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "staff/payouts/index";
        }
        
        createPayoutUseCase.execute(new CreatePayoutCommand(
            form.getTherapistId(),
            form.getAmount(),
            form.getCurrency(),
            form.getNote(),
            staff.getId()
        ));
        
        redirectAttributes.addFlashAttribute("success", "Payout recorded");
        return "redirect:/staff/payouts";
    }
    
    @GetMapping("/list")
    @ResponseBody
    public PayoutsListResponse getPayoutsData(
            @RequestParam(required = false) Long therapistId,
            @RequestParam(defaultValue = "0") int page) {
        return getPayoutsUseCase.execute(therapistId, page);
    }
}
```

---

## Payout Form

```java
public class CreatePayoutForm {
    @NotNull
    private Long therapistId;
    
    @NotNull @Min(1)
    private Integer amount;  // In cents
    
    @NotBlank
    private String currency;
    
    private String note;
}
```

---

## Payout Data

```java
public record TherapistEarningsDto(
    Long id,
    String fullName,
    int totalEarnings,
    int totalPaidOut,
    int pendingPayout,
    String currency
) {}

public record PayoutHistoryItemDto(
    Long id,
    String therapistName,
    int amount,
    String currency,
    String note,
    LocalDateTime createdAt,
    String createdBy
) {}
```

---

## Template Features

- Therapist selector showing pending payouts
- Amount input
- Note textarea
- Submit button
- History table below form
- Filter by therapist

---

## PHP Reference
- `original_php_project/src/Staff/Controller/PayoutsController.php`

---

## Verification
- [ ] Only superusers can access
- [ ] Therapist list with earnings
- [ ] Can record payout
- [ ] History displays correctly
- [ ] Amounts calculated correctly

---

## Next
Proceed to **5.10: Templates**

