package com.goodhelp.user.infrastructure.persistence;

import com.goodhelp.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for User entity.
 */
@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email (case-insensitive).
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if user exists by email (case-insensitive).
     */
    boolean existsByEmailIgnoreCase(String email);
}

