# Sub-Stage 8.5: Documentation

## Goal
Create comprehensive project documentation.

---

## README.md

```markdown
# GoodHelp - Psychology Consultation Platform

Online platform for booking psychology consultations.

## Tech Stack

- Java 21
- Spring Boot 3.5.8
- PostgreSQL 18
- Apache Kafka 4.1
- Thymeleaf 3.1

## Prerequisites

- Java 21+
- Docker & Docker Compose
- Maven 3.9+

## Quick Start

1. Start infrastructure:
   ```bash
   cd docker
   docker-compose up -d
   ```

2. Run application:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Access: http://localhost:8080

## Project Structure

```
src/main/java/com/goodhelp/
├── config/          # Configuration classes
├── shared/          # Shared kernel (value objects, base classes)
├── identity/        # Authentication & users
├── booking/         # Consultations & scheduling
├── catalog/         # Therapist catalog
├── messaging/       # Chat functionality
├── billing/         # Payments & wallets
├── content/         # Blog & profiles
├── notification/    # Email & Telegram
└── admin/           # Staff administration
```

## Configuration

Copy `application-dev.yml.example` to `application-dev.yml` and configure:

- Database connection
- Kafka connection
- Email settings
- WayForPay credentials
- Telegram bot token

## Testing

```bash
# Unit tests
./mvnw test

# Integration tests
./mvnw verify -P integration-tests
```

## Deployment

See `docs/deployment.md` for production deployment guide.
```

---

## API Documentation

### OpenAPI/Swagger

```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("GoodHelp API")
                .version("1.0")
                .description("Psychology consultation platform API"))
            .addSecurityItem(new SecurityRequirement().addList("session"))
            .components(new Components()
                .addSecuritySchemes("session", new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.COOKIE)
                    .name("JSESSIONID")));
    }
}
```

Access at: `/swagger-ui.html`

---

## Code Documentation

### Package-level Javadoc
```java
/**
 * Booking bounded context.
 * 
 * Handles consultation booking, scheduling, and management.
 * 
 * <h2>Key Aggregates</h2>
 * <ul>
 *   <li>{@link Consultation} - Booked session between user and therapist</li>
 *   <li>{@link Schedule} - Therapist's availability</li>
 * </ul>
 * 
 * <h2>Use Cases</h2>
 * <ul>
 *   <li>{@link BookConsultationUseCase} - Book a new consultation</li>
 *   <li>{@link CancelConsultationUseCase} - Cancel existing booking</li>
 * </ul>
 */
package com.goodhelp.booking;
```

### Method Documentation
```java
/**
 * Books a consultation with a therapist.
 *
 * @param command booking details including therapist, slot, and user info
 * @return result containing consultation ID if successful
 * @throws SlotNotAvailableException if slot is already booked
 * @throws TherapistNotActiveException if therapist is inactive
 */
public BookingResult execute(BookConsultationCommand command);
```

---

## Architecture Decision Records

Create `docs/adr/` folder for important decisions:
- ADR-001: Use DDD Architecture
- ADR-002: Kafka over RabbitMQ
- ADR-003: Auto-login Authentication
- ADR-004: Thymeleaf over SPA

---

## Verification
- [ ] README complete
- [ ] OpenAPI docs accessible
- [ ] Key classes documented
- [ ] Architecture decisions recorded

---

## Next
Proceed to **8.6: Final Testing**

