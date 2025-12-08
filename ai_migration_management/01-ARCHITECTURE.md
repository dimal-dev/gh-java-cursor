# GoodHelp Java Project Architecture

## Overview

This document defines the complete architecture for the GoodHelp Java/Spring Boot application. 

> **Key Principle**: This is a **native Java application built with DDD**, not a PHP translation. The PHP codebase serves as functional requirements only.

The architecture follows:
- **Domain-Driven Design (DDD)** - Rich domain models, aggregates, bounded contexts
- **Clean/Hexagonal Architecture** - Proper layer separation and dependency inversion
- **Modern Java idioms** - Records, sealed classes, Optional, pattern matching
- **Spring Boot best practices** - IoC, AOP, declarative transactions

---

## Technology Stack Details

### Core Technologies

```yaml
Java: 21.0.x (LTS)
  - Virtual threads support
  - Pattern matching for switch
  - Record patterns
  - String templates (preview)

Spring Boot: 3.5.8
  - Auto-configuration
  - Embedded Tomcat
  - Spring Security 6.x
  - Spring Data JPA 3.5.x

Hibernate: 6.6.x
  - Jakarta Persistence 3.2
  - Improved query performance
  - Native SQL support

PostgreSQL: 18.1
  - Replacing MariaDB from original
  - JSON support
  - Full-text search capabilities

Apache Kafka: 4.1.0
  - Replacing RabbitMQ
  - Event-driven architecture
  - Message persistence

Thymeleaf: 3.1.x
  - Natural templates
  - Spring Security dialect
  - Layout dialect
```

### Build & Development

```yaml
Maven: 3.9.11
  - Dependency management
  - Build lifecycle
  - Plugin ecosystem

Docker: Engine 29.x
  - Local development containers
  - PostgreSQL, Redis, Kafka

JUnit: 5.13.4
  - Parameterized tests
  - Nested test classes
  - Extensions
```

---

## Project Folder Structure (DDD-Based)

Each module follows a **layered DDD structure** with clear separation:
- `domain/` - Core business logic (entities, value objects, domain services, repository interfaces)
- `application/` - Use cases, commands, queries, application services
- `infrastructure/` - Persistence, external services, framework integrations
- `presentation/` - Controllers, view models, REST endpoints

