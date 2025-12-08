# Domain-Driven Design (DDD) Principles for GoodHelp

## Overview

This document defines how DDD principles are applied to the GoodHelp Java application. The goal is to build a **modern, idiomatic Java/Spring Boot application** that delivers the same features as the original PHP application but is architected "the Java way" using industry best practices.

---

## Core Philosophy

> **"Build a Java application, not a translated PHP application"**

This means:
- ✅ Use Java idioms, patterns, and conventions
- ✅ Apply DDD tactical and strategic patterns
- ✅ Leverage Spring Boot's strengths
- ✅ Create a rich domain model with business logic in the domain
- ✅ Use the PHP codebase as a **requirements specification**, not a design template
- ❌ Do NOT copy PHP structure verbatim
- ❌ Do NOT create anemic domain models (getters/setters only)
- ❌ Do NOT put business logic in controllers

---

## Strategic DDD: Bounded Contexts

The application is divided into **Bounded Contexts**, each with its own domain model:

```
┌─────────────────────────────────────────────────────────────────────┐
│                         GoodHelp Platform                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                │
│  │   Booking   │  │  Identity   │  │   Billing   │                │
│  │   Context   │  │   Context   │  │   Context   │                │
│  │             │  │             │  │             │                │
│  │ • Schedules │  │ • Users     │  │ • Orders    │                │
│  │ • Consults  │  │ • Psihologs │  │ • Payments  │                │
│  │ • Chat      │  │ • Staff     │  │ • Wallets   │                │
│  └─────────────┘  └─────────────┘  └─────────────┘                │
│                                                                     │
│  ┌─────────────┐  ┌─────────────┐                                 │
│  │   Content   │  │Notification │                                 │
│  │   Context   │  │   Context   │                                 │
│  │             │  │             │                                 │
│  │ • Blog      │  │ • Telegram  │                                 │
│  │ • Profiles  │  │ • Email     │                                 │
│  └─────────────┘  └─────────────┘                                 │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### Module Boundaries

| Module | Bounded Context | Core Domain Concepts |
|--------|-----------------|---------------------|
| `psiholog` | Identity + Booking | Therapist, Schedule, Availability |
| `user` | Identity + Booking | User, Consultation, Chat |
| `landing` | Content + Booking | TherapistCatalog, BookingProcess |
| `staff` | Identity + Content | Administration, ContentManagement |
| `billing` | Billing | Order, Payment, Wallet |
| `notification` | Notification | NotificationChannel, Message |

---

## Tactical DDD Patterns

### 1. Aggregates & Aggregate Roots

Each bounded context has **aggregate roots** that control access to related entities:

```java
// GOOD: Aggregate Root controls access to child entities
@Entity
public class Consultation {  // Aggregate Root
    @Id
    private Long id;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleSlot> bookedSlots = new ArrayList<>();
    
    @Embedded
    private ConsultationStatus status;
    
    // Business logic IN the domain
    public void cancel(CancellationReason reason, LocalDateTime cancelledAt) {
        if (!this.status.canBeCancelled()) {
            throw new ConsultationCannotBeCancelledException(this.id);
        }
        this.status = this.status.cancel(reason, cancelledAt);
        // Slots are released via aggregate
        this.bookedSlots.forEach(ScheduleSlot::release);
    }
    
    public boolean isCancellableByUser(LocalDateTime now) {
        return this.status.isActive() && 
               this.getScheduledTime().isAfter(now.plusHours(24));
    }
}
```

```java
// BAD: Anemic model with logic in service (PHP-style translation)
@Entity
public class Consultation {
    @Id private Long id;
    private Integer state;  // Just a number
    // Only getters/setters, no behavior
}

@Service
public class ConsultationService {
    public void cancel(Long id, int reason) {
        Consultation c = repo.findById(id);
        c.setState(5);  // Magic number!
        // Logic scattered in service
    }
}
```

### 2. Value Objects

Use **immutable Value Objects** for concepts without identity:

```java
// Value Object: Money
@Embeddable
public record Money(
    int amount,  // In minor units (cents/kopecks)
    Currency currency
) {
    public Money {
        if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative");
        Objects.requireNonNull(currency);
    }
    
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException(this.currency, other.currency);
        }
        return new Money(this.amount + other.amount, this.currency);
    }
    
    public Money applyDiscount(int discountPercent) {
        int discountedAmount = this.amount - (this.amount * discountPercent / 100);
        return new Money(discountedAmount, this.currency);
    }
    
    public String formatted() {
        return String.format("%d.%02d %s", amount / 100, amount % 100, currency.symbol());
    }
}

