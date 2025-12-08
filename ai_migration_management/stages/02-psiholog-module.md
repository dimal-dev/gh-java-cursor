# Stage 2: Therapist Module

## Overview

This stage implements the Therapist Cabinet using **Domain-Driven Design principles**. This is the first fully functional bounded context and serves as the reference implementation for subsequent modules.

> **Remember**: We are building a **Java application**, not translating PHP. The PHP codebase defines the **features to deliver**, not the **implementation approach**.

**Estimated Sub-stages:** 12  
**Dependencies:** Stage 1 (Project Foundation)  
**Next Stage:** Stage 3 - Landing Module

---

## DDD Context: Identity + Booking

This module spans two bounded contexts:
- **Identity Context** - Therapist authentication, profiles
- **Booking Context** - Schedule management, consultations

---

## PHP Source Reference (REQUIREMENTS ONLY)

Use PHP code to understand **what features** to implement, not **how** to implement:

| Type | Path | Use For |
|------|------|---------|
| Controllers | `src/Psiholog/Controller/` | Feature requirements |
| Entities | `src/Psiholog/Entity/` | Data field requirements |
| Templates | `src/Psiholog/Resources/templates/` | UI requirements |

---

## Sub-Stage Files (DDD-Organized)

| Sub-Stage | File | Focus |
|-----------|------|-------|
| 2.1 | `substages/02-01-domain-model.md` | Domain entities, value objects, aggregates |
| 2.2 | `substages/02-02-domain-services.md` | Domain services, repository interfaces |
| 2.3 | `substages/02-03-application-layer.md` | Use cases, commands, DTOs |
| 2.4 | `substages/02-04-infrastructure.md` | JPA repositories, persistence |
| 2.5 | `substages/02-05-security.md` | Authentication, authorization |
| 2.6 | `substages/02-06-auth-controllers.md` | Login/logout presentation |
| 2.7 | `substages/02-07-schedule-feature.md` | Schedule management (full stack) |
| 2.8 | `substages/02-08-clients-feature.md` | Client management (full stack) |
| 2.9 | `substages/02-09-chat-feature.md` | Chat functionality (full stack) |
| 2.10 | `substages/02-10-settings-payments.md` | Settings & payments |
| 2.11 | `substages/02-11-templates.md` | Thymeleaf templates |
| 2.12 | `substages/02-12-styles.md` | CSS/SCSS styling |

---

## PHP Controllers to Migrate

| PHP Controller | Java Controller | Route |
|----------------|-----------------|-------|
| `IndexController` | `TherapistDashboardController` | `/therapist/` |
| `LoginController` | `TherapistLoginController` | `/therapist/login` |
| `AutoLoginController` | `TherapistLoginController` | `/therapist/auto-login` |
| `LogoutController` | (Spring Security) | `/therapist/logout` |
| `ScheduleController` | `TherapistScheduleController` | `/therapist/schedule` |
| `ScheduleListAjaxController` | `TherapistScheduleController` | `/therapist/schedule/list` |
| `ScheduleSettingsController` | `TherapistScheduleController` | `/therapist/schedule/settings` |
| `ScheduleSettingsWeekController` | `TherapistScheduleController` | `/therapist/schedule/settings/week` |
| `ScheduleSettingsBookTimeAjaxController` | `TherapistScheduleController` | `/therapist/schedule/book` |
| `SettingsController` | `TherapistSettingsController` | `/therapist/settings` |
| `ClientsController` | `TherapistClientsController` | `/therapist/clients` |
| `ClientsListAjaxController` | `TherapistClientsController` | `/therapist/clients/list` |
| `UserNotesController` | `TherapistClientsController` | `/therapist/clients/notes` |
| `ChatController` | `TherapistChatController` | `/therapist/chat` |
| `GetMessagesAjaxController` | `TherapistChatController` | `/therapist/chat/messages` |
| `SendMessageController` | `TherapistChatController` | `/therapist/chat/send` |
| `NewMessagesController` | `TherapistChatController` | `/therapist/chat/new` |
| `GetUnreadMessagesAmountAjaxController` | `TherapistChatController` | `/therapist/chat/unread` |
| `PaymentsController` | `TherapistPaymentsController` | `/therapist/payments` |
| `PaymentsHistoryController` | `TherapistPaymentsController` | `/therapist/payments/history` |
| `PaymentsHistoryItemsAjaxController` | `TherapistPaymentsController` | `/therapist/payments/history/items` |
| `PublicProfileController` | (In Landing) | `/therapist/profile` |
| `CancelConsultationController` | `TherapistScheduleController` | `/therapist/consultation/cancel` |

