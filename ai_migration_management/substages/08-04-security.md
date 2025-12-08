# Sub-Stage 8.4: Security Hardening

## Goal
Review and strengthen application security.

---

## Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // ... existing config ...
            
            // Security headers
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; " +
                                     "style-src 'self' 'unsafe-inline'; img-src 'self' data:"))
                .frameOptions(frame -> frame.deny())
                .xssProtection(xss -> xss.block(true))
                .contentTypeOptions(Customizer.withDefaults())
                .referrerPolicy(referrer -> referrer.policy(ReferrerPolicy.SAME_ORIGIN))
            )
            
            // Session management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(3)
                .maxSessionsPreventsLogin(false)
            )
            
            .build();
    }
}
```

---

## Input Validation

### Bean Validation
```java
public class BookingForm {
    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    @Size(max = 255)
    private String email;
    
    @Size(max = 100)
    @Pattern(regexp = "^[\\p{L}\\s'-]+$", message = "{validation.name.invalid}")
    private String name;
    
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "{validation.phone.invalid}")
    private String phone;
}
```

### Sanitization
```java
@Component
public class InputSanitizer {
    
    public String sanitizeHtml(String input) {
        if (input == null) return null;
        return Jsoup.clean(input, Safelist.basic());
    }
    
    public String sanitizePlainText(String input) {
        if (input == null) return null;
        return input.replaceAll("[<>\"']", "");
    }
}
```

---

## Rate Limiting

```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final RateLimiter rateLimiter;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, ...) {
        String clientId = getClientIdentifier(request);
        
        if (!rateLimiter.tryAcquire(clientId)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getClientIdentifier(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        return xForwardedFor != null ? xForwardedFor.split(",")[0] : request.getRemoteAddr();
    }
}
```

---

## Security Checklist

| Area | Status | Notes |
|------|--------|-------|
| CSRF protection | ✓ | Enabled by default |
| XSS prevention | ✓ | Thymeleaf escaping + CSP |
| SQL injection | ✓ | JPA parameterized queries |
| Session security | ✓ | HttpOnly, Secure cookies |
| Password storage | N/A | No passwords (auto-login) |
| Webhook signatures | ✓ | WayForPay/Telegram verified |
| Admin route protection | ✓ | Role-based access |
| Input validation | ✓ | Bean Validation |
| Error messages | ✓ | No sensitive info leaked |
| Dependencies | Check | Run dependency audit |

---

## Dependency Audit

```bash
# Check for vulnerable dependencies
./mvnw dependency-check:check
```

---

## Verification
- [ ] Security headers present
- [ ] CSRF enabled on forms
- [ ] Rate limiting works
- [ ] Input validation complete
- [ ] No sensitive data in errors
- [ ] Dependency audit clean

---

## Next
Proceed to **8.5: Documentation**

