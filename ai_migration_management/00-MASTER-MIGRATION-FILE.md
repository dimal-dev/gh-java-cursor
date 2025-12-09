# GoodHelp Migration Master File

## Quick Reference for AI Agent

This is the master control file for migrating GoodHelp from PHP/Symfony to Java/Spring Boot. **Read this file at the start of every session.**

---

## ⚠️ CRITICAL: Java-Native Development Philosophy

> **BUILD A JAVA APPLICATION, NOT A PHP TRANSLATION**

The PHP codebase is a **requirements specification**, not a design template. We are building a **modern, production-grade Java/Spring Boot application** using:

- ✅ **Domain-Driven Design (DDD)** - Rich domain models, aggregates, value objects
- ✅ **Java idioms** - Records, sealed classes, Optional, streams
- ✅ **Clean Architecture** - Proper layering, dependency inversion
- ✅ **Spring Boot best practices** - Proper use of IoC, AOP, transactions
- ❌ **NOT** copying PHP structure or patterns
- ❌ **NOT** creating anemic models (getters/setters only)
- ❌ **NOT** putting business logic in controllers/services only

**See `03-DDD-DESIGN-PRINCIPLES.md` for detailed patterns and examples.**

---

## Current Migration Progress

| Stage | Module | Status | Progress File |
|-------|--------|--------|---------------|
| 1 | Project Foundation | COMPLETED | `stages/01-project-foundation.md` |
| 2 | Therapist Module | COMPLETED | `stages/02-psiholog-module.md` |
| 3 | Landing Module | COMPLETED | `stages/03-landing-module.md` |
| 4 | User Module | NOT_STARTED | `stages/04-user-module.md` |
| 5 | Staff Module | NOT_STARTED | `stages/05-staff-module.md` |
| 6 | Billing Module | NOT_STARTED | `stages/06-billing-module.md` |
| 7 | Notification Module | NOT_STARTED | `stages/07-notification-module.md` |
| 8 | Integration & Polish | NOT_STARTED | `stages/08-integration-polish.md` |

**Current Active Stage:** Stage 4 - User Module  
**Current Sub-Stage:** 4.5 - Dashboard ✅ COMPLETED  
**Next Sub-Stage:** 4.6 - Chat functionality  
**Last Updated:** 2025-12-08

---

## Technology Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 (LTS) | Core language |
| Spring Boot | 3.5.8 | Application framework |
| Spring Web MVC | 6.2.x | REST & MVC controllers |
| Spring Data JPA | 3.5.x | Data access layer |
| Hibernate | 6.6.x | ORM implementation |
| PostgreSQL | 18.1 | Primary database |
| Apache Kafka | 4.1.0 | Message queue (replaces RabbitMQ) |
| Maven | 3.9.11 | Build tool |
| Thymeleaf | 3.1.x | Server-side templating |
| SpringDoc OpenAPI | 3.0.0 | API documentation |
| JUnit | 5.13.4 | Testing framework |

### Infrastructure
| Technology | Version | Purpose |
|------------|---------|---------|
| Docker | Engine 29.x | Containerization |
| Docker Compose | Latest | Multi-container orchestration |
| Redis | 7.x | Session storage & caching |

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| Vanilla JavaScript | ES6+ | Client-side logic |
| SCSS/SASS | Latest | Styling |
| Thymeleaf | 3.1.x | Template engine (replaces Twig) |

---

## Critical Migration Rules

### 1. Feature Parity (User-Facing)
- **Every user-facing feature from the PHP project MUST be delivered**
- The UI and UX must look and work identically to the original
- Business outcomes must be preserved exactly
- PHP code is **requirements specification**, not implementation guide

### 2. Domain-Driven Design
- **Rich Domain Model** - Business logic lives IN domain entities, not services
- **Aggregates** - Each bounded context has clear aggregate roots
- **Value Objects** - Use immutable value objects for domain concepts (Money, Email, TimeSlot)
- **Domain Events** - Decouple contexts via events (Kafka integration)
- **Ubiquitous Language** - Use domain terms in code (Consultation, not UserConsultation)
- See `03-DDD-DESIGN-PRINCIPLES.md` for complete patterns

### 3. Modern Java Idioms
- **Records** - For DTOs, Commands, Value Objects, Events
- **Sealed Classes** - For state machines and type hierarchies
- **Optional** - For potentially absent values (not as fields)
- **Streams** - For collection processing
- **var** - Where it improves readability
- **Pattern Matching** - Switch expressions, instanceof patterns

### 4. Clean Architecture
- **Layers**: Presentation → Application → Domain → Infrastructure
- **Dependency Rule**: Inner layers know nothing about outer layers
- **Use Cases**: Application services orchestrate domain logic
- **Repositories**: Interfaces in domain, implementations in infrastructure

### 5. Code Quality Standards
- Self-documenting code with descriptive naming
- Comprehensive unit tests (domain) and integration tests (use cases)
- Proper logging with SLF4J
- Input validation in Value Objects and Commands
- Security-first approach
- No magic numbers - use enums and constants

