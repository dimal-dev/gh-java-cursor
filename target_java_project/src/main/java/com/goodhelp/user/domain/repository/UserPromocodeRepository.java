package com.goodhelp.user.domain.repository;

import com.goodhelp.user.domain.model.UserPromocode;
import com.goodhelp.user.domain.model.UserPromocodeState;

import java.util.Optional;

/**
 * Repository for user promocode usage.
 */
public interface UserPromocodeRepository {

    long countByEmailAndPromocodeIdAndState(String email, Long promocodeId, UserPromocodeState state);

    UserPromocode save(UserPromocode userPromocode);

    Optional<UserPromocode> findById(Long id);
}

