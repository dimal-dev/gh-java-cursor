package com.goodhelp.landing.domain.repository;

import com.goodhelp.landing.domain.model.UserRequestTherapist;

import java.util.Optional;

/**
 * Repository interface for UserRequestTherapist.
 */
public interface UserRequestTherapistRepository {

    /**
     * Save a user request.
     */
    UserRequestTherapist save(UserRequestTherapist request);

    /**
     * Find by ID.
     */
    Optional<UserRequestTherapist> findById(Long id);
}