```
target_java_project/
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── docker/
│   ├── docker-compose.yml
│   ├── postgres/
│   │   └── init.sql
│   └── kafka/
│       └── kafka-config.yml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── goodhelp/
│   │   │           ├── GoodHelpApplication.java
│   │   │           │
│   │   │           ├── config/
│   │   │           │   ├── SecurityConfig.java
│   │   │           │   ├── WebMvcConfig.java
│   │   │           │   ├── ThymeleafConfig.java
│   │   │           │   ├── KafkaConfig.java
│   │   │           │   └── JpaConfig.java
│   │   │           │
│   │   │           ├── shared/                    # Shared kernel
│   │   │           │   ├── domain/
│   │   │           │   │   ├── AggregateRoot.java
│   │   │           │   │   ├── DomainEvent.java
│   │   │           │   │   ├── ValueObject.java
│   │   │           │   │   └── valueobject/
│   │   │           │   │       ├── Money.java
│   │   │           │   │       ├── Email.java
│   │   │           │   │       └── TimeSlot.java
│   │   │           │   ├── application/
│   │   │           │   │   └── DomainEventPublisher.java
│   │   │           │   └── infrastructure/
│   │   │           │       ├── persistence/
│   │   │           │       │   └── BaseEntity.java
│   │   │           │       └── exception/
│   │   │           │           ├── GlobalExceptionHandler.java
│   │   │           │           └── DomainException.java
│   │   │           │
│   │   │           ├── identity/                  # Identity Context
│   │   │           │   ├── domain/
│   │   │           │   │   ├── model/
│   │   │           │   │   │   ├── Therapist.java       # Aggregate Root
│   │   │           │   │   │   ├── TherapistProfile.java
│   │   │           │   │   │   ├── TherapistStatus.java # Enum
│   │   │           │   │   │   ├── User.java               # Aggregate Root
│   │   │           │   │   │   ├── StaffUser.java          # Aggregate Root
│   │   │           │   │   │   └── AuthToken.java          # Value Object
│   │   │           │   │   └── repository/
│   │   │           │   │       ├── TherapistRepository.java
│   │   │           │   │       └── UserRepository.java
│   │   │           │   ├── application/
│   │   │           │   │   ├── usecase/
│   │   │           │   │   │   ├── AuthenticateTherapistUseCase.java
│   │   │           │   │   │   └── AuthenticateUserUseCase.java
│   │   │           │   │   └── dto/
│   │   │           │   │       └── TherapistDto.java
│   │   │           │   ├── infrastructure/
│   │   │           │   │   ├── persistence/
│   │   │           │   │   │   ├── JpaTherapistRepository.java
│   │   │           │   │   │   └── JpaUserRepository.java
│   │   │           │   │   └── security/
│   │   │           │   │       └── TokenAuthenticationFilter.java
│   │   │           │   └── presentation/
│   │   │           │       └── web/
│   │   │           │           ├── TherapistLoginController.java
│   │   │           │           └── UserLoginController.java
│   │   │           │
│   │   │           ├── booking/                   # Booking Context
│   │   │           │   ├── domain/
│   │   │           │   │   ├── model/
│   │   │           │   │   │   ├── Consultation.java       # Aggregate Root
│   │   │           │   │   │   ├── ConsultationStatus.java # Sealed interface
│   │   │           │   │   │   ├── ScheduleSlot.java       # Entity
│   │   │           │   │   │   ├── Schedule.java           # Aggregate Root
│   │   │           │   │   │   └── ConsultationType.java   # Enum
│   │   │           │   │   ├── service/
│   │   │           │   │   │   └── BookingDomainService.java
│   │   │           │   │   ├── repository/
│   │   │           │   │   │   ├── ConsultationRepository.java
│   │   │           │   │   │   └── ScheduleRepository.java
│   │   │           │   │   └── event/
│   │   │           │   │       ├── ConsultationBookedEvent.java
│   │   │           │   │       └── ConsultationCancelledEvent.java
│   │   │           │   ├── application/
│   │   │           │   │   ├── usecase/
│   │   │           │   │   │   ├── BookConsultationUseCase.java
│   │   │           │   │   │   ├── CancelConsultationUseCase.java
│   │   │           │   │   │   └── GetScheduleUseCase.java
│   │   │           │   │   ├── command/
│   │   │           │   │   │   └── BookConsultationCommand.java
│   │   │           │   │   └── dto/
│   │   │           │   │       └── ConsultationDto.java
│   │   │           │   ├── infrastructure/
│   │   │           │   │   └── persistence/
│   │   │           │   │       └── JpaConsultationRepository.java
│   │   │           │   └── presentation/
│   │   │           │       └── web/
│   │   │           │           ├── ScheduleController.java
│   │   │           │           └── BookingController.java
│   │   │           │
│   │   │           ├── catalog/                   # Catalog Context (Landing)
│   │   │           │   ├── domain/
│   │   │           │   │   ├── model/
│   │   │           │   │   │   ├── TherapistCatalogEntry.java
│   │   │           │   │   │   └── PriceOption.java
│   │   │           │   │   └── service/
│   │   │           │   │       └── CatalogQueryService.java
│   │   │           │   ├── application/
│   │   │           │   │   └── usecase/
│   │   │           │   │       └── GetCatalogUseCase.java
│   │   │           │   └── presentation/
│   │   │           │       └── web/
│   │   │           │           ├── HomeController.java
│   │   │           │           ├── CatalogController.java
│   │   │           │           └── ProfileController.java
│   │   │           │
│   │   │           ├── messaging/                 # Messaging Context (Chat)
│   │   │           │   ├── domain/
│   │   │           │   │   ├── model/
│   │   │           │   │   │   ├── Conversation.java       # Aggregate Root
│   │   │           │   │   │   └── Message.java            # Entity
│   │   │           │   │   └── repository/
│   │   │           │   │       └── ConversationRepository.java
│   │   │           │   ├── application/
│   │   │           │   │   └── usecase/
│   │   │           │   │       ├── SendMessageUseCase.java
│   │   │           │   │       └── GetConversationsUseCase.java
│   │   │           │   └── presentation/
│   │   │           │       └── web/
│   │   │           │           └── ChatController.java
│   │   │           │
│   │   │           ├── billing/                   # Billing Context
│   │   │           │   ├── domain/
│   │   │           │   │   ├── model/
│   │   │           │   │   │   ├── Order.java              # Aggregate Root
│   │   │           │   │   │   ├── OrderStatus.java
│   │   │           │   │   │   ├── Wallet.java             # Aggregate Root
│   │   │           │   │   │   └── WalletTransaction.java
│   │   │           │   │   ├── service/
│   │   │           │   │   │   └── PaymentDomainService.java
│   │   │           │   │   └── event/
│   │   │           │   │       └── PaymentCompletedEvent.java
│   │   │           │   ├── application/
│   │   │           │   │   └── usecase/
│   │   │           │   │       └── ProcessPaymentUseCase.java
│   │   │           │   ├── infrastructure/
│   │   │           │   │   ├── payment/
│   │   │           │   │   │   └── WayForPayGateway.java
│   │   │           │   │   └── persistence/
│   │   │           │   └── presentation/
│   │   │           │       └── webhook/
│   │   │           │           └── PaymentWebhookController.java
│   │   │           │
│   │   │           ├── content/                   # Content Context (Blog, Profiles)
│   │   │           │   ├── domain/
│   │   │           │   │   └── model/
│   │   │           │   │       └── BlogPost.java
│   │   │           │   └── presentation/
│   │   │           │       └── web/
│   │   │           │           └── BlogController.java
│   │   │           │
│   │   │           ├── notification/              # Notification Context
│   │   │           │   ├── domain/
│   │   │           │   │   └── model/
│   │   │           │   │       └── Notification.java
│   │   │           │   ├── application/
│   │   │           │   │   └── NotificationService.java
│   │   │           │   ├── infrastructure/
│   │   │           │   │   ├── telegram/
│   │   │           │   │   │   └── TelegramAdapter.java
│   │   │           │   │   └── email/
│   │   │           │   │       └── EmailAdapter.java
│   │   │           │   └── presentation/
│   │   │           │       └── webhook/
│   │   │           │           └── TelegramWebhookController.java
│   │   │           │
│   │   │           └── admin/                     # Admin Context (Staff)
│   │   │               ├── application/
│   │   │               │   └── usecase/
│   │   │               │       ├── ManageTherapistsUseCase.java
│   │   │               │       └── ManageBlogUseCase.java
│   │   │               └── presentation/
│   │   │                   └── web/
│   │   │                       ├── AdminDashboardController.java
│   │   │                       └── TherapistManagementController.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       │
│   │       ├── messages.properties
│   │       ├── messages_uk.properties
│   │       ├── messages_ru.properties
│   │       │
│   │       ├── db/
│   │       │   └── migration/
│   │       │       └── V1__Initial_Schema.sql
│   │       │
│   │       ├── templates/
│   │       │   ├── layout/
│   │       │   │   ├── main.html
│   │       │   │   └── _footer.html
│   │       │   ├── fragments/
│   │       │   │   ├── header.html
│   │       │   │   └── sidebar.html
│   │       │   ├── landing/
│   │       │   │   ├── index.html
│   │       │   │   ├── therapist-list.html
│   │       │   │   ├── therapist-profile.html
│   │       │   │   ├── book-consultation.html
│   │       │   │   ├── checkout.html
│   │       │   │   └── blog/
│   │       │   ├── therapist/
│   │       │   │   ├── layout/
│   │       │   │   ├── dashboard.html
│   │       │   │   ├── schedule.html
│   │       │   │   └── settings.html
│   │       │   ├── user/
│   │       │   ├── staff/
│   │       │   └── error/
│   │       │
│   │       └── static/
│   │           ├── css/
│   │           │   └── landing/
│   │           ├── js/
│   │           │   └── landing.js
│   │           └── img/
│   │               ├── logo/
│   │               └── icons/
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── goodhelp/
│                   ├── therapist/
│                   ├── landing/
│                   ├── user/
│                   ├── staff/
│                   ├── billing/
│                   └── notification/
│
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
└── README.md
```

