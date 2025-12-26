package com.goodhelp.therapist.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.security.SecureRandom;
import java.util.Objects;

/**
 * Entity representing an auto-login token for passwordless authentication.
 * 
 * <p>Authentication flow:</p>
 * <ol>
 *   <li>Therapist enters email on login page</li>
 *   <li>System generates token and sends link via email/Telegram</li>
 *   <li>Therapist clicks link with token</li>
 *   <li>System validates token and creates session</li>
 * </ol>
 */
@Entity
@Table(name = "therapist_autologin_token", indexes = {
    @Index(name = "idx_therapist_autologin_token", columnList = "token", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TherapistAutologinToken extends BaseEntity {

    private static final int TOKEN_LENGTH = 32;
    private static final String TOKEN_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = TOKEN_LENGTH)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false, unique = true)
    private Therapist therapist;

    /**
     * Private constructor - use factory methods.
     */
    private TherapistAutologinToken(Therapist therapist, String token) {
        this.therapist = Objects.requireNonNull(therapist, "Therapist is required");
        this.token = validateToken(token);
    }

    /**
     * Create a new autologin token for a therapist.
     */
    public static TherapistAutologinToken create(Therapist therapist, String token) {
        return new TherapistAutologinToken(therapist, token);
    }

    /**
     * Create with auto-generated token.
     */
    public static TherapistAutologinToken createWithGeneratedToken(Therapist therapist) {
        return new TherapistAutologinToken(therapist, generateToken());
    }

    /**
     * Generate a cryptographically secure random token.
     */
    public static String generateToken() {
        StringBuilder sb = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            sb.append(TOKEN_CHARS.charAt(RANDOM.nextInt(TOKEN_CHARS.length())));
        }
        return sb.toString();
    }

    // ==================== Business Methods ====================

    /**
     * Check if this token matches the given value.
     * Uses constant-time comparison to prevent timing attacks.
     */
    public boolean matches(String tokenToCheck) {
        if (tokenToCheck == null || tokenToCheck.length() != token.length()) {
            return false;
        }
        // Constant-time comparison
        int result = 0;
        for (int i = 0; i < token.length(); i++) {
            result |= token.charAt(i) ^ tokenToCheck.charAt(i);
        }
        return result == 0;
    }

    /**
     * Update the token (regenerate).
     */
    void updateToken(String newToken) {
        this.token = validateToken(newToken);
    }

    /**
     * Regenerate with new random token.
     */
    public void regenerate() {
        this.token = generateToken();
    }

    /**
     * Build the auto-login URL path.
     * 
     * @return path like "/therapist/auto-login?t=abc123"
     */
    public String buildLoginPath() {
        return "/therapist/auto-login?t=" + token;
    }

    // ==================== Validation ====================

    private String validateToken(String token) {
        Objects.requireNonNull(token, "Token is required");
        if (token.length() != TOKEN_LENGTH) {
            throw new IllegalArgumentException(
                "Token must be exactly " + TOKEN_LENGTH + " characters"
            );
        }
        return token.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TherapistAutologinToken that = (TherapistAutologinToken) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
