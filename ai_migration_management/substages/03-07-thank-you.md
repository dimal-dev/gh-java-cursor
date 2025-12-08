# Sub-Stage 3.7: Thank You Page

## Goal
Implement the post-payment thank you page with status checking.

---

## Routes
- `GET /checkout/thank-you/{slug}` - Thank you page
- `GET /api/checkout/status/{slug}` - AJAX status check

---

## Controller

```java
@Controller
public class CheckoutThankYouController {
    
    private final GetOrderStatusUseCase orderStatusUseCase;
    
    @GetMapping({"/checkout/thank-you/{slug}", "/{lang}/checkout/thank-you/{slug}"})
    public String thankYou(@PathVariable String slug, Model model) {
        var order = orderStatusUseCase.execute(slug);
        model.addAttribute("order", order);
        model.addAttribute("checkoutSlug", slug);
        return "landing/checkout-thank-you";
    }
}

@RestController
@RequestMapping("/api/checkout")
public class CheckoutStatusApiController {
    
    @GetMapping("/status/{slug}")
    public OrderStatusDto checkStatus(@PathVariable String slug) {
        return orderStatusUseCase.execute(slug);
    }
}
```

---

## Page States

### 1. Processing (Pending)
```html
<div class="status-pending">
    <div class="spinner"></div>
    <h2 th:text="#{landing.thankYou.processing}">Processing your payment...</h2>
    <p th:text="#{landing.thankYou.pleaseWait}">Please wait, this may take a moment</p>
</div>
```

### 2. Success (Approved)
```html
<div class="status-success">
    <div class="success-icon">✓</div>
    <h2 th:text="#{landing.thankYou.success}">Payment Successful!</h2>
    <p th:text="#{landing.thankYou.successMessage}">Your consultation is booked</p>
    
    <div class="booking-details">
        <p><strong th:text="#{landing.thankYou.therapist}">Therapist:</strong> 
           <span th:text="${order.therapistName}"></span></p>
        <p><strong th:text="#{landing.thankYou.date}">Date:</strong> 
           <span th:text="${order.consultationDate}"></span></p>
        <p><strong th:text="#{landing.thankYou.time}">Time:</strong> 
           <span th:text="${order.consultationTime}"></span></p>
    </div>
    
    <p th:text="#{landing.thankYou.emailSent}">A confirmation email has been sent</p>
    
    <a th:href="@{/user/login}" class="btn-primary">
        <span th:text="#{landing.thankYou.goToCabinet}">Go to My Cabinet</span>
    </a>
</div>
```

### 3. Failed
```html
<div class="status-failed">
    <div class="error-icon">✗</div>
    <h2 th:text="#{landing.thankYou.failed}">Payment Failed</h2>
    <p th:text="#{landing.thankYou.failedMessage}">Something went wrong</p>
    <a th:href="@{/checkout/{slug}(slug=${checkoutSlug})}" class="btn-secondary">
        <span th:text="#{landing.thankYou.tryAgain}">Try Again</span>
    </a>
</div>
```

---

## JavaScript Polling

```javascript
// Poll for status while pending
async function checkStatus() {
    const slug = document.getElementById('checkout-slug').value;
    const response = await fetch(`/api/checkout/status/${slug}`);
    const data = await response.json();
    
    if (data.status === 'pending') {
        setTimeout(checkStatus, 2000); // Check every 2 seconds
    } else {
        updateStatusDisplay(data.status);
    }
}

// Start polling if status is pending
if (initialStatus === 'pending') {
    checkStatus();
}
```

---

## Order Status DTO

```java
public record OrderStatusDto(
    String status,  // "pending", "approved", "failed"
    String therapistName,
    String consultationDate,
    String consultationTime,
    String errorMessage  // null unless failed
) {}
```

---

## PHP Reference
- `original_php_project/src/Landing/Controller/CheckoutThankYouController.php`
- `original_php_project/src/Landing/Controller/CheckoutThankYouCheckStatusAjaxController.php`

---

## Verification
- [ ] Page displays correct initial status
- [ ] Polling works for pending payments
- [ ] Success state shows booking details
- [ ] Failed state offers retry option
- [ ] Email sent on success
- [ ] Cabinet link works

---

## Next
Proceed to **3.8: Static Pages**

