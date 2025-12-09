package com.goodhelp.user.infrastructure.persistence;

import com.goodhelp.user.domain.model.User;
import com.goodhelp.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Adapter implementing the domain UserRepository interface
 * using Spring Data JPA.
 * 
 * <p>This class bridges the domain layer's repository interface with
 * the infrastructure layer's JPA implementation.</p>
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaRepository;

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmailIgnoreCase(email);
    }

    @Override
    @Transactional
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}

