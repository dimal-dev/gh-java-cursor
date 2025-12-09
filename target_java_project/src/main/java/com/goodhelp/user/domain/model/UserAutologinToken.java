package com.goodhelp.user.domain.model;

import com.goodhelp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.security.SecureRandom;
import java.util.Objects;

/**
 * Entity representing an autologin token for user authentication.
 * 
 * <p>Users authenticate via email links containing a unique token.
 * This token allows passwordless login.</p>
 * 
 * <p>Business rules:</p>
 * <ul>
 *   <li>Each user has exactly one autologin token</li>
 *   <li>Token must be unique across all users</li>
 *   <li>Token is 32 characters long</li>
 * </ul>
 */
@Entity
@Table(name = "user_autologin_token", indexes = {
    @Index(name = "idx_user_autologin_token", columnList = "token", unique = true)
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
public class UserAutologinToken extends BaseEntity {

    private static final int TOKEN_LENGTH = 32;
    private static final String TOKEN_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 32)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Private constructor - use factory methods.
     */
    private UserAutologinToken(User user, String token) {
        this.user = Objects.requireNonNull(user, "User is required");
        this.token = validateToken(token);
    }

    /**
     * Create a new autologin token for a user.
     */
    public static UserAutologinToken create(User user, String token) {
        return new UserAutologinToken(user, token);
    }

    /**
     * Create with auto-generated token.
     */
    public static UserAutologinToken createWithGeneratedToken(User user) {
        return new UserAutologinToken(user, generateToken());
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

    /**
     * Update the token (e.g., on token rotation).
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
     * @return path like "/user/auto-login?t=abc123"
     */
    public String buildLoginPath() {
        return "/user/auto-login?t=" + token;
    }

    // ==================== Validation ====================

    private String validateToken(String token) {
        Objects.requireNonNull(token, "Token is required");
        token = token.trim().toLowerCase();
        if (token.length() != TOKEN_LENGTH) {
            throw new IllegalArgumentException("Token must be exactly " + TOKEN_LENGTH + " characters: " + token.length());
        }
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAutologinToken that = (UserAutologinToken) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("UserAutologinToken[id=%d, user=%d, token=%s...]", 
            id, user != null ? user.getId() : null, 
            token != null && token.length() > 4 ? token.substring(0, 4) + "..." : "null");
    }
}