---

## Sub-Stage 2.1: Domain Model

**Goal:** Create the domain model following DDD principles with rich behavior.

### Aggregate: Therapist (Identity Context)

```java
// Aggregate Root
com.goodhelp.identity.domain.model.Therapist
├── TherapistProfile (Value Object - embedded)
├── TherapistStatus (Enum)
└── AuthToken (Value Object)

// Value Objects
com.goodhelp.shared.domain.valueobject.Email
com.goodhelp.shared.domain.valueobject.PersonName
```

### Aggregate: Schedule (Booking Context)

```java
// Aggregate Root - owns schedule slots
com.goodhelp.booking.domain.model.Schedule
├── ScheduleSlot (Entity within aggregate)
└── TimeSlot (Value Object)
└── SlotStatus (Enum)

// Supporting Value Objects
com.goodhelp.shared.domain.valueobject.Money
com.goodhelp.booking.domain.model.PriceOption
```

### Key Design Decisions

1. **Rich Behavior in Aggregates** - Business logic in domain, not services
2. **Value Objects for Domain Concepts** - Money, Email, TimeSlot are immutable
3. **Aggregates Control Invariants** - Schedule ensures no double-booking
4. **Java Records for Value Objects** - Immutable by default

**Reference:** See `03-DDD-DESIGN-PRINCIPLES.md` for patterns.

---

## Sub-Stage 2.2: Repositories

**Goal:** Create Spring Data JPA repositories with custom queries.

**Repositories to Create:**
1. `TherapistRepository.java`
2. `TherapistProfileRepository.java`
3. `TherapistScheduleRepository.java`
4. `TherapistPriceRepository.java`
5. `TherapistSettingsRepository.java`
6. `TherapistAutologinTokenRepository.java`
7. `TherapistUserNotesRepository.java`

**Key Custom Queries (from PHP):**
```java
// TherapistRepository
Optional<Therapist> findByEmail(String email);
List<Therapist> findAllByStateAndRoleIn(TherapistState state, List<TherapistRole> roles);

// TherapistScheduleRepository
List<TherapistSchedule> findByTherapistIdAndAvailableAtAfterAndState(
    Long therapistId, LocalDateTime fromTime, ScheduleState state);
    
// TherapistPriceRepository
Optional<TherapistPrice> findByTherapistAndCurrencyAndTypeAndState(
    Therapist therapist, String currency, PriceType type, PriceState state);
Optional<TherapistPrice> findBySlug(String slug);
```

---

## Sub-Stage 2.3: Security

**Goal:** Implement therapist-specific authentication.

**Classes to Create:**
1. `TherapistUserDetails.java` - UserDetails implementation
2. `TherapistUserDetailsService.java` - UserDetailsService
3. `TherapistAutoLoginAuthenticator.java` - Custom authentication filter

**Authentication Flow:**
1. User clicks auto-login link with token
2. `TherapistAutoLoginAuthenticator` intercepts request
3. Token is validated against `TherapistAutologinToken`
4. `TherapistUserDetails` is created and authenticated
5. Session is established

**Roles:**
- `ROLE_PSIHOLOG`
- `ROLE_TEST_PSIHOLOG`

---

## Sub-Stage 2.4: Login/Logout Controllers

**Goal:** Implement authentication endpoints.

**Endpoints:**
- `GET /therapist/login` - Show login page
- `POST /therapist/login` - Process email, send autologin link
- `GET /therapist/auto-login?t={token}` - Auto-login with token
- `POST /therapist/logout` - Logout (Spring Security handles)

**PHP Reference:** `LoginController.php`, `AutoLoginController.php`

---

## Sub-Stage 2.5: Dashboard Controller

**Goal:** Implement the main dashboard page.

**Endpoint:** `GET /therapist/` or `GET /therapist/dashboard`

**Currently:** Redirects to schedule page (per PHP implementation)

**PHP Reference:** `IndexController.php`

---

## Sub-Stage 2.6: Schedule Management

**Goal:** Implement schedule viewing and management.

