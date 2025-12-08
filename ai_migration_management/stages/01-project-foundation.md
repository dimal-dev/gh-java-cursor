# Stage 1: Project Foundation

## Overview

This stage sets up the foundational infrastructure for the Java/Spring Boot application. All subsequent stages depend on successful completion of this stage.

**Estimated Sub-stages:** 8  
**Dependencies:** None  
**Next Stage:** Stage 2 - Therapist Module

---

## Sub-Stage Files

| Sub-Stage | File | Status |
|-----------|------|--------|
| 1.1 | `substages/01-01-maven-setup.md` | NOT_STARTED |
| 1.2 | `substages/01-02-docker-compose.md` | NOT_STARTED |
| 1.3 | `substages/01-03-spring-boot-app.md` | NOT_STARTED |
| 1.4 | `substages/01-04-configuration.md` | NOT_STARTED |
| 1.5 | `substages/01-05-security-skeleton.md` | NOT_STARTED |
| 1.6 | `substages/01-06-thymeleaf-i18n.md` | NOT_STARTED |
| 1.7 | `substages/01-07-common-module.md` | NOT_STARTED |
| 1.8 | `substages/01-08-database-schema.md` | NOT_STARTED |

---

## Sub-Stage 1.1: Maven Project Setup

**Goal:** Create the Maven project structure with all necessary dependencies.

**Tasks:**
1. Create `pom.xml` with all dependencies
2. Create Maven wrapper files
3. Create `.gitignore`
4. Set up project directory structure

**Key Dependencies:**
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-thymeleaf
- spring-boot-starter-validation
- postgresql driver
- spring-kafka
- spring-boot-starter-data-redis
- springdoc-openapi-starter-webmvc-ui
- thymeleaf-layout-dialect
- lombok
- mapstruct

**Deliverables:**
- `pom.xml`
- `mvnw`, `mvnw.cmd`
- `.mvn/wrapper/maven-wrapper.properties`
- `.gitignore`

---

## Sub-Stage 1.2: Docker Compose Setup

**Goal:** Create Docker Compose configuration for local development infrastructure.

**Tasks:**
1. Create `docker/docker-compose.yml`
2. Configure PostgreSQL container
3. Configure Redis container
4. Configure Kafka container (with Zookeeper)
5. Create initialization scripts

**Services:**
- PostgreSQL 18.1 on port 5432
- Redis 7.x on port 6379
- Kafka 4.1.0 on port 9092
- Zookeeper on port 2181

**Deliverables:**
- `docker/docker-compose.yml`
- `docker/postgres/init.sql`

---

## Sub-Stage 1.3: Spring Boot Application Entry Point

**Goal:** Create the main Spring Boot application class and package structure.

**Tasks:**
1. Create main application class `GoodHelpApplication.java`
2. Create base package structure for all modules
3. Verify application starts successfully

**Package Structure:**
```
com.goodhelp
├── GoodHelpApplication.java
├── config/
├── common/
├── therapist/
├── landing/
├── user/
├── staff/
├── billing/
└── notification/
```

**Deliverables:**
- `src/main/java/com/goodhelp/GoodHelpApplication.java`
- All package placeholder classes

---

## Sub-Stage 1.4: Configuration Files

**Goal:** Set up application configuration for different environments.

**Tasks:**
1. Create `application.yml` with common settings
2. Create `application-dev.yml` for development
3. Create `application-prod.yml` for production
4. Configure datasource, JPA, logging

**Configuration Areas:**
- Server port (8080)
- Database connection
- JPA/Hibernate settings
- Redis connection
- Kafka connection
- Logging configuration
- Spring profiles

**Deliverables:**
- `src/main/resources/application.yml`
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-prod.yml`

---

## Sub-Stage 1.5: Security Configuration Skeleton

**Goal:** Create the security configuration structure that will be expanded in later stages.

**Tasks:**
1. Create `SecurityConfig.java`
2. Define security filter chains for each area
3. Set up CSRF, CORS configuration
4. Create placeholder authentication providers

**Security Contexts:**
- `/therapist/**` - Therapist authentication
- `/user/**` - User authentication
- `/staff/**` - Staff authentication
- `/billing/webhook` - Signature-based authentication
- `/**` - Public access

**Deliverables:**
- `src/main/java/com/goodhelp/config/SecurityConfig.java`

---

## Sub-Stage 1.6: Thymeleaf & i18n Setup

**Goal:** Configure Thymeleaf templating and internationalization.

**Tasks:**
1. Create `ThymeleafConfig.java`
2. Create `MessageSourceConfig.java`
3. Create `WebMvcConfig.java` with locale resolver
4. Create message property files
5. Create base layout template

**Locale Configuration:**
- Default locale: `en` (English)
- Supported locales: `en`, `uk`, `ru`
- URL-based locale: `/`, `/ru/`, `/uk/`

**Translation Files:**
- `messages.properties` (English - default)
- `messages_uk.properties` (Ukrainian)
- `messages_ru.properties` (Russian)

**Deliverables:**
- Configuration classes
- `src/main/resources/messages*.properties`
- `src/main/resources/templates/layout/main.html`

---

## Sub-Stage 1.7: Common Module

**Goal:** Create shared utilities, exceptions, and base classes.

**Tasks:**
1. Create `BaseEntity.java` with audit fields
2. Create global exception handler
3. Create custom exceptions
4. Create utility classes from PHP project

**Classes to Create:**

*Exceptions:*
- `GlobalExceptionHandler.java`
- `ResourceNotFoundException.java`
- `BusinessException.java`
- `AuthenticationException.java`

*Utilities:*
- `TimezoneHelper.java`
- `DateLocalizedHelper.java`
- `PasswordGenerator.java`
- `Pluralizer.java`
- `ArrayIndexer.java`

**Deliverables:**
- All common module classes in `com.goodhelp.common`

---

## Sub-Stage 1.8: Database Schema

**Goal:** Create initial database schema with all tables.

**Tasks:**
1. Create Flyway/manual migration script
2. Define all tables based on entity mapping
3. Add indexes and constraints
4. Create seed data script (optional)

**Tables to Create:**
- psiholog
- psiholog_profile
- psiholog_schedule
- psiholog_price
- psiholog_settings
- psiholog_autologin_token
- psiholog_user_notes
- user
- user_autologin_token
- user_consultation
- chat_message
- promocode
- user_promocode
- billing_order
- billing_checkout
- order_log
- order_psiholog_schedule
- user_wallet
- user_wallet_operation
- staff_user
- staff_user_autologin_token
- blog_post
- image

**Deliverables:**
- `src/main/resources/db/migration/V1__Initial_Schema.sql`

---

## Verification Checklist

After completing Stage 1, verify:

- [ ] Maven build succeeds (`./mvnw clean compile`)
- [ ] Docker Compose starts all services
- [ ] Application starts without errors
- [ ] Database connection works
- [ ] Redis connection works
- [ ] Thymeleaf renders test page
- [ ] i18n switching works
- [ ] Database schema is created correctly

---

## Transition to Stage 2

Once all sub-stages are complete and verified:

1. Update `99-PROGRESS-TRACKER.md`
2. Mark Stage 1 as COMPLETED
3. Proceed to Stage 2: Therapist Module