### 6. Internationalization (i18n)
- Support 3 languages: English (default), Ukrainian, Russian
- Use Spring's `MessageSource` for translations
- Preserve all existing translations from PHP
- English translations derived from Ukrainian versions

---

## Project Structure Overview

```
target_java_project/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/goodhelp/
│       │       ├── GoodHelpApplication.java
│       │       ├── config/
│       │       ├── common/
│       │       ├── therapist/
│       │       ├── landing/
│       │       ├── user/
│       │       ├── staff/
│       │       ├── billing/
│       │       └── notification/
│       └── resources/
│           ├── application.yml
│           ├── messages.properties (English - default)
│           ├── messages_uk.properties (Ukrainian)
│           ├── messages_ru.properties (Russian)
│           ├── templates/
│           └── static/
├── src/test/
├── docker/
│   └── docker-compose.yml
├── pom.xml
└── README.md
```

---

## Module Dependencies

```
┌─────────────────────────────────────────────────────────────┐
│                        Common Module                         │
│  (Shared utilities, entities, configurations)               │
└─────────────────────────────────────────────────────────────┘
                              ▲
          ┌───────────────────┼───────────────────┐
          │                   │                   │
    ┌─────┴─────┐      ┌─────┴─────┐      ┌─────┴─────┐
    │ Therapist │      │  Landing  │      │   User    │
    │  Module   │◄────►│  Module   │◄────►│  Module   │
    └─────┬─────┘      └─────┬─────┘      └─────┬─────┘
          │                  │                   │
          │           ┌──────┴──────┐           │
          │           │   Billing   │           │
          └──────────►│   Module    │◄──────────┘
                      └──────┬──────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
    ┌─────┴─────┐     ┌─────┴─────┐     ┌─────┴─────┐
    │   Staff   │     │Notification│     │   Blog    │
    │  Module   │     │   Module  │     │(in Landing)│
    └───────────┘     └───────────┘     └───────────┘
```

---

## Key Files Reference

| Purpose | File Path |
|---------|-----------|
| Architecture & Structure | `01-ARCHITECTURE.md` |
| Entity Mapping (PHP→Java) | `02-ENTITY-MAPPING.md` |
| **DDD Design Principles** | `03-DDD-DESIGN-PRINCIPLES.md` ⭐ |
| Stage 1 Details | `stages/01-project-foundation.md` |
| Stage 2 Details | `stages/02-psiholog-module.md` |
| Stage 3 Details | `stages/03-landing-module.md` |
| Stage 4 Details | `stages/04-user-module.md` |
| Stage 5 Details | `stages/05-staff-module.md` |
| Stage 6 Details | `stages/06-billing-module.md` |
| Stage 7 Details | `stages/07-notification-module.md` |
| Stage 8 Details | `stages/08-integration-polish.md` |
| Progress Tracker | `99-PROGRESS-TRACKER.md` |

⭐ = Must read for understanding Java implementation approach

---

## Session Workflow

When the user says "Continue the migration using the master file":

1. **Read this file** to understand current status
2. **Check `99-PROGRESS-TRACKER.md`** for exact progress point
3. **Read the relevant stage file** from `stages/` directory
4. **Read the relevant sub-stage file** from `substages/` directory
5. **Continue implementation** from where it left off
6. **Update progress** in `99-PROGRESS-TRACKER.md` after each sub-stage

---

## Original PHP Project Reference

| Module | Controller Path | Entity Path | Service Path |
|--------|-----------------|-------------|--------------|
| Psiholog | `src/Psiholog/Controller/` | `src/Psiholog/Entity/` | `src/Psiholog/Service/` |
| Landing | `src/Landing/Controller/` | `src/Landing/Entity/` | `src/Landing/Service/` |
| User | `src/User/Controller/` | `src/User/Entity/` | `src/User/Service/` |
| Staff | `src/Staff/Controller/` | `src/Staff/Entity/` | `src/Staff/Service/` |
| Billing | `src/Billing/Controller/` | `src/Billing/Entity/` | `src/Billing/Service/` |
| Notification | `src/Notification/Controller/` | N/A | `src/Notification/Service/` |
| Common | N/A | `src/Common/Entity/` | `src/Common/Service/` |

**Templates Location:** `src/{Module}/Resources/templates/`  
**Styles Location:** `original_php_project/assets/landing/styles-new/`  
**Translations:** `original_php_project/resources/translations/`

---

## Important Notes

1. **Order of Implementation:** Therapist → Landing → User → Staff → Billing → Notification
2. **Test After Each Sub-Stage:** Ensure functionality works before proceeding
3. **Commit Frequently:** Make logical commits after each completed sub-stage
4. **UI Fidelity:** The migrated UI must match the original exactly
5. **No Feature Skipping:** All features must be migrated, no exceptions

