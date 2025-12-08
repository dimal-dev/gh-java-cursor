# Sub-Stage 3.6: Checkout Page

## Goal
Implement checkout page with payment form and WayForPay integration.

---

## Routes
- `POST /checkout` - Create checkout and show payment page
- `GET /checkout/{slug}` - Resume checkout by slug

---

## Controller

```java
@Controller
public class CheckoutController {
    
    private final CreateCheckoutUseCase createCheckoutUseCase;
    private final GetCheckoutUseCase getCheckoutUseCase;
    private final PreparePaymentUseCase preparePaymentUseCase;
    
    @PostMapping({"/checkout", "/{lang}/checkout"})
    public String createCheckout(@Valid BookingForm form,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            return "redirect:/book-consultation/" + form.getTherapistId();
        }
        
        var checkout = createCheckoutUseCase.execute(CreateCheckoutCommand.from(form));
        return prepareCheckoutPage(checkout, model);
    }
    
    @GetMapping({"/checkout/{slug}", "/{lang}/checkout/{slug}"})
    public String resumeCheckout(@PathVariable String slug, Model model) {
        var checkout = getCheckoutUseCase.execute(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Checkout not found"));
        return prepareCheckoutPage(checkout, model);
    }
    
    private String prepareCheckoutPage(CheckoutDto checkout, Model model) {
        var paymentData = preparePaymentUseCase.execute(checkout);
        model.addAttribute("checkout", checkout);
        model.addAttribute("payment", paymentData);
        return "landing/checkout";
    }
}
```

---

## Page Content

### Order Summary
- Therapist name and photo
- Selected time slot
- Session type (Individual/Couple)
- Price breakdown
- Discount (if promocode applied)
- Total amount

### Payment Form
WayForPay form (auto-submitted or click to pay):

```html
<form id="payment-form" 
      method="POST" 
      th:action="${payment.actionUrl}" 
      accept-charset="UTF-8">
    <input type="hidden" name="merchantAccount" th:value="${payment.merchantAccount}">
    <input type="hidden" name="merchantDomainName" th:value="${payment.merchantDomain}">
    <input type="hidden" name="orderReference" th:value="${payment.orderReference}">
    <input type="hidden" name="orderDate" th:value="${payment.orderDate}">
    <input type="hidden" name="amount" th:value="${payment.amount}">
    <input type="hidden" name="currency" th:value="${payment.currency}">
    <input type="hidden" name="productName[]" th:value="${payment.productName}">
    <input type="hidden" name="productPrice[]" th:value="${payment.productPrice}">
    <input type="hidden" name="productCount[]" value="1">
    <input type="hidden" name="clientFirstName" th:value="${checkout.clientName}">
    <input type="hidden" name="clientEmail" th:value="${checkout.email}">
    <input type="hidden" name="clientPhone" th:value="${checkout.phone}">
    <input type="hidden" name="returnUrl" th:value="${payment.returnUrl}">
    <input type="hidden" name="serviceUrl" th:value="${payment.serviceUrl}">
    <input type="hidden" name="merchantSignature" th:value="${payment.signature}">
    
    <button type="submit" class="btn-primary btn-large">
        <span th:text="#{landing.checkout.pay}">Pay Now</span>
    </button>
</form>
```

---

## Payment DTO

```java
public record PaymentFormDataDto(
    String actionUrl,
    String merchantAccount,
    String merchantDomain,
    String orderReference,
    long orderDate,
    String amount,
    String currency,
    String productName,
    String productPrice,
    String returnUrl,
    String serviceUrl,
    String signature
) {}
```

---

## Checkout Entity

```java
@Entity
@Table(name = "checkout")
public class Checkout {
    @Id @GeneratedValue
    private Long id;
    
    @Column(unique = true)
    private String slug;  // Random 32-char string
    
    private Long therapistId;
    private Long priceId;
    private Long slotId;
    private Long userId;  // null for new users
    
    private String email;
    private String phone;
    private String name;
    private String authType;  // "new" or "existing"
    
    private Long userPromocodeId;  // if applied
    
    private LocalDateTime createdAt;
}
```

---

## PHP Reference
- `original_php_project/src/Landing/Controller/CheckoutController.php`
- `original_php_project/src/Landing/Service/CheckoutCreator.php`
- `original_php_project/src/Billing/Service/WayForPaySignatureMaker.php`

---

## Verification
- [ ] Checkout created from booking form
- [ ] Order summary displayed correctly
- [ ] Payment form has correct fields
- [ ] Signature generated correctly
- [ ] Form submits to WayForPay
- [ ] Checkout resumable via slug

---

## Next
Proceed to **3.7: Thank You Page**

