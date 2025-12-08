# Sub-Stage 1.7: Common Module

## Goal
Create shared utilities, base entities, and exception handling infrastructure used across all modules.

---

## Files to Create

### 1. BaseEntity.java

**Path:** `src/main/java/com/goodhelp/common/entity/BaseEntity.java`

```java
package com.goodhelp.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Base entity with audit fields.
 * All entities should extend this for consistent timestamp handling.
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

---

### 2. Exception Classes

#### GlobalExceptionHandler.java

**Path:** `src/main/java/com/goodhelp/common/exception/GlobalExceptionHandler.java`

```java
package com.goodhelp.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Global exception handler for the application.
 * Provides consistent error handling across all controllers.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        log.warn("Resource not found: {}", ex.getMessage());
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFound(NoHandlerFoundException ex, Model model) {
        log.warn("No handler found: {}", ex.getRequestURL());
        return "error/404";
    }
    
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBusinessException(BusinessException ex, Model model) {
        log.warn("Business exception: {}", ex.getMessage());
        model.addAttribute("message", ex.getMessage());
        return "error/400";
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Unexpected error", ex);
        model.addAttribute("message", "An unexpected error occurred");
        return "error/500";
    }
}
```

#### ResourceNotFoundException.java

**Path:** `src/main/java/com/goodhelp/common/exception/ResourceNotFoundException.java`

```java
package com.goodhelp.common.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resourceType, Long id) {
        super(String.format("%s not found with id: %d", resourceType, id));
    }
    
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s not found: %s", resourceType, identifier));
    }
}
```

#### BusinessException.java

**Path:** `src/main/java/com/goodhelp/common/exception/BusinessException.java`

```java
package com.goodhelp.common.exception;

/**
 * Exception thrown for business rule violations.
 */
public class BusinessException extends RuntimeException {
    
    private final String code;
    
    public BusinessException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
    }
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
}
```

---

### 3. Utility Classes

#### TimezoneHelper.java

**Path:** `src/main/java/com/goodhelp/common/service/TimezoneHelper.java`

```java
package com.goodhelp.common.service;

import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Helper service for timezone operations.
 * Migrated from PHP: App\Common\Service\TimezoneHelper
 */
@Service
public class TimezoneHelper {
    
    public static final String DEFAULT_TIMEZONE = "Europe/Kiev";
    
    /**
     * Get a human-readable label for a timezone offset.
     * 
     * @param offsetSeconds Offset in seconds from UTC
     * @return Label like "UTC+2" or "UTC-5"
     */
    public String getLabelForOffset(int offsetSeconds) {
        return getLabelForOffset(offsetSeconds, null);
    }
    
    /**
     * Get a human-readable label for a timezone offset.
     * 
     * @param offsetSeconds Offset in seconds from UTC
     * @param timezoneName Optional timezone name to append
     * @return Label like "UTC+2 (Europe/Kiev)"
     */
    public String getLabelForOffset(int offsetSeconds, String timezoneName) {
        int hours = offsetSeconds / 3600;
        int minutes = Math.abs((offsetSeconds % 3600) / 60);
        
        StringBuilder label = new StringBuilder("UTC");
        if (hours >= 0) {
            label.append("+");
        }
        label.append(hours);
        
        if (minutes > 0) {
            label.append(":").append(String.format("%02d", minutes));
        }
        
        if (timezoneName != null && !timezoneName.isEmpty()) {
            label.append(" (").append(timezoneName).append(")");
        }
        
        return label.toString();
    }
    
    /**
     * Get the current offset for a timezone.
     * 
     * @param timezone Timezone identifier (e.g., "Europe/Kiev")
     * @return Offset in seconds
     */
    public int getOffsetForTimezone(String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        ZoneOffset offset = ZonedDateTime.now(zoneId).getOffset();
        return offset.getTotalSeconds();
    }
    
