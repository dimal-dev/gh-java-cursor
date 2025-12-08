# Sub-Stage 1.5: Security Configuration Skeleton

## Goal
Create the security configuration structure that defines the multi-firewall setup for different user types.

---

## Files to Create

### 1. SecurityConfig.java

**Path:** `src/main/java/com/goodhelp/config/SecurityConfig.java`

```java
package com.goodhelp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Security configuration for GoodHelp application.
 * 
 * Defines multiple security filter chains for different areas:
 * - /therapist/** - Therapist authentication
 * - /staff/** - Staff/Admin authentication
 * - /user/** - User authentication
 * - /billing/webhook - Webhook signature verification
 * - /** - Public access
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * Security filter chain for therapist cabinet (/therapist/**)
     */
    @Bean
    @Order(1)
    public SecurityFilterChain psihologSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/therapist/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/therapist/login", "/therapist/auto-login").permitAll()
                .anyRequest().hasAnyRole("PSIHOLOG", "TEST_PSIHOLOG")
            )
            .formLogin(form -> form
                .loginPage("/therapist/login")
                .loginProcessingUrl("/therapist/login")
                .defaultSuccessUrl("/therapist/schedule", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/therapist/logout"))
                .logoutSuccessUrl("/therapist/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/therapist/api/**")
            );
        
        return http.build();
    }
    
    /**
     * Security filter chain for staff cabinet (/staff/**)
     */
    @Bean
    @Order(2)
    public SecurityFilterChain staffSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/staff/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/staff/login", "/staff/auto-login").permitAll()
                .anyRequest().hasRole("STAFF_USER")
            )
            .formLogin(form -> form
                .loginPage("/staff/login")
                .loginProcessingUrl("/staff/login")
                .defaultSuccessUrl("/staff/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/staff/logout"))
                .logoutSuccessUrl("/staff/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );
        
        return http.build();
    }
    
    /**
     * Security filter chain for user cabinet (/user/**)
     */
    @Bean
    @Order(3)
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/user/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/user/login", "/user/auto-login").permitAll()
                .anyRequest().hasRole("USER")
            )
            .formLogin(form -> form
                .loginPage("/user/login")
                .loginProcessingUrl("/user/login")
                .defaultSuccessUrl("/user/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .logoutSuccessUrl("/user/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .rememberMe(remember -> remember
                .key("goodhelp-user-remember-me")
                .tokenValiditySeconds(31104000) // ~1 year
            );
        
        return http.build();
    }
    
    /**
     * Security filter chain for billing webhooks
     * Disables CSRF for webhook endpoints
     */
    @Bean
    @Order(4)
    public SecurityFilterChain billingSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/billing/**")
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }
    
    /**
     * Security filter chain for notification webhooks
     */
    @Bean
    @Order(5)
    public SecurityFilterChain notificationSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/notification/**")
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }
    
    /**
     * Default security filter chain for public pages
     */
    @Bean
    @Order(100)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/en/**",
                    "/ru/**",
                    "/uk/**",
                    "/therapist-list",
                    "/therapist/**",
                    "/book-consultation/**",
                    "/checkout/**",
                    "/blog/**",
                    "/about",
                    "/prices",
                    "/privacy",
                    "/terms-of-use",
                    "/refund-policy",
                    "/consultation-conditions",
                    "/request-therapist/**",
                    "/psiholog-apply",
                    "/family-psiholog",
                    "/teenage-psiholog",
                    "/api/timezone",
                    "/static/**",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            );
        
        return http.build();
    }
}
```

---

### 2. Role Hierarchy Bean (Optional Enhancement)

Add to SecurityConfig.java:

```java
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Bean
public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
    hierarchy.setHierarchy("ROLE_STAFF_SUPERUSER > ROLE_STAFF_USER");
    return hierarchy;
}
```

---

## Security Architecture Notes

### Authentication Flow (Auto-Login Token)

The actual auto-login authentication will be implemented in each module's security package. The flow is:

1. User requests `/therapist/auto-login?t={token}`
2. Custom filter intercepts the request
3. Token is validated against database
4. User is authenticated and redirected

### Roles Summary

| Area | Roles |
|------|-------|
| Psychologist | `ROLE_PSIHOLOG`, `ROLE_TEST_PSIHOLOG` |
| User | `ROLE_USER` |
| Staff | `ROLE_STAFF_USER`, `ROLE_STAFF_SUPERUSER` |

### CSRF Notes

- Disabled for `/billing/**` - webhook endpoints
- Disabled for `/notification/**` - Telegram webhooks
- Disabled for `/api/**` endpoints - AJAX calls
- Enabled everywhere else

---

## Verification

1. **Compile:**
   ```bash
   ./mvnw clean compile
   ```

2. **Start application:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Test security:**
   - Access `/` - should work (public)
   - Access `/therapist/schedule` - should redirect to login
   - Access `/user/` - should redirect to login
   - Access `/staff/` - should redirect to login

---

## Checklist

- [ ] SecurityConfig.java created
- [ ] All security filter chains defined
- [ ] CSRF properly configured
- [ ] Login/logout URLs configured
- [ ] Public paths accessible
- [ ] Protected paths redirect to login

---

## Next Sub-Stage
Proceed to **1.6: Thymeleaf & i18n Setup**

