# Sub-Stage 6.1: Billing Entities

## Goal
Create domain entities for the billing/payment module.

---

## Entities to Create

**Location:** `com.goodhelp.billing.domain.model/`

### Order (Aggregate Root)
```java
@Entity
@Table(name = "order")
public class Order {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    
    private Long checkoutId;
    
    @Convert(converter = OrderStateConverter.class)
    private OrderState state;
    
    // Payment details
    private int price;           // In cents
    private String currency;
    private String checkoutSlug;
    
    // Contact info from checkout
    private String email;
    private String phone;
    private String clientName;
    
    // WayForPay response fields
    private String cardPan;
    private String cardType;
    private String reason;
    private String reasonCode;
    private String paymentSystem;
    
    // State timestamps
    private LocalDateTime pendingStateAt;
    private LocalDateTime approvedStateAt;
    private LocalDateTime failedStateAt;
    private LocalDateTime createdAt;
    
    @OneToOne
    private UserConsultation userConsultation;
    
    // Domain methods
    public void markPending() {
        this.state = OrderState.PENDING;
        this.pendingStateAt = LocalDateTime.now();
    }
    
    public void approve(String cardPan, String cardType, String paymentSystem) {
        this.state = OrderState.APPROVED;
        this.approvedStateAt = LocalDateTime.now();
        this.cardPan = cardPan;
        this.cardType = cardType;
        this.paymentSystem = paymentSystem;
    }
    
    public void fail(String reason, String reasonCode) {
        this.state = OrderState.FAILED;
        this.failedStateAt = LocalDateTime.now();
        this.reason = reason;
        this.reasonCode = reasonCode;
    }
}
```

### Checkout
```java
@Entity
@Table(name = "checkout")
public class Checkout {
    @Id @GeneratedValue
    private Long id;
    
    @Column(unique = true)
    private String slug;
    
    private Long therapistId;
    private Long priceId;
    private Long slotId;
    private Long userId;
    
    private String email;
    private String phone;
    private String name;
    private String authType;  // "new" or "existing"
    
    private Long userPromocodeId;
    private String gaClientId;
    
    private LocalDateTime createdAt;
}
```

### UserWallet
```java
@Entity
@Table(name = "user_wallet")
public class UserWallet {
    @Id @GeneratedValue
    private Long id;
    
    @OneToOne
    private User user;
    
    private int balance;  // In cents
    private String currency;
    
    public void addFunds(int amount) {
        this.balance += amount;
    }
    
    public void deductFunds(int amount) {
        if (this.balance < amount) {
            throw new InsufficientBalanceException();
        }
        this.balance -= amount;
    }
}
```

### UserWalletOperation
```java
@Entity
@Table(name = "user_wallet_operation")
public class UserWalletOperation {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToOne
    private UserWallet wallet;
    
    private int amount;
    private String currency;
    
    @Enumerated(EnumType.STRING)
    private OperationType type;  // ADD, SUBTRACT
    
    @Enumerated(EnumType.STRING)
    private OperationReason reasonType;  // PURCHASE, CONSULTATION, REFUND
    
    private Long reasonId;
    private LocalDateTime createdAt;
}
```

### OrderLog
```java
@Entity
@Table(name = "order_log")
public class OrderLog {
    @Id @GeneratedValue
    private Long id;
    
    @Column(columnDefinition = "TEXT")
    private String content;  // Raw JSON of webhook request
    
    private LocalDateTime createdAt;
}
```

---

## Enums

```java
public enum OrderState {
    CREATED(1), PENDING(2), APPROVED(3), FAILED(4);
}

public enum OperationType { ADD, SUBTRACT }
public enum OperationReason { PURCHASE, CONSULTATION, REFUND }
```

---

## PHP Reference
- `original_php_project/src/Billing/Entity/`

---

## Verification
- [ ] All entities compile
- [ ] Relationships correct
- [ ] Domain logic in entities

---

## Next
Proceed to **6.2: Repositories**

