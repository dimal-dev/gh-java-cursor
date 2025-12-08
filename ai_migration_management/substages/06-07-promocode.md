# Sub-Stage 6.7: Promocode Handling

## Goal
Implement promocode application and validation.

---

## Promocode Service

```java
@Service
public class PromocodeService {
    
    private final PromocodeRepository promocodeRepository;
    private final UserPromocodeRepository userPromocodeRepository;
    
    public PromocodeResult applyPromocode(String code, String userEmail, Long priceId) {
        // 1. Find promocode
        Promocode promocode = promocodeRepository.findByNameAndState(code, PromocodeState.ACTIVE)
            .orElse(null);
        
        if (promocode == null) {
            return PromocodeResult.invalid("Promocode not found");
        }
        
        // 2. Check expiration
        if (promocode.getExpireAt() != null && 
            promocode.getExpireAt().isBefore(LocalDateTime.now())) {
            return PromocodeResult.invalid("Promocode expired");
        }
        
        // 3. Check max usage
        if (promocode.getMaxUseNumber() != null) {
            long usedCount = userPromocodeRepository.countByPromocodeIdAndState(
                promocode.getId(), UserPromocodeState.USED);
            if (usedCount >= promocode.getMaxUseNumber()) {
                return PromocodeResult.invalid("Promocode usage limit reached");
            }
        }
        
        // 4. Check if user already applied this promocode
        Optional<UserPromocode> existing = userPromocodeRepository
            .findByEmailAndPromocodeId(userEmail, promocode.getId());
        if (existing.isPresent()) {
            if (existing.get().getState() == UserPromocodeState.USED) {
                return PromocodeResult.invalid("You have already used this promocode");
            }
            // Return existing applied promocode
            return PromocodeResult.valid(promocode.getDiscountPercent(), existing.get().getId());
        }
        
        // 5. Create user promocode record
        UserPromocode userPromocode = new UserPromocode();
        userPromocode.setPromocode(promocode);
        userPromocode.setEmail(userEmail);
        userPromocode.setState(UserPromocodeState.APPLIED);
        userPromocode.setAppliedAt(LocalDateTime.now());
        userPromocodeRepository.save(userPromocode);
        
        return PromocodeResult.valid(promocode.getDiscountPercent(), userPromocode.getId());
    }
    
    public void markUsed(Long userPromocodeId) {
        UserPromocode userPromocode = userPromocodeRepository.findById(userPromocodeId)
            .orElseThrow();
        userPromocode.setState(UserPromocodeState.USED);
        userPromocode.setUsedAt(LocalDateTime.now());
        userPromocodeRepository.save(userPromocode);
    }
    
    public int calculateDiscountedPrice(int originalPrice, int discountPercent) {
        return originalPrice - (originalPrice * discountPercent / 100);
    }
}
```

---

## Result DTO

```java
public record PromocodeResult(
    boolean valid,
    String message,
    Integer discountPercent,
    Long userPromocodeId,
    Integer newPrice
) {
    public static PromocodeResult invalid(String message) {
        return new PromocodeResult(false, message, null, null, null);
    }
    
    public static PromocodeResult valid(int discountPercent, Long userPromocodeId) {
        return new PromocodeResult(true, "Promocode applied", discountPercent, userPromocodeId, null);
    }
    
    public PromocodeResult withPrice(int newPrice) {
        return new PromocodeResult(valid, message, discountPercent, userPromocodeId, newPrice);
    }
}
```

---

## API Endpoint

```java
@RestController
@RequestMapping("/api/book-consultation")
public class BookConsultationApiController {
    
    @PostMapping("/apply-promocode")
    public PromocodeResult applyPromocode(@RequestBody ApplyPromocodeRequest request) {
        var result = promocodeService.applyPromocode(
            request.code(), 
            request.email(), 
            request.priceId()
        );
        
        if (result.valid()) {
            // Calculate new price
            int originalPrice = priceService.getPrice(request.priceId());
            int newPrice = promocodeService.calculateDiscountedPrice(
                originalPrice, result.discountPercent());
            return result.withPrice(newPrice);
        }
        
        return result;
    }
}
```

---

## PHP Reference
- `original_php_project/src/Landing/Controller/BookConsultationApplyPromocodeController.php`
- `original_php_project/src/User/Entity/Promocode.php`

---

## Verification
- [ ] Valid promocodes apply
- [ ] Invalid codes rejected
- [ ] Expired codes rejected
- [ ] Usage limits enforced
- [ ] Discount calculated correctly
- [ ] Mark as used on payment

---

## Stage 6 Complete
Verify all billing features work, then proceed to **Stage 7: Notification Module**.

