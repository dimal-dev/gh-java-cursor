# Sub-Stage 3.5: Book Consultation Page

## Goal
Implement the booking page where users select time slots and enter contact info.

---

## Routes
- `GET /{lang}/book-consultation/{therapistId}`
- `POST /api/book-consultation/apply-promocode` (AJAX)

---

## Controller

```java
@Controller
public class BookConsultationController {
    
    private final GetTherapistForBookingUseCase bookingInfoUseCase;
    private final GetAvailableSlotsUseCase slotsUseCase;
    
    @GetMapping({"/{lang}/book-consultation/{id}", "/book-consultation/{id}"})
    public String bookConsultation(@PathVariable(required = false) String lang,
                                   @PathVariable Long id,
                                   Model model) {
        var therapist = bookingInfoUseCase.execute(id);
        var slots = slotsUseCase.execute(id);
        
        model.addAttribute("therapist", therapist);
        model.addAttribute("slots", slots);
        model.addAttribute("bookingForm", new BookingForm());
        addLanguageUrls(model, "book-consultation/" + id);
        
        return "landing/book-consultation";
    }
}

@RestController
@RequestMapping("/api/book-consultation")
public class BookConsultationApiController {
    
    @PostMapping("/apply-promocode")
    public PromocodeResult applyPromocode(@RequestBody ApplyPromocodeRequest request) {
        return promocodeUseCase.execute(request.code(), request.priceId());
    }
}
```

---

## Page Sections

### 1. Therapist Summary
- Mini profile card
- Selected price display

### 2. Price Selection
- Radio buttons for Individual/Couple
- Price displayed with each option

### 3. Time Slot Selection
- Calendar view showing available dates
- Time slots for selected date
- Timezone indicator

### 4. Contact Information
- Auth type: "New User" / "Existing User"
- Email field
- Phone field
- Name field

### 5. Promocode Section
- Input field for code
- "Apply" button
- Discount display when applied

### 6. Summary & Continue
- Selected slot
- Price (with discount if applied)
- "Continue to Payment" button

---

## Form Structure

```java
public class BookingForm {
    @NotNull private Long priceId;
    @NotNull private Long slotId;
    @NotBlank @Email private String email;
    private String phone;
    private String name;
    private String promocode;
    private String authType;  // "new" or "existing"
}
```

---

## JavaScript Features

```javascript
// Time slot selection
function selectSlot(slotId, time) {
    document.getElementById('slotId').value = slotId;
    highlightSelectedSlot(slotId);
    updateSummary();
}

// Promocode application
async function applyPromocode() {
    const code = document.getElementById('promocode').value;
    const priceId = document.getElementById('priceId').value;
    
    const response = await fetch('/api/book-consultation/apply-promocode', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ code, priceId })
    });
    
    const result = await response.json();
    if (result.valid) {
        displayDiscount(result.discountPercent, result.newPrice);
    } else {
        showError(result.message);
    }
}

// Timezone detection
const userTimezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
```

---

## Slot Display DTOs

```java
public record AvailableSlotsDto(
    List<SlotDayDto> days
) {}

public record SlotDayDto(
    LocalDate date,
    String dayName,
    List<SlotTimeDto> times
) {}

public record SlotTimeDto(
    Long id,
    LocalTime time,
    String formatted  // "14:00"
) {}
```

---

## PHP Reference
- `original_php_project/src/Landing/Controller/BookConsultationController.php`
- `original_php_project/src/Landing/Controller/BookConsultationApplyPromocodeController.php`
- `original_php_project/src/Landing/Resources/templates/pages/book-consultation.html.twig`

---

## Verification
- [ ] Page loads with therapist info
- [ ] Price options selectable
- [ ] Available slots displayed
- [ ] Can select time slot
- [ ] Contact form validates
- [ ] Promocode application works
- [ ] Timezone displayed correctly
- [ ] Form submits to checkout

---

## Next
Proceed to **3.6: Checkout Page**

