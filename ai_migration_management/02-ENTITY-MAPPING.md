# Entity Mapping: PHP Doctrine → Java JPA

This document provides a complete mapping of all entities from the original PHP/Doctrine project to the target Java/JPA implementation.

---

## Psiholog Module Entities

### Psiholog

**PHP Entity:** `App\Psiholog\Entity\Psiholog`  
**Java Entity:** `com.goodhelp.psiholog.entity.Psiholog`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | Primary key, auto-generated |
| email | string(255) | email | String | Unique |
| role | integer | role | PsihologRole (enum) | PSIHOLOG=1, TEST_PSIHOLOG=2 |
| state | integer | state | PsihologState (enum) | ACTIVE=1 |

**Constants to Enums:**
```java
public enum PsihologRole {
    PSIHOLOG(1),
    TEST_PSIHOLOG(2);
}

public enum PsihologState {
    ACTIVE(1);
}
```

---

### PsihologProfile

**PHP Entity:** `App\Psiholog\Entity\PsihologProfile`  
**Java Entity:** `com.goodhelp.psiholog.entity.PsihologProfile`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| firstName | string | firstName | String | |
| lastName | string | lastName | String | |
| birthDate | string | birthDate | LocalDate | Convert from string |
| worksFrom | string | worksFrom | LocalDate | Convert from string |
| profileTemplate | string | profileTemplate | String | Template identifier |
| sex | integer | sex | Sex (enum) | WOMAN=1, MAN=2 |
| psihologId | integer | psiholog | Psiholog | @OneToOne relationship |

---

### PsihologSchedule

**PHP Entity:** `App\Psiholog\Entity\PsihologSchedule`  
**Java Entity:** `com.goodhelp.psiholog.entity.PsihologSchedule`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| state | integer | state | ScheduleState (enum) | See states below |
| psihologId | integer | psiholog | Psiholog | @ManyToOne |
| availableAt | datetime | availableAt | LocalDateTime | UTC storage |

**States Enum:**
```java
public enum ScheduleState {
    AVAILABLE(1),
    BOOKED(2),
    UNAVAILABLE(3),
    DONE(4),
    FAILED(5),
    EXPIRED(6);
}
```

---

### PsihologPrice

**PHP Entity:** `App\Psiholog\Entity\PsihologPrice`  
**Java Entity:** `com.goodhelp.psiholog.entity.PsihologPrice`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| price | integer | price | Integer | In minor currency unit |
| slug | string | slug | String | Nullable |
| currency | string | currency | String | e.g., "UAH" |
| type | integer | type | PriceType (enum) | INDIVIDUAL=1, COUPLE=2 |
| state | integer | state | PriceState (enum) | CURRENT=1, PAST=2, UNLISTED=3 |
| payRatePercent | integer | payRatePercent | Integer | |
| psihologId | integer | psiholog | Psiholog | @ManyToOne |

---

### PsihologSettings

**PHP Entity:** `App\Psiholog\Entity\PsihologSettings`  
**Java Entity:** `com.goodhelp.psiholog.entity.PsihologSettings`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| timezone | string | timezone | String | e.g., "Europe/Kiev" |
| telegramChatId | string | telegramChatId | String | Nullable |
| scheduleTimeCap | string | scheduleTimeCap | String | Default "+3 hour" |
| psiholog | Psiholog | psiholog | Psiholog | @OneToOne |

---

### PsihologAutologinToken

**PHP Entity:** `App\Psiholog\Entity\PsihologAutologinToken`  
**Java Entity:** `com.goodhelp.psiholog.entity.PsihologAutologinToken`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| token | string(32) | token | String | Unique |
| psiholog | Psiholog | psiholog | Psiholog | @OneToOne |

---

### PsihologUserNotes

**PHP Entity:** `App\Psiholog\Entity\PsihologUserNotes`  
**Java Entity:** `com.goodhelp.psiholog.entity.PsihologUserNotes`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| psiholog | Psiholog | psiholog | Psiholog | @ManyToOne |
| user | User | user | User | @ManyToOne |
| notes | text | notes | String | @Lob |

---

## User Module Entities

### User

**PHP Entity:** `App\User\Entity\User`  
**Java Entity:** `com.goodhelp.user.entity.User`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| email | string(500) | email | String | |
| fullName | string(500) | fullName | String | Default "" |
| isFullNameSetByUser | boolean | fullNameSetByUser | boolean | |
| timezone | string | timezone | String | |
| locale | string | locale | String | Default "ua" |
| isEmailReal | boolean | emailReal | boolean | Default true |

---

### UserAutologinToken

