package com.goodhelp.user.infrastructure.persistence;

import com.goodhelp.user.domain.model.UserAutologinToken;
import com.goodhelp.user.domain.repository.UserAutologinTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Adapter implementing the domain UserAutologinTokenRepository interface
 * using Spring Data JPA.
 * 
 * <p>This class bridges the domain layer's repository interface with
 * the infrastructure layer's JPA implementation.</p>
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAutologinTokenRepositoryAdapter implements UserAutologinTokenRepository {

    private final JpaUserAutologinTokenRepository jpaRepository;

    @Override
    public Optional<UserAutologinToken> findByToken(String token) {
        return jpaRepository.findByToken(token);
    }

    @Override
    public Optional<UserAutologinToken> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public UserAutologinToken save(UserAutologinToken token) {
        return jpaRepository.save(token);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        jpaRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void delete(UserAutologinToken token) {
        jpaRepository.delete(token);
    }

    @Override
    public boolean existsByToken(String token) {
        return jpaRepository.existsByToken(token);
    }
}

