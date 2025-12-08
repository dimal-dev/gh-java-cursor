# Sub-Stage 1.3: Spring Boot Application Entry Point

## Goal
Create the main Spring Boot application class and establish the base package structure.

---

## Files to Create

### 1. GoodHelpApplication.java

**Path:** `src/main/java/com/goodhelp/GoodHelpApplication.java`

```java
package com.goodhelp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the GoodHelp psychology consultation platform.
 * 
 * This application provides:
 * - Public landing pages for browsing therapists
 * - User cabinet for managing consultations
 * - Therapist cabinet for managing schedules and clients
 * - Staff/admin cabinet for platform management
 * - Billing integration for payment processing
 * - Notification system via Telegram and email
 */
@SpringBootApplication
@EnableScheduling
public class GoodHelpApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodHelpApplication.class, args);
    }
}
```

---

### 2. Package Structure with Placeholder Classes

Create package-info.java files to establish package structure:

#### config/package-info.java
```java
/**
 * Configuration classes for the GoodHelp application.
 * Contains Spring configuration for security, web MVC, Thymeleaf, and other components.
 */
package com.goodhelp.config;
```

#### common/package-info.java
```java
/**
 * Common/shared components used across all modules.
 * Includes base entities, exceptions, utilities, and shared services.
 */
package com.goodhelp.common;
```

#### therapist/package-info.java
```java
/**
 * Therapist module - handles the therapist cabinet functionality.
 * Includes authentication, schedule management, client management, and chat.
 */
package com.goodhelp.therapist;
```

#### landing/package-info.java
```java
/**
 * Landing module - handles public-facing website pages.
 * Includes home page, therapist catalog, booking flow, checkout, and blog.
 */
package com.goodhelp.landing;
```

#### user/package-info.java
```java
/**
 * User module - handles the user cabinet functionality.
 * Includes user authentication, dashboard, consultations, and chat.
 */
package com.goodhelp.user;
```

#### staff/package-info.java
```java
/**
 * Staff/Admin module - handles administrative functions.
 * Includes therapist management, blog management, and payouts.
 */
package com.goodhelp.staff;
```

#### billing/package-info.java
```java
/**
 * Billing module - handles payment processing.
 * Includes WayForPay integration, webhooks, orders, and wallet management.
 */
package com.goodhelp.billing;
```

#### notification/package-info.java
```java
/**
 * Notification module - handles notifications via Telegram and email.
 * Includes scheduled tasks and event-driven notifications.
 */
package com.goodhelp.notification;
```

---

### 3. Complete Package Structure

```
src/main/java/com/goodhelp/
├── GoodHelpApplication.java
├── config/
│   └── package-info.java
├── common/
│   ├── package-info.java
│   ├── entity/
│   ├── dto/
│   ├── exception/
│   ├── service/
│   └── util/
├── therapist/
│   ├── package-info.java
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   ├── service/
│   └── security/
├── landing/
│   ├── package-info.java
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
├── user/
│   ├── package-info.java
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   ├── service/
│   └── security/
├── staff/
│   ├── package-info.java
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   ├── service/
│   └── security/
├── billing/
│   ├── package-info.java
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
└── notification/
    ├── package-info.java
    ├── controller/
    └── service/
```

---

### 4. Basic Test Class

**Path:** `src/test/java/com/goodhelp/GoodHelpApplicationTests.java`

```java
package com.goodhelp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GoodHelpApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring context loads successfully
    }
}
```

---

## Implementation Steps

1. Create the main application class
2. Create all package directories
3. Create package-info.java files for documentation
4. Create the test class
5. Verify application starts (requires configuration files from next sub-stage)

---

## Verification

After completing sub-stage 1.4 (Configuration):

- [ ] Application compiles without errors
- [ ] `./mvnw clean compile` succeeds
- [ ] Package structure is correct
- [ ] Test class is in place

---

## Notes

The application won't fully start until:
- Configuration files are created (sub-stage 1.4)
- Database is available (sub-stage 1.2)

---

## Next Sub-Stage
Proceed to **1.4: Configuration Files**

