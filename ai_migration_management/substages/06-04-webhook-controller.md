# Sub-Stage 6.4: Billing Webhook Controller

## Goal
Implement webhook endpoint to receive and process payment notifications.

---

## Endpoint
`POST /billing/webhook`

---

## Controller

```java
@RestController
@RequestMapping("/billing")
@Slf4j
public class BillingWebhookController {
    
    private final ProcessPaymentWebhookUseCase processWebhookUseCase;
    private final WayForPayService wayForPayService;
    private final OrderLogRepository orderLogRepository;
    
    @PostMapping("/webhook")
    public ResponseEntity<WebhookResponse> handleWebhook(
            @RequestBody String rawBody,
            @RequestBody WebhookRequest request) {
        
        // 1. Log raw request
        orderLogRepository.save(new OrderLog(rawBody, LocalDateTime.now()));
        log.info("Received payment webhook: orderRef={}, status={}", 
            request.orderReference(), request.transactionStatus());
        
        // 2. Verify signature
        if (!wayForPayService.verifyWebhookSignature(request, request.merchantSignature())) {
            log.warn("Invalid webhook signature for order: {}", request.orderReference());
            return ResponseEntity.badRequest().build();
        }
        
        // 3. Process payment
        try {
            processWebhookUseCase.execute(ProcessWebhookCommand.from(request));
        } catch (Exception e) {
            log.error("Error processing webhook for order: {}", request.orderReference(), e);
        }
        
        // 4. Return response (WayForPay expects specific format)
        long time = System.currentTimeMillis() / 1000;
        String signature = wayForPayService.generateResponseSignature(
            request.orderReference(), "accept", time);
        
        return ResponseEntity.ok(new WebhookResponse(
            request.orderReference(),
            "accept",
            time,
            signature
        ));
    }
}
```

---

## Webhook Response

```java
public record WebhookResponse(
    String orderReference,
    String status,
    long time,
    String signature
) {}
```

---

## Security Configuration

Webhook endpoint needs to be accessible without authentication:

```java
// In SecurityConfig
.requestMatchers("/billing/webhook").permitAll()
```

Also disable CSRF for this endpoint if needed.

---

## Transaction Statuses

| Status | Action |
|--------|--------|
| `Approved` | Process approved payment |
| `Pending` | Mark order as pending |
| `Declined` | Mark order as failed |
| `Expired` | Mark order as failed |
| `Refunded` | Process refund |

---

## PHP Reference
- `original_php_project/src/Billing/Controller/Webhook/WayForPayController.php`

---

## Verification
- [ ] Webhook receives POST requests
- [ ] Raw request logged
- [ ] Signature validation works
- [ ] Response format correct
- [ ] Approved payments processed
- [ ] Failed payments handled

---

## Next
Proceed to **6.5: Order Processing**

