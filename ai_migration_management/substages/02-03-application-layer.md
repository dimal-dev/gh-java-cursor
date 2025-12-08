# Sub-Stage 2.3: Application Layer

## Goal
Create use cases, commands, queries, and DTOs for the therapist module.

---

## Use Cases to Create

**Location:** `com.goodhelp.therapist.application.usecase/`

### Authentication
```java
AuthenticateTherapistUseCase
├── Input: AuthenticateCommand(token: String)
├── Output: AuthenticationResult(therapistId, sessionData)
└── Flow: Validate token → Load therapist → Create session

RequestLoginLinkUseCase
├── Input: RequestLoginCommand(email: String)
├── Output: void (sends email)
└── Flow: Find therapist → Generate token → Send email
```

### Schedule Management
```java
GetScheduleUseCase
├── Input: GetScheduleQuery(therapistId, dateRange)
├── Output: ScheduleDto (list of slots with status)

UpdateScheduleSlotUseCase
├── Input: UpdateSlotCommand(slotId, newStatus)
├── Output: void
└── Flow: Load slot → Validate transition → Update → Publish event

GenerateWeeklySlotsUseCase
├── Input: GenerateSlotsCommand(therapistId, weekStart, template)
├── Output: List<ScheduleSlotDto>
```

### Client Management
```java
GetClientsUseCase
├── Input: GetClientsQuery(therapistId, page, search)
├── Output: PagedResult<ClientDto>

SaveUserNotesUseCase
├── Input: SaveNotesCommand(therapistId, userId, notes)
├── Output: void
```

---

## Commands & Queries (Records)

```java
// Commands (mutate state)
public record RequestLoginCommand(@NotBlank @Email String email) {}
public record UpdateSlotCommand(@NotNull Long slotId, @NotNull SlotStatus newStatus) {}
public record SaveNotesCommand(@NotNull Long userId, String notes) {}
public record CancelConsultationCommand(@NotNull Long consultationId, String reason) {}

// Queries (read-only)
public record GetScheduleQuery(Long therapistId, LocalDate from, LocalDate to) {}
public record GetClientsQuery(Long therapistId, int page, int size, String search) {}
public record GetChatQuery(Long therapistId, Long userId, int page) {}
```

---

## DTOs

**Location:** `com.goodhelp.therapist.application.dto/`

```java
public record ScheduleSlotDto(
    Long id,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String status,
    String clientName,  // null if available
    Long consultationId // null if available
) {}

public record ClientDto(
    Long userId,
    String name,
    String email,
    int consultationCount,
    LocalDateTime lastConsultation
) {}

public record TherapistDashboardDto(
    int upcomingConsultations,
    int todayConsultations,
    int unreadMessages,
    LocalDateTime nextConsultation
) {}
```

---

## PHP Controller → Use Case Mapping

| PHP Controller | Use Case |
|----------------|----------|
| `LoginController` | `RequestLoginLinkUseCase` |
| `AutoLoginController` | `AuthenticateTherapistUseCase` |
| `ScheduleController` | `GetScheduleUseCase` |
| `ScheduleSettingsBookTimeAjaxController` | `UpdateScheduleSlotUseCase` |
| `ClientsController` | `GetClientsUseCase` |
| `UserNotesController` | `SaveUserNotesUseCase` |

---

## Verification
- [ ] Use cases are single-responsibility
- [ ] Commands validated with Bean Validation
- [ ] DTOs are immutable records
- [ ] Use cases coordinate domain logic, don't contain it

---

## Next
Proceed to **2.4: Infrastructure (JPA Repositories)**

