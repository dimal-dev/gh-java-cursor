# Sub-Stage 2.1: Therapist Domain Model

## Goal
Create rich domain model for the Therapist bounded context following DDD principles.

---

## Aggregates to Create

### 1. Therapist Aggregate (Identity Context)
**Location:** `com.goodhelp.identity.domain.model/`

```java
// Aggregate Root
Therapist.java
├── TherapistProfile.java (Embedded Value Object)
├── TherapistSettings.java (Entity)
├── TherapistStatus (Enum: ACTIVE)
└── TherapistRole (Enum: PSIHOLOG, TEST_PSIHOLOG)

// Value Objects  
Email.java (shared kernel)
PersonName.java
AuthToken.java
```

### 2. Schedule Aggregate (Booking Context)
**Location:** `com.goodhelp.booking.domain.model/`

```java
// Aggregate Root
Schedule.java (owns all slots for a therapist)
├── ScheduleSlot.java (Entity within aggregate)
├── TimeSlot.java (Value Object - start time + duration)
└── SlotStatus (Enum: AVAILABLE, BOOKED, UNAVAILABLE, DONE, FAILED, EXPIRED)

// Supporting
PriceOption.java (Value Object)
PriceType (Enum: INDIVIDUAL, COUPLE)
```

---

## Key Files to Create

| File | Package | Type |
|------|---------|------|
| `Therapist.java` | `identity.domain.model` | Aggregate Root |
| `TherapistProfile.java` | `identity.domain.model` | Embedded VO |
| `TherapistSettings.java` | `identity.domain.model` | Entity |
| `TherapistRole.java` | `identity.domain.model` | Enum |
| `TherapistStatus.java` | `identity.domain.model` | Enum |
| `ScheduleSlot.java` | `booking.domain.model` | Entity |
| `SlotStatus.java` | `booking.domain.model` | Enum |
| `PriceOption.java` | `booking.domain.model` | Value Object |
| `Email.java` | `shared.domain.valueobject` | Value Object |
| `Money.java` | `shared.domain.valueobject` | Value Object |
| `TimeSlot.java` | `shared.domain.valueobject` | Value Object |

---

## Implementation Notes

### Therapist Aggregate
- Owns profile, settings, auth tokens
- Business methods: `activate()`, `deactivate()`, `updateProfile()`, `linkTelegram()`
- Invariants: Email must be unique, must have profile when active

### Schedule Slot
- Rich behavior: `book()`, `release()`, `markDone()`, `expire()`
- Invariants: Cannot double-book, cannot book past slots
- Time stored in UTC, displayed in therapist's timezone

### Value Objects as Records
```java
public record Email(String value) {
    public Email { /* validation */ }
}

public record Money(int amountCents, String currency) {
    public Money { /* validation */ }
    public Money applyDiscount(int percent) { /* business logic */ }
}
```

---

## PHP Source Reference
| PHP Entity | Java Entity | Notes |
|------------|-------------|-------|
| `Psiholog.php` | `Therapist.java` | Add behavior, use enums |
| `PsihologProfile.php` | `TherapistProfile.java` | Embed as VO |
| `PsihologSchedule.php` | `ScheduleSlot.java` | Rich state management |
| `PsihologPrice.php` | `PriceOption.java` | Immutable value object |

See `02-ENTITY-MAPPING.md` for complete field mapping.

---

## Verification
- [ ] All entities compile without errors
- [ ] Value objects are immutable (records or final fields)
- [ ] Enums have proper value mapping for JPA
- [ ] Business logic in domain, not in services

---

## Next
Proceed to **2.2: Domain Services & Repository Interfaces**

