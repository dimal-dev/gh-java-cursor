# Sub-Stage 4.3: User Security

## Goal
Implement user-specific authentication with auto-login token support.

---

## Security Components

**Location:** `com.goodhelp.user.infrastructure.security/`

### UserDetails Implementation
```java
public class GoodHelpUserDetails implements UserDetails {
    private final Long id;
    private final String email;
    private final String fullName;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
    
    @Override
    public String getUsername() { return email; }
    
    @Override
    public boolean isEnabled() { return true; }
    
    // Static factory from User entity
    public static GoodHelpUserDetails from(User user) {
        return new GoodHelpUserDetails(user.getId(), user.getEmail(), user.getFullName());
    }
}
```

### UserDetailsService
```java
@Service
public class GoodHelpUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
            .map(GoodHelpUserDetails::from)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
```

### Auto-Login Filter
```java
public class UserAutoLoginFilter extends OncePerRequestFilter {
    private static final String AUTO_LOGIN_PATH = "/user/auto-login";
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, ...) {
        if (isAutoLoginRequest(request)) {
            String token = request.getParameter("t");
            authenticateWithToken(token);
        }
        filterChain.doFilter(request, response);
    }
}
```

---

## Security Configuration

**In:** `com.goodhelp.config.SecurityConfig`

```java
@Bean
@Order(3)
public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
        .securityMatcher("/user/**")
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/user/login", "/user/auto-login").permitAll()
            .requestMatchers("/user/**").hasRole("USER")
        )
        .formLogin(form -> form
            .loginPage("/user/login")
            .defaultSuccessUrl("/user/")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/user/logout")
            .logoutSuccessUrl("/user/login")
        )
        .addFilterBefore(userAutoLoginFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
```

---

## Authentication Flow

```
1. User enters email on login page
2. System sends auto-login email with token
3. User clicks link: /user/auto-login?t={token}
4. UserAutoLoginFilter validates token
5. User entity loaded, GoodHelpUserDetails created
6. Session established
7. Redirect to /user/ (dashboard)
```

---

## Verification
- [ ] Auto-login works with valid token
- [ ] Invalid token shows error
- [ ] Protected routes require auth
- [ ] @AuthenticationPrincipal injects GoodHelpUserDetails

---

## Next
Proceed to **4.4: Login/Logout**

