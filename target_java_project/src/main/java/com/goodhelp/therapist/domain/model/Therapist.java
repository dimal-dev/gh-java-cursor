package com.goodhelp.therapist.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import com.goodhelp.shared.domain.valueobject.Email;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.util.Objects;
import java.util.Optional;

/**
 * Aggregate Root for the Therapist bounded context.
 * 
 * A Therapist represents a mental health professional who provides
 * consultations through the platform. This entity encapsulates:
 * - Identity (email, role)
 * - Profile information (embedded)
 * - Settings (related entity)
 * - Authentication tokens
 * 
 * <p>Business rules enforced by this aggregate:</p>
 * <ul>
 *   <li>Email must be unique across all therapists</li>
 *   <li>Only active therapists can accept consultations</li>
 *   <li>Profile is required for active therapists</li>
 * </ul>
 */
@Entity
@Table(name = "psiholog", indexes = {
    @Index(name = "idx_psiholog_email", columnList = "email", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
public class Therapist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "role", nullable = false)
    @Convert(converter = TherapistRoleConverter.class)
    private TherapistRole role;

    @Column(name = "state", nullable = false)
    @Convert(converter = TherapistStatusConverter.class)
    private TherapistStatus status;

    @Embedded
    private TherapistProfile profile;

    @OneToOne(mappedBy = "therapist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TherapistSettings settings;

    @OneToOne(mappedBy = "therapist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TherapistAutologinToken autologinToken;

    /**
     * Private constructor - use factory methods.
     */
    private Therapist(String email, TherapistRole role, TherapistProfile profile) {
        this.email = validateEmail(email);
        this.role = Objects.requireNonNull(role, "Role is required");
        this.profile = profile;
        this.status = TherapistStatus.ACTIVE;
    }

    /**
     * Create a new therapist with profile.
     */
    public static Therapist create(String email, TherapistRole role, TherapistProfile profile) {
        return new Therapist(email, role, profile);
    }

    /**
     * Create a new therapist (profile can be set later).
     */
    public static Therapist create(String email, TherapistRole role) {
        return new Therapist(email, role, null);
    }

    // ==================== Business Methods ====================

    /**
     * Check if this therapist can currently accept consultations.
     */
    public boolean canAcceptConsultations() {
        return status.canAcceptConsultations() && profile != null;
    }

    /**
     * Check if this therapist is currently active.
     */
    public boolean isActive() {
        return status == TherapistStatus.ACTIVE;
    }

    /**
     * Check if this is a real therapist (not test account).
     */
    public boolean isRealTherapist() {
        return role.isRealTherapist();
    }

    /**
     * Get the therapist's email as a validated Email value object.
     */
    public Email getEmailAsValueObject() {
        return new Email(email);
    }

    /**
     * Get the therapist's full name from profile.
     * 
     * @return full name or "Unknown" if no profile
     */
    public String getFullName() {
        return profile != null ? profile.getFullName() : "Unknown";
    }

    /**
     * Get profile if present.
     */
    public Optional<TherapistProfile> getProfileOptional() {
        return Optional.ofNullable(profile);
    }

    /**
     * Get settings if present.
     */
    public Optional<TherapistSettings> getSettingsOptional() {
        return Optional.ofNullable(settings);
    }

    /**
     * Get timezone from settings, or default.
     */
    public String getTimezone() {
        return settings != null ? settings.getTimezone() : "Europe/Kiev";
    }

    // ==================== State Modification Methods ====================

    /**
     * Update the therapist's email.
     * 
     * @param newEmail the new email address
     * @throws IllegalArgumentException if email is invalid
     */
    public void updateEmail(String newEmail) {
        this.email = validateEmail(newEmail);
    }

    /**
     * Update the therapist's profile.
     */
    public void updateProfile(TherapistProfile newProfile) {
        this.profile = Objects.requireNonNull(newProfile, "Profile cannot be null");
    }

    /**
     * Assign settings to this therapist.
     */
    public void assignSettings(TherapistSettings newSettings) {
        this.settings = newSettings;
        newSettings.setTherapist(this);
    }

    /**
     * Generate or regenerate the autologin token.
     * 
     * @param tokenValue the new token value
     */
    public void regenerateAutologinToken(String tokenValue) {
        if (this.autologinToken == null) {
            this.autologinToken = TherapistAutologinToken.create(this, tokenValue);
        } else {
            this.autologinToken.updateToken(tokenValue);
        }
    }

    /**
     * Link Telegram account for notifications.
     * 
     * @param chatId the Telegram chat ID
     */
    public void linkTelegram(String chatId) {
        if (settings == null) {
            throw new IllegalStateException("Cannot link Telegram: settings not initialized");
        }
        settings.linkTelegram(chatId);
    }

    /**
     * Check if Telegram is linked.
     */
    public boolean isTelegramLinked() {
        return settings != null && settings.isTelegramLinked();
    }

    // ==================== Validation ====================

    private String validateEmail(String email) {
        Objects.requireNonNull(email, "Email is required");
        email = email.toLowerCase().trim();
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        return email;
    }

    // ==================== Equality ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Therapist therapist = (Therapist) o;
        return id != null && id.equals(therapist.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Therapist[id=%d, email=%s, role=%s, status=%s]",
            id, email, role, status);
    }
}

