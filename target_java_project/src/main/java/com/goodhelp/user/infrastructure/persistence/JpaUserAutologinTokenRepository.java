package com.goodhelp.user.infrastructure.persistence;

import com.goodhelp.user.domain.model.UserAutologinToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for UserAutologinToken entity.
 */
@Repository
public interface JpaUserAutologinTokenRepository extends JpaRepository<UserAutologinToken, Long> {

    /**
     * Find token by token string.
     */
    Optional<UserAutologinToken> findByToken(String token);

    /**
     * Find token by user ID.
     */
    Optional<UserAutologinToken> findByUserId(Long userId);

    /**
     * Delete token by user ID.
     */
    @Modifying
    @Query("DELETE FROM UserAutologinToken t WHERE t.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * Check if token exists.
     */
    boolean existsByToken(String token);
}

