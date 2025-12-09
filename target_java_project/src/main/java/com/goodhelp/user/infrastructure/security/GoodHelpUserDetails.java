package com.goodhelp.user.infrastructure.security;

import com.goodhelp.user.domain.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Spring Security UserDetails implementation for users.
 * 
 * Wraps a User entity and provides the necessary methods
 * for Spring Security authentication and authorization.
 * 
 * <p>Role mapping:</p>
 * <ul>
 *   <li>All users → ROLE_USER</li>
 * </ul>
 */
@Getter
public class GoodHelpUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String fullName;
    private final String timezone;

    private GoodHelpUserDetails(Long id, String email, String fullName, String timezone) {
        this.id = Objects.requireNonNull(id, "User ID is required");
        this.email = Objects.requireNonNull(email, "Email is required");
        this.fullName = fullName != null ? fullName : "";
        this.timezone = timezone != null ? timezone : "Europe/Kiev";
    }

    /**
     * Create UserDetails from a User entity.
     */
    public static GoodHelpUserDetails from(User user) {
        return new GoodHelpUserDetails(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getTimezone()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // Users use token-based authentication, no password
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoodHelpUserDetails that = (GoodHelpUserDetails) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("GoodHelpUserDetails[id=%d, email=%s]", id, email);
    }
}