---

## Layered Architecture (Clean/Hexagonal with DDD)

```
┌─────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                          │
│  • Controllers (Web MVC, REST)                                  │
│  • View Models / Response DTOs                                  │
│  • Input validation (Bean Validation)                           │
├─────────────────────────────────────────────────────────────────┤
│                     APPLICATION LAYER                           │
│  • Use Cases (orchestrate domain)                               │
│  • Application Services                                         │
│  • Commands & Queries (CQRS-lite)                              │
│  • Transaction boundaries                                       │
├─────────────────────────────────────────────────────────────────┤
│                       DOMAIN LAYER                              │
│  • Aggregate Roots (rich behavior)                              │
│  • Entities (identity + behavior)                               │
│  • Value Objects (immutable)                                    │
│  • Domain Services (cross-aggregate logic)                      │
│  • Domain Events                                                │
│  • Repository Interfaces (not implementations!)                 │
├─────────────────────────────────────────────────────────────────┤
│                   INFRASTRUCTURE LAYER                          │
│  • Repository Implementations (JPA)                             │
│  • External Service Adapters                                    │
│  • Messaging (Kafka producers/consumers)                        │
│  • Framework configuration                                      │
└─────────────────────────────────────────────────────────────────┘

         ↑ Dependency direction: always point inward ↑
```

### Domain Layer (The Core)
Contains business logic. **Has NO dependencies on other layers.**