**PHP Entity:** `App\User\Entity\UserAutologinToken`  
**Java Entity:** `com.goodhelp.user.entity.UserAutologinToken`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| token | string(32) | token | String | |
| user | User | user | User | @OneToOne |

---

### UserConsultation

**PHP Entity:** `App\User\Entity\UserConsultation`  
**Java Entity:** `com.goodhelp.user.entity.UserConsultation`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| userId | integer | user | User | @ManyToOne |
| psihologId | integer | psiholog | Psiholog | @ManyToOne |
| psihologPriceId | integer | psihologPrice | PsihologPrice | @ManyToOne |
| state | integer | state | ConsultationState (enum) | |
| type | integer | type | ConsultationType (enum) | |

**States Enum:**
```java
public enum ConsultationState {
    CREATED(1),
    COMPLETED(2),
    CANCELLED_BY_USER_IN_TIME(5),
    CANCELLED_BY_USER_NOT_IN_TIME(6),
    CANCELLED_BY_PSIHOLOG_IN_TIME(7),
    CANCELLED_BY_PSIHOLOG_NOT_IN_TIME(8);
}
```

---

### ChatMessage

**PHP Entity:** `App\User\Entity\ChatMessage`  
**Java Entity:** `com.goodhelp.user.entity.ChatMessage`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| type | integer | type | MessageType (enum) | SENT_BY_USER=1, SENT_BY_THERAPIST=2 |
| state | integer | state | MessageState (enum) | UNREAD=1, READ=2 |
| body | text | body | String | @Lob |
| sentAt | datetime | sentAt | LocalDateTime | |
| user | User | user | User | @ManyToOne |
| psiholog | Psiholog | psiholog | Psiholog | @ManyToOne |

---

### Promocode

**PHP Entity:** `App\User\Entity\Promocode`  
**Java Entity:** `com.goodhelp.user.entity.Promocode`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| name | string(500) | name | String | |
| state | integer | state | PromocodeState (enum) | ACTIVE=1, INACTIVE=2 |
| discountPercent | integer | discountPercent | Integer | |
| maxUseNumber | integer | maxUseNumber | Integer | Nullable, default 1 |
| expireAt | datetime | expireAt | LocalDateTime | Nullable |

---

### UserPromocode

**PHP Entity:** `App\User\Entity\UserPromocode`  
**Java Entity:** `com.goodhelp.user.entity.UserPromocode`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| promocodeId | integer | promocode | Promocode | @ManyToOne |
| userId | integer | userId | Long | Nullable |
| email | string(500) | email | String | |
| state | integer | state | UserPromocodeState (enum) | APPLIED=1, USED=2 |
| appliedAt | datetime | appliedAt | LocalDateTime | |
| usedAt | datetime | usedAt | LocalDateTime | Nullable |

---

## Billing Module Entities

### Order

**PHP Entity:** `App\Billing\Entity\Order`  
**Java Entity:** `com.goodhelp.billing.entity.Order`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| userId | integer | user | User | @ManyToOne |
| checkoutId | integer | checkoutId | Long | Nullable |
| state | integer | state | OrderState (enum) | |
| gaClientId | string(50) | gaClientId | String | |
| requestCookies | string | requestCookies | String | |
| checkoutSlug | string(32) | checkoutSlug | String | |
| price | integer | price | Integer | In cents |
| currency | string | currency | String | |
| psihologPriceId | integer | psihologPriceId | Long | |
| phone | string | phone | String | |
| email | string | email | String | |
| timezone | string | timezone | String | |
| locale | string | locale | String | Default "ua" |
| clientName | string | clientName | String | |
| cardPan | string | cardPan | String | |
| cardType | string | cardType | String | |
| reason | string | reason | String | |
| reasonCode | string | reasonCode | String | |
| fee | string | fee | String | |
| issuerBankCountry | string | issuerBankCountry | String | |
| issuerBankName | string | issuerBankName | String | |
| paymentSystem | string | paymentSystem | String | |
| pendingStateAt | datetime | pendingStateAt | LocalDateTime | |
| approvedStateAt | datetime | approvedStateAt | LocalDateTime | |
| failedStateAt | datetime | failedStateAt | LocalDateTime | |
| dateCreated | datetime | dateCreated | LocalDateTime | |
| userConsultation | UserConsultation | userConsultation | UserConsultation | @OneToOne |

**States:**
```java
public enum OrderState {
    CREATED(1),
    PENDING(2),
    APPROVED(3),
    FAILED(4);
}
```

---

### Checkout

