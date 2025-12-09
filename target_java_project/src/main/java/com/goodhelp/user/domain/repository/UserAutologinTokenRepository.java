package com.goodhelp.user.domain.repository;

import com.goodhelp.user.domain.model.UserAutologinToken;

import java.util.Optional;

/**
 * Repository interface for UserAutologinToken entity.
 * Defines domain-level persistence operations.
 * 
 * <p>Implementation will be provided by the infrastructure layer
 * using Spring Data JPA.</p>
 */
public interface UserAutologinTokenRepository {

    /**
     * Find token by token string.
     */
    Optional<UserAutologinToken> findByToken(String token);

    /**
     * Find token by user ID.
     */
    Optional<UserAutologinToken> findByUserId(Long userId);

    /**
     * Save a token (create or update).
     */
    UserAutologinToken save(UserAutologinToken token);

    /**
     * Delete token by user ID.
     */
    void deleteByUserId(Long userId);

    /**
     * Delete a token.
     */
    void delete(UserAutologinToken token);

    /**
     * Check if token exists.
     */
    boolean existsByToken(String token);
}

