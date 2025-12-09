package com.goodhelp.user.domain.repository;

import com.goodhelp.user.domain.model.User;

import java.util.Optional;

/**
 * Repository interface for User aggregate.
 * Defines domain-level persistence operations.
 * 
 * <p>Implementation will be provided by the infrastructure layer
 * using Spring Data JPA.</p>
 */
public interface UserRepository {

    /**
     * Find user by ID.
     */
    Optional<User> findById(Long id);

    /**
     * Find user by email address.
     * Email comparison should be case-insensitive.
     */
    Optional<User> findByEmail(String email);

    /**
     * Save a user (create or update).
     */
    User save(User user);

    /**
     * Check if email is already in use.
     */
    boolean existsByEmail(String email);

    /**
     * Check if user exists by ID.
     */
    boolean existsById(Long id);
}