// Value Object: TimeSlot
@Embeddable  
public record TimeSlot(
    LocalDateTime startTime,
    Duration duration
) {
    public TimeSlot {
        Objects.requireNonNull(startTime);
        Objects.requireNonNull(duration);
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
    }
    
    public LocalDateTime endTime() {
        return startTime.plus(duration);
    }
    
    public boolean overlaps(TimeSlot other) {
        return this.startTime.isBefore(other.endTime()) && 
               other.startTime.isBefore(this.endTime());
    }
}

// Value Object: Email
public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    public Email {
        if (value == null || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException(value);
        }
        value = value.toLowerCase().trim();
    }
}
```

### 3. Domain Services

Business logic that doesn't belong to a single entity:

```java
@Service
public class BookingDomainService {
    
    /**
     * Domain service for complex booking logic spanning multiple aggregates.
     */
    public Consultation createBooking(
            Therapist therapist,
            User user,
            ScheduleSlot slot,
            ConsultationType type,
            Money price
    ) {
        // Invariant checks
        if (!therapist.isActive()) {
            throw new TherapistNotAvailableException(therapist.getId());
        }
        
        if (!slot.isAvailable()) {
            throw new SlotNotAvailableException(slot.getId());
        }
        
        if (!therapist.hasSlot(slot)) {
            throw new SlotDoesNotBelongToTherapistException();
        }
        
        // Create the consultation (domain logic)
        Consultation consultation = Consultation.create(
            user,
            therapist,
            slot,
            type,
            price
        );
        
        // Reserve the slot (aggregate behavior)
        slot.reserve(consultation);
        
        return consultation;
    }
}
```

### 4. Application Services (Use Cases)

Orchestrate domain logic and infrastructure:

```java
@Service
@Transactional
public class BookConsultationUseCase {
    
    private final TherapistRepository therapistRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final ConsultationRepository consultationRepository;
    private final BookingDomainService bookingDomainService;
    private final DomainEventPublisher eventPublisher;
    
    public BookingResult execute(BookConsultationCommand command) {
        // 1. Load aggregates
        Therapist therapist = therapistRepository
            .findById(command.therapistId())
            .orElseThrow(() -> new TherapistNotFoundException(command.therapistId()));
            
        User user = userRepository
            .findById(command.userId())
            .orElseThrow(() -> new UserNotFoundException(command.userId()));
            
        ScheduleSlot slot = scheduleRepository
            .findById(command.slotId())
            .orElseThrow(() -> new SlotNotFoundException(command.slotId()));
        
        // 2. Execute domain logic
        Consultation consultation = bookingDomainService.createBooking(
            therapist,
            user,
            slot,
            command.consultationType(),
            command.price()
        );
        
        // 3. Persist
        consultationRepository.save(consultation);
        scheduleRepository.save(slot);
        
        // 4. Publish domain event
        eventPublisher.publish(new ConsultationBookedEvent(
            consultation.getId(),
            therapist.getId(),
            user.getId(),
            slot.getTimeSlot()
        ));
        
        return new BookingResult(consultation.getId());
    }
}
```

### 5. Domain Events

Use events for cross-context communication:

```java
// Domain Event
public record ConsultationBookedEvent(
    Long consultationId,
    Long therapistId,
    Long userId,
    TimeSlot timeSlot,
    LocalDateTime occurredAt
) implements DomainEvent {
    public ConsultationBookedEvent(Long consultationId, Long therapistId, 
                                    Long userId, TimeSlot timeSlot) {
        this(consultationId, therapistId, userId, timeSlot, LocalDateTime.now());
    }
}

// Event Publisher using Spring + Kafka
@Service
public class KafkaDomainEventPublisher implements DomainEventPublisher {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationEventPublisher localPublisher;
    
