# Sub-Stage 2.5: Therapist Security

## Goal
Implement therapist-specific authentication with auto-login token support.

---

## Security Components

**Location:** `com.goodhelp.therapist.infrastructure.security/`

### TherapistUserDetails
```java
public class TherapistUserDetails implements UserDetails {
    private final Long id;
    private final String email;
    private final TherapistRole role;
    private final boolean active;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @Override
    public String getUsername() { return email; }
    
    @Override
    public boolean isEnabled() { return active; }
    
    // Other UserDetails methods...
}
```

### TherapistUserDetailsService
```java
@Service
public class TherapistUserDetailsService implements UserDetailsService {
    private final TherapistRepository therapistRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) {
        return therapistRepository.findByEmail(new Email(email))
            .map(TherapistUserDetails::from)
            .orElseThrow(() -> new UsernameNotFoundException("Therapist not found"));
    }
}
```

### AutoLoginTokenAuthenticationFilter
```java
public class TherapistAutoLoginFilter extends OncePerRequestFilter {
    private static final String TOKEN_PARAM = "t";
    private static final String AUTO_LOGIN_PATH = "/therapist/auto-login";
    
    private final AuthTokenRepository tokenRepository;
    private final TherapistRepository therapistRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, ...) {
        if (isAutoLoginRequest(request)) {
            String token = request.getParameter(TOKEN_PARAM);
            authenticateWithToken(token);
        }
        filterChain.doFilter(request, response);
    }
    
    private void authenticateWithToken(String token) {
        tokenRepository.findByToken(token)
            .map(AuthToken::getTherapistId)
            .flatMap(therapistRepository::findById)
            .ifPresent(this::setAuthentication);
    }
}
```

---

## Security Configuration

**In:** `com.goodhelp.config.SecurityConfig`

```java
@Bean
@Order(2) // After main security
public SecurityFilterChain therapistSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
        .securityMatcher("/therapist/**")
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/therapist/login", "/therapist/auto-login").permitAll()
            .requestMatchers("/therapist/**").hasAnyRole("PSIHOLOG", "TEST_PSIHOLOG")
        )
        .formLogin(form -> form
            .loginPage("/therapist/login")
            .loginProcessingUrl("/therapist/login")
            .defaultSuccessUrl("/therapist/schedule")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/therapist/logout")
            .logoutSuccessUrl("/therapist/login")
        )
        .addFilterBefore(therapistAutoLoginFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
```

---

## Authentication Flow

```
1. User clicks auto-login link: /therapist/auto-login?t={token}
2. TherapistAutoLoginFilter intercepts
3. Token validated against psiholog_autologin_token table
4. Therapist loaded from database
5. TherapistUserDetails created
6. SecurityContext populated
7. Session established
8. Redirect to /therapist/schedule
```

---

## Roles

| Role | DB Value | Description |
|------|----------|-------------|
| `ROLE_PSIHOLOG` | 1 | Regular psychologist |
| `ROLE_TEST_PSIHOLOG` | 2 | Test account |

---

## Verification
- [x] Auto-login with valid token works
- [x] Invalid token shows error
- [x] Protected routes require authentication
- [x] Logout clears session
- [x] @AuthenticationPrincipal injects TherapistUserDetails

## Implementation Notes

### Created Files (`com.goodhelp.therapist.infrastructure.security`):

1. **TherapistUserDetails.java** - Spring Security UserDetails implementation
   - Maps TherapistRole to Spring Security authorities (ROLE_PSIHOLOG, ROLE_TEST_PSIHOLOG)
   - Provides user state information (enabled, locked, etc.)

2. **TherapistUserDetailsService.java** - UserDetailsService implementation
   - Loads therapist by email for authentication
   - Also supports loading by ID for session-based auth

3. **TherapistAutoLoginFilter.java** - Custom authentication filter
   - Handles `/therapist/auto-login?t={token}` requests
   - Validates token and establishes Spring Security session
   - Redirects to schedule on success, login page on failure

4. **CurrentTherapist.java** - Convenience annotation
   - Wraps @AuthenticationPrincipal for cleaner controller code
   - Usage: `@CurrentTherapist TherapistUserDetails therapist`

### Updated Files:

- **SecurityConfig.java** - Added auto-login filter integration
- **TherapistStatus.java** - Added INACTIVE, SUSPENDED, PENDING_APPROVAL statuses

## Status: COMPLETED ✅

---

## Next
Proceed to **2.6: Auth Controllers (Login/Logout)**

