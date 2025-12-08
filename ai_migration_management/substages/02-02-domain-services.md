# Sub-Stage 2.2: Domain Services & Repository Interfaces

## Goal
Create domain services for cross-aggregate logic and define repository interfaces in the domain layer.

---

## Repository Interfaces

**Location:** `com.goodhelp.identity.domain.repository/` and `com.goodhelp.booking.domain.repository/`

### Identity Context
```java
public interface TherapistRepository {
    Optional<Therapist> findById(Long id);
    Optional<Therapist> findByEmail(Email email);
    List<Therapist> findAllActive();
    Therapist save(Therapist therapist);
}

public interface AuthTokenRepository {
    Optional<AuthToken> findByToken(String token);
    void save(AuthToken token);
    void deleteByTherapistId(Long therapistId);
}
```

### Booking Context
```java
public interface ScheduleSlotRepository {
    Optional<ScheduleSlot> findById(Long id);
    List<ScheduleSlot> findByTherapistAndDateRange(Long therapistId, LocalDateTime from, LocalDateTime to);
    List<ScheduleSlot> findAvailableByTherapist(Long therapistId, LocalDateTime after);
    ScheduleSlot save(ScheduleSlot slot);
    void saveAll(List<ScheduleSlot> slots);
}

public interface PriceOptionRepository {
    List<PriceOption> findActiveByTherapist(Long therapistId);
    Optional<PriceOption> findBySlug(String slug);
}
```

---

## Domain Services

### ScheduleDomainService
**Location:** `com.goodhelp.booking.domain.service/`

```java
@Service
public class ScheduleDomainService {
    
    // Generate time slots for a week based on therapist's schedule template
    public List<ScheduleSlot> generateWeeklySlots(
        Therapist therapist, 
        WeeklyScheduleTemplate template,
        LocalDate weekStart
    );
    
    // Check if slot can be booked
    public boolean isSlotAvailable(ScheduleSlot slot, LocalDateTime now);
    
    // Calculate available slots considering time cap
    public List<ScheduleSlot> getBookableSlots(
        Long therapistId, 
        Duration timeCap,
        LocalDateTime now
    );
}
```

### TherapistNoteDomainService
**Location:** `com.goodhelp.identity.domain.service/`

```java
// Handles cross-aggregate concern: therapist notes about users
public record UserNote(Long therapistId, Long userId, String content, LocalDateTime updatedAt) {}
```

---

## PHP Service → Java Domain Service Mapping

| PHP Service | Java Service | Notes |
|-------------|--------------|-------|
| `TimeHelper.php` | `ScheduleDomainService` | Schedule calculations |
| `PsihologAutologinTokenCreator.php` | Application layer | Token creation |
| `ChatMessagesRetriever.php` | Messaging context | Move to messaging |

---

## Verification
- [ ] Repository interfaces defined in domain layer
- [ ] Domain services contain only business logic
- [ ] No infrastructure dependencies in domain layer
- [ ] Methods use domain types (not primitives where possible)

---

## Next
Proceed to **2.3: Application Layer (Use Cases, Commands)**

