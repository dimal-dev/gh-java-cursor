# Sub-Stage 6.3: WayForPay Service

## Goal
Implement WayForPay payment gateway integration with signature generation/verification.

---

## Service

**Location:** `com.goodhelp.billing.infrastructure.payment/`

```java
@Service
public class WayForPayService {
    
    @Value("${wayforpay.merchant-account}")
    private String merchantAccount;
    
    @Value("${wayforpay.merchant-secret}")
    private String merchantSecret;
    
    @Value("${wayforpay.merchant-domain}")
    private String merchantDomain;
    
    private static final String[] REQUEST_SIGNATURE_FIELDS = {
        "merchantAccount", "orderReference", "amount", "currency",
        "authCode", "cardPan", "transactionStatus", "reasonCode"
    };
    
    private static final String[] RESPONSE_SIGNATURE_FIELDS = {
        "orderReference", "status", "time"
    };
    
    /**
     * Generate signature for payment form data
     */
    public String generatePaymentSignature(PaymentRequest request) {
        String data = String.join(";",
            merchantAccount,
            merchantDomain,
            request.orderReference(),
            String.valueOf(request.orderDate()),
            String.valueOf(request.amount()),
            request.currency(),
            request.productName(),
            "1",  // productCount
            String.valueOf(request.productPrice())
        );
        return hmacMd5(data, merchantSecret);
    }
    
    /**
     * Verify incoming webhook signature
     */
    public boolean verifyWebhookSignature(WebhookRequest request, String providedSignature) {
        String data = String.join(";",
            request.merchantAccount(),
            request.orderReference(),
            String.valueOf(request.amount()),
            request.currency(),
            request.authCode(),
            request.cardPan(),
            request.transactionStatus(),
            String.valueOf(request.reasonCode())
        );
        String calculated = hmacMd5(data, merchantSecret);
        return calculated.equalsIgnoreCase(providedSignature);
    }
    
    /**
     * Generate signature for webhook response
     */
    public String generateResponseSignature(String orderReference, String status, long time) {
        String data = String.join(";", orderReference, status, String.valueOf(time));
        return hmacMd5(data, merchantSecret);
    }
    
    private String hmacMd5(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacMD5");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacMD5");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
```

---

## Payment Request DTO

```java
public record PaymentRequest(
    String orderReference,
    long orderDate,
    int amount,
    String currency,
    String productName,
    int productPrice,
    String returnUrl,
    String serviceUrl
) {}

public record PaymentFormData(
    String merchantAccount,
    String merchantDomain,
    String orderReference,
    long orderDate,
    String amount,
    String currency,
    String productName,
    String productPrice,
    String productCount,
    String returnUrl,
    String serviceUrl,
    String merchantSignature
) {}
```

---

## Webhook Request DTO

```java
public record WebhookRequest(
    String merchantAccount,
    String orderReference,
    int amount,
    String currency,
    String authCode,
    String cardPan,
    String transactionStatus,
    int reasonCode,
    String merchantSignature
    // Additional fields as needed
) {}
```

---

## Configuration

```yaml
# application.yml
wayforpay:
  merchant-account: ${WAYFORPAY_MERCHANT_ACCOUNT}
  merchant-secret: ${WAYFORPAY_MERCHANT_SECRET}
  merchant-domain: ${WAYFORPAY_MERCHANT_DOMAIN:goodhelp.com}
  service-url: ${APP_BASE_URL}/billing/webhook
```

---

## PHP Reference
- `original_php_project/src/Billing/Service/WayForPaySignatureMaker.php`

---

## Verification
- [ ] Payment signature generates correctly
- [ ] Webhook signature verifies correctly
- [ ] Response signature generates correctly
- [ ] All crypto operations work

---

## Next
Proceed to **6.4: Webhook Controller**