```java
// Aggregate Root with rich behavior
@Entity
public class Consultation {
    @Id private Long id;
    
    @Embedded
    private ConsultationStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Therapist therapist;
    
    @Embedded
    private TimeSlot timeSlot;
    
    @Embedded
    private Money price;
    
    // Business logic IN the domain
    public void cancel(CancellationReason reason, Clock clock) {
        if (!canBeCancelled(clock)) {
            throw new ConsultationCannotBeCancelledException(this.id);
        }
        this.status = ConsultationStatus.cancelled(reason, clock.instant());
        registerEvent(new ConsultationCancelledEvent(this.id, reason));
    }
    
    public boolean canBeCancelled(Clock clock) {
        return status.isActive() && 
               timeSlot.startsAfter(clock.instant().plus(Duration.ofHours(24)));
    }
}

// Value Object (immutable, no identity)
public record Money(int amountInCents, Currency currency) {
    public Money {
        if (amountInCents < 0) throw new IllegalArgumentException("Negative amount");
        Objects.requireNonNull(currency);
    }
    
    public Money applyDiscount(int percent) {
        return new Money(amountInCents - (amountInCents * percent / 100), currency);
    }
}

// Repository Interface (in domain layer)
public interface ConsultationRepository {
    Optional<Consultation> findById(Long id);
    Consultation save(Consultation consultation);
    List<Consultation> findUpcomingByUser(Long userId);
}
```

### Application Layer (Use Cases)
Orchestrates domain logic. Defines transaction boundaries.

```java
@Service
@Transactional
public class CancelConsultationUseCase {
    
    private final ConsultationRepository consultationRepository;
    private final DomainEventPublisher eventPublisher;
    private final Clock clock;
    
    public CancellationResult execute(CancelConsultationCommand command) {
        // 1. Load aggregate
        Consultation consultation = consultationRepository
            .findById(command.consultationId())
            .orElseThrow(() -> new ConsultationNotFoundException(command.consultationId()));
        
        // 2. Execute domain logic (behavior in aggregate)
        consultation.cancel(command.reason(), clock);
        
        // 3. Persist
        consultationRepository.save(consultation);
        
        // 4. Publish domain events
        consultation.getDomainEvents().forEach(eventPublisher::publish);
        
        return new CancellationResult(consultation.getId());
    }
}

// Command (immutable record)
public record CancelConsultationCommand(
    @NotNull Long consultationId,
    @NotNull CancellationReason reason
) {}
```

### Presentation Layer (Controllers)
Thin controllers. Input validation only.

```java
@Controller
@RequestMapping("/user/consultations")
public class UserConsultationController {
    
    private final CancelConsultationUseCase cancelConsultationUseCase;
    private final GetUserConsultationsUseCase getConsultationsUseCase;
    
    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, 
                         @Valid CancelForm form,
                         @AuthenticationPrincipal UserPrincipal user,
                         RedirectAttributes redirectAttributes) {
        try {
            var command = new CancelConsultationCommand(id, form.toReason());
            cancelConsultationUseCase.execute(command);
            redirectAttributes.addFlashAttribute("message", "Consultation cancelled");
        } catch (ConsultationCannotBeCancelledException e) {
            redirectAttributes.addFlashAttribute("error", "Cannot cancel this consultation");
        }
        return "redirect:/user/consultations";
    }
}
```

### Infrastructure Layer (Implementations)
Implements interfaces defined in domain layer.

```java
// Repository Implementation (in infrastructure)
@Repository
public class JpaConsultationRepository implements ConsultationRepository {
    
    private final ConsultationJpaRepository jpaRepository;
    
    @Override
    public Optional<Consultation> findById(Long id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    public Consultation save(Consultation consultation) {
        return jpaRepository.save(consultation);
    }
    
    @Override
    public List<Consultation> findUpcomingByUser(Long userId) {
        return jpaRepository.findByUserIdAndStatusActiveOrderByTimeSlotAsc(userId);
    }
}

// Spring Data JPA interface (infrastructure detail)
interface ConsultationJpaRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByUserIdAndStatusActiveOrderByTimeSlotAsc(Long userId);
}
```

---

## Security Architecture

### Authentication Flow

```
┌─────────────┐    ┌──────────────┐    ┌─────────────────┐
│   Browser   │───►│  Controller  │───►│  Auth Service   │
└─────────────┘    └──────────────┘    └─────────────────┘
                          │                     │
                          ▼                     ▼
                   ┌──────────────┐    ┌─────────────────┐
                   │    Filter    │    │   Repository    │
                   │    Chain     │    │  (Token Check)  │
                   └──────────────┘    └─────────────────┘
```

