# Sub-Stage 5.3: Staff Security

## Goal
Implement staff authentication and authorization.

---

## Security Components

### StaffUserDetails
```java
public class StaffUserDetails implements UserDetails {
    private final Long id;
    private final String email;
    private final StaffRole role;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_STAFF_USER"));
        if (role == StaffRole.SUPERUSER) {
            authorities.add(new SimpleGrantedAuthority("ROLE_STAFF_SUPERUSER"));
        }
        return authorities;
    }
}
```

### StaffUserDetailsService
```java
@Service
public class StaffUserDetailsService implements UserDetailsService {
    private final StaffUserRepository staffRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) {
        return staffRepository.findByEmail(email)
            .map(StaffUserDetails::from)
            .orElseThrow(() -> new UsernameNotFoundException("Staff user not found"));
    }
}
```

---

## Security Configuration

```java
@Bean
@Order(1) // Highest priority
public SecurityFilterChain staffSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
        .securityMatcher("/staff/**")
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/staff/login", "/staff/auto-login").permitAll()
            .requestMatchers("/staff/therapists/add", "/staff/payouts/**")
                .hasRole("STAFF_SUPERUSER")
            .requestMatchers("/staff/**").hasRole("STAFF_USER")
        )
        .formLogin(form -> form
            .loginPage("/staff/login")
            .defaultSuccessUrl("/staff/")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/staff/logout")
            .logoutSuccessUrl("/staff/login")
        )
        .addFilterBefore(staffAutoLoginFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
```

---

## Role Hierarchy

```
ROLE_STAFF_SUPERUSER
        ↓
ROLE_STAFF_USER
```

Superuser can:
- Add new therapists
- Manage payouts
- All regular staff actions

Regular staff can:
- View/edit therapists
- Manage blog

---

## Verification
- [ ] Auto-login works
- [ ] Role hierarchy enforced
- [ ] Superuser-only routes protected

---

## Next
Proceed to **5.4: Login/Logout**