    @Override
    public void publish(DomainEvent event) {
        // Local handling (same transaction)
        localPublisher.publishEvent(event);
        
        // Async cross-service (Kafka)
        kafkaTemplate.send("domain-events", event.getClass().getSimpleName(), event);
    }
}

// Event Handler (Notification Context)
@Component
public class ConsultationEventHandler {
    
    private final NotificationService notificationService;
    
    @EventListener
    public void onConsultationBooked(ConsultationBookedEvent event) {
        notificationService.notifyTherapist(
            event.therapistId(),
            NotificationType.NEW_BOOKING,
            event.timeSlot()
        );
    }
}
```

---

## Layered Architecture (Onion/Hexagonal)

```
┌─────────────────────────────────────────────────────────────────┐
│                        Presentation Layer                        │
│  Controllers, DTOs, View Models, REST Endpoints, Thymeleaf      │
├─────────────────────────────────────────────────────────────────┤
│                        Application Layer                         │
│  Use Cases, Application Services, Commands/Queries, DTOs        │
├─────────────────────────────────────────────────────────────────┤
│                         Domain Layer                             │
│  Entities, Value Objects, Aggregates, Domain Services,          │
│  Repository Interfaces, Domain Events                           │
├─────────────────────────────────────────────────────────────────┤
│                      Infrastructure Layer                        │
│  Repository Implementations, External Services, Persistence,    │
│  Messaging (Kafka), Email, Telegram                             │
└─────────────────────────────────────────────────────────────────┘

Dependency Rule: Inner layers know nothing about outer layers
```

### Package Structure per Module

```
com.goodhelp.booking/
├── domain/
│   ├── model/
│   │   ├── Consultation.java          # Aggregate Root
│   │   ├── ConsultationStatus.java    # Value Object
│   │   ├── ScheduleSlot.java          # Entity
│   │   ├── TimeSlot.java              # Value Object
│   │   └── ConsultationType.java      # Enum
│   ├── service/
│   │   └── BookingDomainService.java  # Domain Service
│   ├── repository/
│   │   └── ConsultationRepository.java # Interface only
│   └── event/
│       ├── ConsultationBookedEvent.java
│       └── ConsultationCancelledEvent.java
├── application/
│   ├── usecase/
│   │   ├── BookConsultationUseCase.java
│   │   ├── CancelConsultationUseCase.java
│   │   └── GetUpcomingConsultationsUseCase.java
│   ├── command/
│   │   ├── BookConsultationCommand.java
│   │   └── CancelConsultationCommand.java
│   └── dto/
│       ├── ConsultationDto.java
│       └── BookingResultDto.java
├── infrastructure/
│   ├── persistence/
│   │   ├── JpaConsultationRepository.java  # Implementation
│   │   └── ConsultationJpaEntity.java      # JPA mapping
│   └── messaging/
│       └── ConsultationEventPublisher.java
└── presentation/
    ├── web/
    │   ├── BookingController.java
    │   └── ConsultationViewController.java
    └── api/
        └── BookingRestController.java
```

---

## Java Idioms & Best Practices

### Use Records for DTOs and Value Objects

```java
// Command (immutable, validated)
public record BookConsultationCommand(
    @NotNull Long therapistId,
    @NotNull Long slotId,
    @NotNull ConsultationType type,
    @NotBlank String userEmail,
    String userName,
    String userPhone
) {
    public BookConsultationCommand {
        // Compact constructor for validation
        Objects.requireNonNull(therapistId, "therapistId required");
        Objects.requireNonNull(slotId, "slotId required");
    }
}

// DTO for API responses
public record ConsultationDto(
    Long id,
    String therapistName,
    LocalDateTime scheduledAt,
    String status,
    MoneyDto price
) {
    public static ConsultationDto from(Consultation consultation) {
        return new ConsultationDto(
            consultation.getId(),
            consultation.getTherapist().getFullName(),
            consultation.getScheduledTime(),
            consultation.getStatus().displayName(),
            MoneyDto.from(consultation.getPrice())
        );
    }
}
```

### Use Optional Correctly

```java
// GOOD: Use Optional for potentially absent return values
public interface TherapistRepository {
    Optional<Therapist> findById(Long id);
    Optional<Therapist> findByEmail(Email email);
    List<Therapist> findAllActive();  // Empty list, not Optional
}

