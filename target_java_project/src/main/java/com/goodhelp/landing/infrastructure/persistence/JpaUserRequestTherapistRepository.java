package com.goodhelp.landing.infrastructure.persistence;

import com.goodhelp.landing.domain.model.UserRequestTherapist;
import com.goodhelp.landing.domain.repository.UserRequestTherapistRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for UserRequestTherapist.
 */
@Repository
public interface JpaUserRequestTherapistRepository 
        extends JpaRepository<UserRequestTherapist, Long>, UserRequestTherapistRepository {
}

