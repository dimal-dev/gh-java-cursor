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
- [x] Repository interfaces defined in domain layer
- [x] Domain services contain only business logic
- [x] No infrastructure dependencies in domain layer
- [x] Methods use domain types (not primitives where possible)

---

## Status: ✅ COMPLETED (2024-12-08)

### Files Created:

**Therapist Repository Interfaces (`com.goodhelp.therapist.domain.repository`):**
- `TherapistRepository.java` - Therapist aggregate persistence
- `TherapistAutologinTokenRepository.java` - Auth token management
- `TherapistSettingsRepository.java` - Settings persistence
- `TherapistUserNotesRepository.java` - Client notes management

**Booking Repository Interfaces (`com.goodhelp.booking.domain.repository`):**
- `ScheduleSlotRepository.java` - Schedule slot management
- `TherapistPriceRepository.java` - Pricing options

**Domain Services (`com.goodhelp.booking.domain.service`):**
- `ScheduleDomainService.java` - Schedule calculations, slot generation, availability filtering

---

## Next
Proceed to **2.3: Application Layer (Use Cases, Commands)**