// GOOD: Handle Optional in service
public TherapistDto getTherapist(Long id) {
    return therapistRepository.findById(id)
        .map(TherapistDto::from)
        .orElseThrow(() -> new TherapistNotFoundException(id));
}

// BAD: Using Optional as field
public class Consultation {
    private Optional<Promocode> promocode;  // DON'T DO THIS
}

// GOOD: Nullable field with Optional getter
public class Consultation {
    private Promocode promocode;  // Can be null
    
    public Optional<Promocode> getPromocode() {
        return Optional.ofNullable(promocode);
    }
}
```

### Use Sealed Classes for Type Hierarchies

```java
// Sealed interface for consultation states
public sealed interface ConsultationState 
    permits ActiveState, CompletedState, CancelledState {
    
    String displayName();
    boolean canBeCancelled();
    boolean canBeRescheduled();
}

public record ActiveState(LocalDateTime scheduledFor) implements ConsultationState {
    @Override public String displayName() { return "Scheduled"; }
    @Override public boolean canBeCancelled() { return true; }
    @Override public boolean canBeRescheduled() { return true; }
}

public record CompletedState(LocalDateTime completedAt) implements ConsultationState {
    @Override public String displayName() { return "Completed"; }
    @Override public boolean canBeCancelled() { return false; }
    @Override public boolean canBeRescheduled() { return false; }
}

public record CancelledState(
    LocalDateTime cancelledAt, 
    CancellationReason reason
) implements ConsultationState {
    @Override public String displayName() { return "Cancelled"; }
    @Override public boolean canBeCancelled() { return false; }
    @Override public boolean canBeRescheduled() { return false; }
}
```

### Use Builder Pattern for Complex Objects

```java
@Builder
public class Therapist {
    private final Long id;
    private final Email email;
    private final TherapistProfile profile;
    private final TherapistSettings settings;
    private TherapistStatus status;
    
    // Rich behavior...
}

// Usage
Therapist therapist = Therapist.builder()
    .email(new Email("doctor@example.com"))
    .profile(profile)
    .settings(defaultSettings)
    .status(TherapistStatus.ACTIVE)
    .build();
```

---

## Anti-Patterns to Avoid

### ❌ Anemic Domain Model
```java
// BAD: Entity is just a data container
@Entity
public class Consultation {
    private Long id;
    private Integer state;
    private Long userId;
    private Long psihologId;
    // Only getters and setters
}

// Logic scattered in service
@Service
public class ConsultationService {
    public void cancel(Long id) {
        var c = repo.findById(id);
        c.setState(5);  // What is 5?
        repo.save(c);
    }
}
```

### ❌ Transaction Script
```java
// BAD: All logic in service methods (PHP-style)
@Service
public class BookingService {
    public void bookConsultation(Long psihologId, Long slotId, ...) {
        // 200 lines of procedural code
        // Direct database queries
        // Business rules mixed with persistence
    }
}
```

### ❌ Smart UI / Fat Controller
```java
// BAD: Business logic in controller
@Controller
public class BookingController {
    @PostMapping("/book")
    public String book(BookingForm form) {
        // Validation logic
        // Business rules
        // Database operations
        // Everything in one place
    }
}
```

---

## Summary: The Java Way

| Aspect | PHP Style | Java DDD Style |
|--------|-----------|----------------|
| Domain Model | Anemic (getters/setters) | Rich (behavior in entities) |
| Business Logic | In Services/Controllers | In Domain Model + Domain Services |
| Data Transfer | Arrays, raw data | Records, DTOs |
| Validation | In Controllers | In Value Objects + Commands |
| Events | None / procedural | Domain Events + Event Handlers |
| Persistence | Active Record pattern | Repository pattern |
| State | Integer constants | Enums / Sealed classes |
| Null Handling | Null checks | Optional + Value Objects |
| Immutability | Mutable everywhere | Immutable where possible |

The PHP application serves as **functional requirements** - we deliver the same user experience, but the internal architecture follows modern Java/Spring Boot practices with DDD principles.

