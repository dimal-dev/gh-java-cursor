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
- [x] Use cases are single-responsibility
- [x] Commands validated with Bean Validation
- [x] DTOs are immutable records
- [x] Use cases coordinate domain logic, don't contain it

---

## Status: ✅ COMPLETED (2024-12-08)

### Files Created:

**Commands (`com.goodhelp.therapist.application.command`):**
- `RequestLoginCommand.java` - Login link request
- `AuthenticateCommand.java` - Token authentication
- `UpdateSlotCommand.java` - Slot status update
- `ToggleSlotCommand.java` - Slot availability toggle
- `SaveUserNotesCommand.java` - Client notes save
- `CancelConsultationCommand.java` - Consultation cancellation
- `UpdateSettingsCommand.java` - Settings update

**Queries (`com.goodhelp.therapist.application.query`):**
- `GetScheduleQuery.java` - Schedule retrieval
- `GetClientsQuery.java` - Client list
- `GetChatQuery.java` - Chat messages
- `GetPaymentsHistoryQuery.java` - Payment history

**DTOs (`com.goodhelp.therapist.application.dto`):**
- `ScheduleSlotDto.java` - Slot data
- `ClientDto.java` - Client data
- `TherapistDashboardDto.java` - Dashboard summary
- `TherapistDto.java` - Therapist data
- `AuthenticationResultDto.java` - Auth result
- `UserNotesDto.java` - Notes data
- `PaymentHistoryItemDto.java` - Payment item

**Use Cases (`com.goodhelp.therapist.application.usecase`):**
- `AuthenticateTherapistUseCase.java` - Token auth
- `RequestLoginLinkUseCase.java` - Login link
- `GetScheduleUseCase.java` - Schedule retrieval
- `ToggleSlotUseCase.java` - Slot toggle
- `SaveUserNotesUseCase.java` - Save notes
- `GetUserNotesUseCase.java` - Get notes
- `UpdateSettingsUseCase.java` - Update settings

---

## Next
Proceed to **2.4: Infrastructure (JPA Repositories)**