**Endpoints:**
- `GET /therapist/schedule` - View schedule
- `GET /therapist/schedule/list` - AJAX: Get schedule items
- `GET /therapist/schedule/settings` - Schedule settings
- `GET /therapist/schedule/settings/week` - Weekly settings
- `POST /therapist/schedule/book` - AJAX: Book/unbook time slot
- `POST /therapist/consultation/{id}/cancel` - Cancel consultation

**Features:**
- View upcoming consultations
- Set availability for time slots
- Weekly recurring schedule
- Cancel/reschedule consultations
- Time zone handling

**PHP Reference:** `ScheduleController.php`, `ScheduleSettingsController.php`, etc.

---

## Sub-Stage 2.7: Settings Controller

**Goal:** Implement therapist settings page.

**Endpoint:** `GET/POST /therapist/settings`

**Features:**
- View/update timezone
- Telegram integration link
- Display schedule time cap

**PHP Reference:** `SettingsController.php`

---

## Sub-Stage 2.8: Clients & User Notes

**Goal:** Implement client management and notes.

**Endpoints:**
- `GET /therapist/clients` - View clients list
- `GET /therapist/clients/list` - AJAX: Get clients
- `GET /therapist/clients/{userId}/notes` - View notes for client
- `POST /therapist/clients/{userId}/notes` - Save notes

**PHP Reference:** `ClientsController.php`, `UserNotesController.php`

---

## Sub-Stage 2.9: Chat Functionality

**Goal:** Implement chat between therapist and clients.

**Endpoints:**
- `GET /therapist/chat` - Chat list
- `GET /therapist/chat/{userId}` - Chat with specific user
- `GET /therapist/chat/{userId}/messages` - AJAX: Get messages
- `POST /therapist/chat/{userId}/send` - AJAX: Send message
- `GET /therapist/chat/new` - New messages indicator
- `GET /therapist/chat/unread` - AJAX: Unread count

**Features:**
- List all users with conversations
- Real-time message display (polling)
- Send/receive messages
- Mark messages as read
- Unread message indicators

**PHP Reference:** `Chat/` directory controllers

---

## Sub-Stage 2.10: Payments History

**Goal:** Implement payment/earnings history.

**Endpoints:**
- `GET /therapist/payments` - Payments overview
- `GET /therapist/payments/history` - Payment history
- `GET /therapist/payments/history/items` - AJAX: History items

**Features:**
- View completed consultations
- See earnings
- Payment history with pagination

**PHP Reference:** `PaymentsController.php`, `PaymentsHistoryController.php`

---

## Sub-Stage 2.11: Templates

**Goal:** Create all Thymeleaf templates for the therapist cabinet.

**Templates to Create:**
```
templates/therapist/
├── layout/
│   └── main.html
├── login.html
├── dashboard.html (redirect)
├── schedule.html
├── schedule-settings.html
├── schedule-settings-week.html
├── settings.html
├── clients.html
├── user-notes.html
├── chat/
│   ├── index.html
│   └── conversation.html
├── payments/
│   ├── index.html
│   └── history.html
└── fragments/
    └── sidebar.html
```

**PHP Template Reference:** `original_php_project/src/Psiholog/Resources/templates/`

---

## Sub-Stage 2.12: Styles

**Goal:** Migrate and adapt SCSS styles for therapist cabinet.

**Tasks:**
1. Create `therapist.scss` main file
2. Migrate component styles
3. Create sidebar styles
4. Create page-specific styles
5. Compile SCSS to CSS

**PHP Style Reference:** Look at therapist-specific styles in templates.

---

## Verification Checklist

After completing Stage 2, verify:

- [ ] Therapist can log in via email link
- [ ] Schedule page displays correctly
- [ ] Can set availability time slots
- [ ] Can cancel consultations
- [ ] Settings page works with timezone
- [ ] Clients list shows users
- [ ] User notes can be saved/viewed
- [ ] Chat sends/receives messages
- [ ] Payments history displays
- [ ] All pages render correctly
- [ ] i18n works on all pages
- [ ] Responsive design works

---

## Transition to Stage 3

Once all sub-stages are complete and verified:

1. Update `99-PROGRESS-TRACKER.md`
2. Mark Stage 2 as COMPLETED
3. Proceed to Stage 3: Landing Module