**PHP Entity:** `App\Billing\Entity\Checkout`  
**Java Entity:** `com.goodhelp.billing.entity.Checkout`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| psihologPriceId | integer | psihologPriceId | Long | |
| psihologScheduleId | integer | psihologScheduleId | Long | |
| userPromocodeId | integer | userPromocode | UserPromocode | @ManyToOne |
| slug | string(32) | slug | String | Unique |
| userId | integer | userId | Long | Nullable |
| authType | string | authType | String | "new" or "existing" |
| phone | string | phone | String | |
| email | string | email | String | |
| name | string | name | String | |
| gaClientId | string(50) | gaClientId | String | |
| gaClientIdOriginal | string(100) | gaClientIdOriginal | String | |

---

### OrderLog

**PHP Entity:** `App\Billing\Entity\OrderLog`  
**Java Entity:** `com.goodhelp.billing.entity.OrderLog`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| content | json | content | String | JSON stored as text |
| createdAt | datetime | createdAt | LocalDateTime | |

---

### UserWallet

**PHP Entity:** `App\Billing\Entity\UserWallet`  
**Java Entity:** `com.goodhelp.billing.entity.UserWallet`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| user | User | user | User | @OneToOne |
| balance | integer | balance | Integer | In cents |
| currency | string | currency | String | |

---

### UserWalletOperation

**PHP Entity:** `App\Billing\Entity\UserWalletOperation`  
**Java Entity:** `com.goodhelp.billing.entity.UserWalletOperation`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| userWallet | UserWallet | userWallet | UserWallet | @ManyToOne |
| amount | integer | amount | Integer | |
| currency | string | currency | String | |
| type | integer | type | WalletOperationType (enum) | ADD, SUBTRACT |
| reasonType | integer | reasonType | WalletOperationReason (enum) | PURCHASE, etc. |
| reasonId | integer | reasonId | Long | |
| createdAt | datetime | createdAt | LocalDateTime | |

---

## Staff Module Entities

### StaffUser

**PHP Entity:** `App\Staff\Entity\User`  
**Java Entity:** `com.goodhelp.staff.entity.StaffUser`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| email | string(255) | email | String | |
| role | integer | role | StaffRole (enum) | USER=1, SUPERUSER=100 |
| state | integer | state | StaffState (enum) | ACTIVE=1 |

---

### StaffUserAutologinToken

**PHP Entity:** `App\Staff\Entity\UserAutologinToken`  
**Java Entity:** `com.goodhelp.staff.entity.StaffUserAutologinToken`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| token | string(32) | token | String | |
| user | User | staffUser | StaffUser | @OneToOne |

---

## Landing Module Entities

### BlogPost

**PHP Entity:** `App\Landing\Entity\PostUa`  
**Java Entity:** `com.goodhelp.landing.entity.BlogPost`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| psihologId | integer | psiholog | Psiholog | @ManyToOne |
| header | string(1000) | header | String | |
| preview | text | preview | String | @Lob |
| body | text | body | String | @Lob |
| mainImageId | integer | mainImageId | Long | Nullable |
| postedAt | datetime | postedAt | LocalDateTime | |
| state | integer | state | BlogPostState (enum) | DRAFT=0, PUBLISHED=1 |
| locale | string | locale | String | For language versioning |

---

## Common Module Entities

### Image

**PHP Entity:** `App\Common\Entity\Image`  
**Java Entity:** `com.goodhelp.common.entity.Image`

| PHP Field | PHP Type | Java Field | Java Type | Notes |
|-----------|----------|------------|-----------|-------|
| id | integer | id | Long | |
| filename | string | filename | String | |
| path | string | path | String | |
| mimeType | string | mimeType | String | |
| size | integer | size | Long | |
| createdAt | datetime | createdAt | LocalDateTime | |

---

## Base Entity Pattern

All entities should extend a common base for audit fields:

```java
@MappedSuperclass
public abstract class BaseEntity {
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

---

## Enum Value Mapping Pattern

Use a common pattern for all enums with database values:

```java
public enum PsihologRole {
    PSIHOLOG(1),
    TEST_PSIHOLOG(2);
    
    private final int value;
    
    PsihologRole(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static PsihologRole fromValue(int value) {
        return Arrays.stream(values())
            .filter(e -> e.value == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + value));
    }
}
```

Use `@Convert` with `AttributeConverter` for JPA mapping:

```java
@Converter(autoApply = true)
public class PsihologRoleConverter implements AttributeConverter<PsihologRole, Integer> {
    
    @Override
    public Integer convertToDatabaseColumn(PsihologRole role) {
        return role == null ? null : role.getValue();
    }
    
    @Override
    public PsihologRole convertToEntityAttribute(Integer value) {
        return value == null ? null : PsihologRole.fromValue(value);
    }
}
```

