package com.goodhelp.user.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.Objects;

/**
 * Aggregate Root for the User bounded context.
 * 
 * <p>A User represents a client who books consultations with therapists.
 * This entity encapsulates:</p>
 * <ul>
 *   <li>Identity (email)</li>
 *   <li>Profile information (full name, timezone, locale)</li>
 *   <li>Authentication tokens</li>
 * </ul>
 * 
 * <p>Business rules enforced by this aggregate:</p>
 * <ul>
 *   <li>Email is required and must be unique</li>
 *   <li>Full name can be set by user or auto-generated</li>
 *   <li>Timezone defaults to Europe/Kiev</li>
 *   <li>Locale defaults to 'ua' (Ukrainian)</li>
 * </ul>
 */
@Entity
@Table(name = "\"user\"", indexes = {
    @Index(name = "idx_user_email", columnList = "email")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
public class User extends BaseEntity {

    public static final String DEFAULT_LOCALE = "ua";
    public static final String DEFAULT_TIMEZONE = "Europe/Kiev";
    public static final String FAKE_USER_EMAIL_PART = "gh.fake.ue.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, length = 500)
    private String email;

    @Column(name = "full_name", length = 500)
    private String fullName = "";

    @Column(name = "is_full_name_set_by_user", nullable = false)
    private boolean fullNameSetByUser = false;

    @Column(name = "timezone", nullable = false, length = 100)
    private String timezone = DEFAULT_TIMEZONE;

    @Column(name = "locale", nullable = false, length = 10)
    private String locale = DEFAULT_LOCALE;

    @Column(name = "is_email_real", nullable = false)
    private boolean emailReal = true;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserAutologinToken autologinToken;

    /**
     * Private constructor - use factory methods.
     */
    private User(String email, String timezone, String locale) {
        this.email = validateEmail(email);
        this.timezone = Objects.requireNonNull(timezone, "Timezone is required");
        this.locale = Objects.requireNonNull(locale, "Locale is required");
    }

    /**
     * Create a new user with email.
     */
    public static User create(String email) {
        return new User(email, DEFAULT_TIMEZONE, DEFAULT_LOCALE);
    }

    /**
     * Create a new user with email and timezone.
     */
    public static User create(String email, String timezone) {
        return new User(email, timezone, DEFAULT_LOCALE);
    }

    /**
     * Create a new user with email, timezone, and locale.
     */
    public static User create(String email, String timezone, String locale) {
        return new User(email, timezone, locale);
    }

    // ==================== Business Methods ====================

    /**
     * Update user profile information.
     * 
     * @param fullName the user's full name
     * @param timezone the user's timezone
     */
    public void updateProfile(String fullName, String timezone) {
        if (fullName != null && !fullName.isBlank()) {
            this.fullName = fullName.trim();
            this.fullNameSetByUser = true;
        }
        if (timezone != null && !timezone.isBlank()) {
            this.timezone = timezone.trim();
        }
    }

    /**
     * Update only the full name.
     */
    public void updateFullName(String fullName) {
        if (fullName != null && !fullName.isBlank()) {
            this.fullName = fullName.trim();
            this.fullNameSetByUser = true;
        }
    }

    /**
     * Update only the timezone.
     */
    public void updateTimezone(String timezone) {
        if (timezone != null && !timezone.isBlank()) {
            this.timezone = timezone.trim();
        }
    }

    /**
     * Update the locale.
     */
    public void updateLocale(String locale) {
        if (locale != null && !locale.isBlank()) {
            this.locale = locale.trim();
        }
    }

    /**
     * Check if user needs to set their profile (name not set by user).
     */
    public boolean needsProfileSetup() {
        return !fullNameSetByUser || fullName == null || fullName.isBlank();
    }

    /**
     * Check if this is a fake/test user (email contains fake marker).
     */
    public boolean isFakeUser() {
        return email != null && email.contains(FAKE_USER_EMAIL_PART);
    }

    /**
     * Check if email is real (not a placeholder).
     */
    public boolean hasRealEmail() {
        return emailReal;
    }

    /**
     * Mark email as fake/placeholder.
     */
    public void markEmailAsFake() {
        this.emailReal = false;
    }

    /**
     * Mark email as real.
     */
    public void markEmailAsReal() {
        this.emailReal = true;
    }

    // ==================== Validation ====================

    private String validateEmail(String email) {
        Objects.requireNonNull(email, "Email is required");
        email = email.trim().toLowerCase();
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
        if (email.length() > 500) {
            throw new IllegalArgumentException("Email cannot exceed 500 characters");
        }
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, email=%s, fullName=%s]", id, email, fullName);
    }
}

