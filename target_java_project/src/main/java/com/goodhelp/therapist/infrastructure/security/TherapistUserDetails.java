package com.goodhelp.therapist.infrastructure.security;

import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistRole;
import com.goodhelp.therapist.domain.model.TherapistStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Spring Security UserDetails implementation for therapists.
 * 
 * Wraps a Therapist entity and provides the necessary methods
 * for Spring Security authentication and authorization.
 * 
 * <p>Role mapping:</p>
 * <ul>
 *   <li>THERAPIST → ROLE_PSIHOLOG</li>
 *   <li>TEST_THERAPIST → ROLE_TEST_PSIHOLOG</li>
 * </ul>
 */
@Getter
public class TherapistUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final TherapistRole role;
    private final TherapistStatus status;
    private final String fullName;
    private final String timezone;

    private TherapistUserDetails(Long id, String email, TherapistRole role, 
                                  TherapistStatus status, String fullName, String timezone) {
        this.id = Objects.requireNonNull(id, "Therapist ID is required");
        this.email = Objects.requireNonNull(email, "Email is required");
        this.role = Objects.requireNonNull(role, "Role is required");
        this.status = Objects.requireNonNull(status, "Status is required");
        this.fullName = fullName != null ? fullName : "Unknown";
        this.timezone = timezone != null ? timezone : "Europe/Kiev";
    }

    /**
     * Create UserDetails from a Therapist entity.
     */
    public static TherapistUserDetails from(Therapist therapist) {
        return new TherapistUserDetails(
            therapist.getId(),
            therapist.getEmail(),
            therapist.getRole(),
            therapist.getStatus(),
            therapist.getFullName(),
            therapist.getTimezone()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Map domain role to Spring Security role
        String securityRole = switch (role) {
            case THERAPIST -> "ROLE_PSIHOLOG";
            case TEST_THERAPIST -> "ROLE_TEST_PSIHOLOG";
        };
        return List.of(new SimpleGrantedAuthority(securityRole));
    }

    @Override
    public String getPassword() {
        // Therapists use token-based authentication, no password
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
        return status != TherapistStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == TherapistStatus.ACTIVE;
    }

    /**
     * Check if this is a real therapist (not test account).
     */
    public boolean isRealTherapist() {
        return role.isRealTherapist();
    }

    /**
     * Check if the therapist can accept consultations.
     */
    public boolean canAcceptConsultations() {
        return isEnabled() && isAccountNonLocked();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TherapistUserDetails that = (TherapistUserDetails) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("TherapistUserDetails[id=%d, email=%s, role=%s]", id, email, role);
    }
}