    /**
     * Check if a timezone identifier is valid.
     * 
     * @param timezone Timezone identifier
     * @return true if valid
     */
    public boolean isValidTimezone(String timezone) {
        try {
            ZoneId.of(timezone);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

#### DateLocalizedHelper.java

**Path:** `src/main/java/com/goodhelp/common/service/DateLocalizedHelper.java`

```java
package com.goodhelp.common.service;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Helper service for localized date formatting.
 * Migrated from PHP: App\Common\Service\DateLocalizedHelper
 */
@Service
public class DateLocalizedHelper {
    
    private final MessageSource messageSource;
    
    // Week day keys for translation
    private static final String[] WEEKDAY_KEYS = {
        "", // 0 - not used (days start from 1)
        "weekday.monday",
        "weekday.tuesday",
        "weekday.wednesday",
        "weekday.thursday",
        "weekday.friday",
        "weekday.saturday",
        "weekday.sunday"
    };
    
    private static final String[] WEEKDAY_SHORT_KEYS = {
        "",
        "weekday.short.monday",
        "weekday.short.tuesday",
        "weekday.short.wednesday",
        "weekday.short.thursday",
        "weekday.short.friday",
        "weekday.short.saturday",
        "weekday.short.sunday"
    };
    
    private static final String[] MONTH_KEYS = {
        "", // 0 - not used
        "month.january",
        "month.february",
        "month.march",
        "month.april",
        "month.may",
        "month.june",
        "month.july",
        "month.august",
        "month.september",
        "month.october",
        "month.november",
        "month.december"
    };
    
    private static final String[] MONTH_INCLINED_KEYS = {
        "",
        "month.inclined.january",
        "month.inclined.february",
        "month.inclined.march",
        "month.inclined.april",
        "month.inclined.may",
        "month.inclined.june",
        "month.inclined.july",
        "month.inclined.august",
        "month.inclined.september",
        "month.inclined.october",
        "month.inclined.november",
        "month.inclined.december"
    };
    
    public DateLocalizedHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    public String getWeekDayNameByNumber(int dayOfWeek, String localeCode) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("Day of week must be between 1 and 7");
        }
        return messageSource.getMessage(WEEKDAY_KEYS[dayOfWeek], null, getLocale(localeCode));
    }
    
    public String getWeekDayShortNameByNumber(int dayOfWeek, String localeCode) {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("Day of week must be between 1 and 7");
        }
        return messageSource.getMessage(WEEKDAY_SHORT_KEYS[dayOfWeek], null, getLocale(localeCode));
    }
    
    public String getMonthNameByNumber(int month, String localeCode) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        return messageSource.getMessage(MONTH_KEYS[month], null, getLocale(localeCode));
    }
    
    public String getMonthNameByNumberInclined(int month, String localeCode) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        return messageSource.getMessage(MONTH_INCLINED_KEYS[month], null, getLocale(localeCode));
    }
    
    private Locale getLocale(String localeCode) {
        if (localeCode == null || localeCode.isEmpty()) {
            return Locale.ENGLISH;
        }
        // Map "ua" to "uk" (Ukrainian language code)
        if ("ua".equalsIgnoreCase(localeCode)) {
            localeCode = "uk";
        }
        return Locale.forLanguageTag(localeCode);
    }
}
```

#### PasswordGenerator.java

**Path:** `src/main/java/com/goodhelp/common/service/PasswordGenerator.java`

```java
package com.goodhelp.common.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Service for generating random tokens and passwords.
 * Migrated from PHP: App\Common\Service\PasswordGenerator
 */
@Service
public class PasswordGenerator {
    
    private static final String ALPHANUMERIC = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Generate a random alphanumeric token.
     * 
     * @param length Length of the token
     * @return Random token string
     */
    public String generateToken(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }
    
    /**
     * Generate a 32-character token (used for autologin tokens).
     * 
     * @return 32-character random token
     */
    public String generateAutologinToken() {
        return generateToken(32);
    }
}
```

#### ArrayIndexer.java

**Path:** `src/main/java/com/goodhelp/common/util/ArrayIndexer.java`

```java
package com.goodhelp.common.util;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility for indexing collections by a key.
 * Migrated from PHP: App\Common\Service\ArrayIndexer
 */
@Component
public class ArrayIndexer {
    
    /**
     * Index a list by a key, returning a map with unique keys.
     * If duplicate keys exist, the last value wins.
     * 
     * @param list List to index
     * @param keyExtractor Function to extract the key from each element
     * @return Map indexed by the extracted key
     */
    public <K, V> Map<K, V> byKeyUnique(List<V> list, Function<V, K> keyExtractor) {
        return list.stream()
            .collect(Collectors.toMap(
                keyExtractor,
                Function.identity(),
                (existing, replacement) -> replacement,
                LinkedHashMap::new
            ));
    }
    
    /**
     * Index a list of maps by a string key.
     * 
     * @param list List of maps
     * @param keyName Name of the key field
     * @return Map indexed by the key value
     */
    @SuppressWarnings("unchecked")
    public <V> Map<Object, Map<String, V>> byKeyUnique(List<Map<String, V>> list, String keyName) {
        Map<Object, Map<String, V>> result = new LinkedHashMap<>();
        for (Map<String, V> item : list) {
            Object key = item.get(keyName);
            if (key != null) {
                result.put(key, item);
            }
        }
        return result;
    }
    
    /**
     * Group a list by a key, returning a map with lists as values.
     * 
     * @param list List to group
     * @param keyExtractor Function to extract the key from each element
     * @return Map with lists grouped by key
     */
    public <K, V> Map<K, List<V>> byKey(List<V> list, Function<V, K> keyExtractor) {
        return list.stream()
            .collect(Collectors.groupingBy(
                keyExtractor,
                LinkedHashMap::new,
                Collectors.toList()
            ));
    }
    
    /**
     * Group a list of maps by a string key.
     * 
     * @param list List of maps
     * @param keyName Name of the key field
     * @return Map with lists grouped by key
     */
    @SuppressWarnings("unchecked")
    public <V> Map<Object, List<Map<String, V>>> byKey(List<Map<String, V>> list, String keyName) {
        Map<Object, List<Map<String, V>>> result = new LinkedHashMap<>();
        for (Map<String, V> item : list) {
            Object key = item.get(keyName);
            if (key != null) {
                result.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
            }
        }
        return result;
    }
}
```

---

### 4. Error Templates

#### error/404.html

**Path:** `src/main/resources/templates/error/404.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Page Not Found - GoodHelp</title>
    <link rel="stylesheet" th:href="@{/css/landing/main.css}" />
</head>
<body>
<div class="error-page">
    <h1>404</h1>
    <h2 th:text="#{error.not_found}">Page Not Found</h2>
    <p th:text="${message}" th:if="${message}">The requested page could not be found.</p>
    <a th:href="@{/}" class="gh-button" th:text="#{error.go_home}">Go to Home</a>
</div>
</body>
</html>
```

#### error/500.html

**Path:** `src/main/resources/templates/error/500.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Server Error - GoodHelp</title>
    <link rel="stylesheet" th:href="@{/css/landing/main.css}" />
</head>
<body>
<div class="error-page">
    <h1>500</h1>
    <h2 th:text="#{error.server_error}">Server Error</h2>
    <p th:text="#{error.server_error_message}">An unexpected error occurred. Please try again later.</p>
    <a th:href="@{/}" class="gh-button" th:text="#{error.go_home}">Go to Home</a>
</div>
</body>
</html>
```

---

## Directory Structure

```
src/main/java/com/goodhelp/common/
├── entity/
│   └── BaseEntity.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── BusinessException.java
├── service/
│   ├── TimezoneHelper.java
│   ├── DateLocalizedHelper.java
│   └── PasswordGenerator.java
└── util/
    └── ArrayIndexer.java

src/main/resources/templates/error/
├── 404.html
└── 500.html
```

---

## Verification

- [ ] All classes compile without errors
- [ ] Unit tests pass for utility classes
- [ ] Error pages render correctly

---

## Next Sub-Stage
Proceed to **1.8: Database Schema**