### Security Contexts

| Path Pattern | Firewall | Authentication |
|--------------|----------|----------------|
| `/therapist/**` | therapist | AutoLoginToken |
| `/user/**` | user | AutoLoginToken |
| `/staff/**` | staff | AutoLoginToken |
| `/billing/webhook` | billing | Signature verification |
| `/**` | main | Anonymous allowed |

### Role Hierarchy

```
ROLE_STAFF_SUPERUSER
        │
        ▼
ROLE_STAFF_USER

ROLE_PSIHOLOG
ROLE_TEST_PSIHOLOG

ROLE_USER
```

---

## Database Schema Design

### Naming Conventions
- Table names: `snake_case`, singular (e.g., `psiholog`, `user_consultation`)
- Column names: `snake_case` (e.g., `first_name`, `created_at`)
- Foreign keys: `{table}_id` (e.g., `psiholog_id`)
- Indexes: `idx_{table}_{column}` (e.g., `idx_psiholog_email`)

### Common Patterns

```sql
-- Audit columns (on most tables)
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

-- State machine pattern
state INTEGER NOT NULL DEFAULT 1

-- Soft delete (where applicable)
deleted_at TIMESTAMP NULL
```

---

## API Design

### URL Patterns

| Module | Base URL | Example |
|--------|----------|---------|
| Landing | `/` | `/therapist-list`, `/book-consultation/1` |
| User Cabinet | `/user` | `/user/dashboard`, `/user/settings` |
| Therapist Cabinet | `/therapist` | `/therapist/schedule`, `/therapist/chat` |
| Staff Cabinet | `/staff` | `/staff/therapists`, `/staff/blog` |
| Billing | `/billing` | `/billing/webhook` |

### Response Patterns

**Page Controllers:** Return view names for Thymeleaf
```java
@GetMapping("/dashboard")
public String dashboard(Model model) {
    model.addAttribute("data", data);
    return "therapist/dashboard";
}
```

**AJAX Controllers:** Return JSON
```java
@GetMapping("/api/schedule")
@ResponseBody
public ResponseEntity<ScheduleResponse> getSchedule() {
    return ResponseEntity.ok(scheduleService.getSchedule());
}
```

---

## Internationalization (i18n)

### Message Files
- `messages.properties` - English (default/fallback)
- `messages_uk.properties` - Ukrainian
- `messages_ru.properties` - Russian

### URL-Based Locale
- Ukrainian: `/page-name` (default)
- Russian: `/ru/page-name`
- English: `/en/page-name`

### Thymeleaf Usage
```html
<h1 th:text="#{landing.index.title}">Default Title</h1>
```

### Controller Usage
```java
@Autowired
private MessageSource messageSource;

String message = messageSource.getMessage("key", null, locale);
```

---

## Caching Strategy

### Redis Usage
- Session storage
- Rate limiting
- Temporary data caching

### Application-Level Caching
```java
@Cacheable("therapistProfiles")
public TherapistProfileDto getProfile(Long id) {
    // ...
}
```

---

## Error Handling

### Global Exception Handler

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(Model model) {
        return "error/404";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGeneric(Model model) {
        return "error/500";
    }
}
```

### Custom Exceptions
- `ResourceNotFoundException` - 404
- `BusinessException` - Business rule violations
- `AuthenticationException` - Auth failures

---

## Testing Strategy

### Unit Tests
- Service layer logic
- Utility classes
- Mocked dependencies

### Integration Tests
- Repository tests with `@DataJpaTest`
- Controller tests with `@WebMvcTest`
- Full integration with `@SpringBootTest`

### Test Naming
```java
@Test
void shouldReturnScheduleWhenTherapistExists() {
    // ...
}

@Test
void shouldThrowExceptionWhenScheduleNotFound() {
    // ...
}
```

---

## Logging Guidelines

### Log Levels
- `ERROR` - Unexpected errors requiring attention
- `WARN` - Potential issues
- `INFO` - Significant business events
- `DEBUG` - Detailed flow information

### Standard Pattern
```java
private static final Logger log = LoggerFactory.getLogger(MyService.class);

log.info("Processing order: {}", orderId);
log.error("Failed to process payment", exception);
```

---

## Configuration Management

### Profile-Based Configuration

```yaml
# application.yml (common)
spring:
  application:
    name: goodhelp

---
# application-dev.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/goodhelp
  jpa:
    show-sql: true

---
# application-prod.yml  
spring:
  datasource:
    url: ${DATABASE_URL}
  jpa:
    show-sql: false
```

### Environment Variables
- Database credentials
- API keys (WayForPay, Telegram)
- Feature flags

