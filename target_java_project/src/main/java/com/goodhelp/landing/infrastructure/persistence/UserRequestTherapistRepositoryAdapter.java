package com.goodhelp.landing.infrastructure.persistence;

import com.goodhelp.landing.domain.model.UserRequestTherapist;
import com.goodhelp.landing.domain.repository.UserRequestTherapistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter that implements domain repository interface using JPA repository.
 */
@Component
@RequiredArgsConstructor
public class UserRequestTherapistRepositoryAdapter implements UserRequestTherapistRepository {

    private final JpaUserRequestTherapistRepository jpaRepository;

    @Override
    public UserRequestTherapist save(UserRequestTherapist request) {
        return jpaRepository.save(request);
    }

    @Override
    public Optional<UserRequestTherapist> findById(Long id) {
        return jpaRepository.findById(id);
    }
}

