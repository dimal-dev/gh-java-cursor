# Sub-Stage 1.4: Configuration Files

## Goal
Create application configuration files for different environments.

---

## Files to Create

### 1. application.yml (Common Configuration)

**Path:** `src/main/resources/application.yml`

```yaml
spring:
  application:
    name: goodhelp
  
  profiles:
    active: dev
  
  # JPA / Hibernate
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        jdbc:
          time_zone: UTC
    open-in-view: false
  
  # Thymeleaf
  thymeleaf:
    cache: false
    mode: HTML
    encoding: UTF-8
    prefix: classpath:/templates/
    suffix: .html
  
  # Messages / i18n
  messages:
    basename: messages
    encoding: UTF-8
    fallback-to-system-locale: false
  
  # Session
  session:
    store-type: redis
    timeout: 48h
    redis:
      namespace: goodhelp:session
  
  # Servlet
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

# Server configuration
server:
  port: 8080
  servlet:
    context-path: /
    session:
      cookie:
        same-site: lax
        http-only: true

# Logging
logging:
  level:
    root: INFO
    com.goodhelp: DEBUG
    org.springframework.web: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql: TRACE

# Custom application properties
goodhelp:
  default-locale: en
  supported-locales: en,uk,ru
  default-timezone: Europe/Kiev
```

---

### 2. application-dev.yml (Development)

**Path:** `src/main/resources/application-dev.yml`

```yaml
spring:
  # Database - local Docker
  datasource:
    url: jdbc:postgresql://localhost:5432/goodhelp
    username: goodhelp
    password: goodhelp_password
    driver-class-name: org.postgresql.Driver
    hikari:
      connection-timeout: 20000
      maximum-pool-size: 10
  
  # JPA
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
  
  # Redis - local Docker
  data:
    redis:
      host: localhost
      port: 6379
  
  # Kafka - local Docker
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: goodhelp-dev
      auto-offset-reset: earliest
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
  
  # DevTools
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true
  
  # Thymeleaf - no cache for dev
  thymeleaf:
    cache: false

# Logging - more verbose for dev
logging:
  level:
    com.goodhelp: DEBUG
    org.springframework.security: DEBUG

# Dev-specific settings
goodhelp:
  # WayForPay test credentials
  wayforpay:
    merchant-login: ${WAYFORPAY_MERCHANT_LOGIN:test_merchant}
    merchant-secret-key: ${WAYFORPAY_SECRET_KEY:test_secret}
    merchant-domain: localhost:8080
  
  # Telegram test settings
  telegram:
    bot-token: ${TELEGRAM_BOT_TOKEN:}
    enabled: false
```

---

### 3. application-prod.yml (Production)

**Path:** `src/main/resources/application-prod.yml`

```yaml
spring:
  # Database - from environment variables
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      connection-timeout: 20000
      maximum-pool-size: 20
      minimum-idle: 5
  
  # JPA
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        generate_statistics: false
  
  # Redis
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
  
  # Kafka
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS}
    consumer:
      group-id: goodhelp-prod
    properties:
      security.protocol: ${KAFKA_SECURITY_PROTOCOL:PLAINTEXT}
  
  # Thymeleaf - cache enabled
  thymeleaf:
    cache: true

# Logging - less verbose for prod
logging:
  level:
    root: WARN
    com.goodhelp: INFO
    org.springframework: WARN

# Server
server:
  servlet:
    session:
      cookie:
        secure: true

# Production settings
goodhelp:
  wayforpay:
    merchant-login: ${WAYFORPAY_MERCHANT_LOGIN}
    merchant-secret-key: ${WAYFORPAY_SECRET_KEY}
    merchant-domain: goodhelp.com.ua
  
  telegram:
    bot-token: ${TELEGRAM_BOT_TOKEN}
    enabled: true
```

---

### 4. application-test.yml (Testing)

**Path:** `src/main/resources/application-test.yml`

```yaml
spring:
  # H2 in-memory database for tests
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  
  # JPA
  jpa:
    hibernate:
      ddl-auto: create-drop
    database-platform: org.hibernate.dialect.H2Dialect
    show-sql: false
  
  # Disable Redis for tests
  data:
    redis:
      repositories:
        enabled: false
  
  # Disable Kafka for tests
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      auto-startup: false
  
  # Session
  session:
    store-type: none

# Logging
logging:
  level:
    root: WARN
    com.goodhelp: INFO

# Test settings
goodhelp:
  wayforpay:
    merchant-login: test_merchant
    merchant-secret-key: test_secret
    merchant-domain: localhost
  
  telegram:
    enabled: false
```

---

## Custom Configuration Properties Class

**Path:** `src/main/java/com/goodhelp/config/GoodHelpProperties.java`

```java
package com.goodhelp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "goodhelp")
public class GoodHelpProperties {
    
    private String defaultLocale = "en";
    private List<String> supportedLocales = List.of("en", "uk", "ru");
    private String defaultTimezone = "Europe/Kiev";
    
    private WayForPay wayforpay = new WayForPay();
    private Telegram telegram = new Telegram();
    
    @Data
    public static class WayForPay {
        private String merchantLogin;
        private String merchantSecretKey;
        private String merchantDomain;
    }
    
    @Data
    public static class Telegram {
        private String botToken;
        private boolean enabled = false;
    }
}
```

---

## Verification

1. **Compile the project:**
   ```bash
   ./mvnw clean compile
   ```

2. **Start Docker services:**
   ```bash
   cd docker && docker-compose up -d
   ```

3. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Check application started:**
   - Application should start on port 8080
   - Check logs for any errors

---

## Checklist

- [ ] application.yml created
- [ ] application-dev.yml created
- [ ] application-prod.yml created
- [ ] application-test.yml created
- [ ] GoodHelpProperties class created
- [ ] Application starts without errors

---

## Next Sub-Stage
Proceed to **1.5: Security Configuration Skeleton**

