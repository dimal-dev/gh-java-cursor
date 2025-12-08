package com.goodhelp.user.domain.repository;

import com.goodhelp.user.domain.model.Promocode;
import com.goodhelp.user.domain.model.PromocodeState;

import java.util.Optional;

/**
 * Repository for promocodes.
 */
public interface PromocodeRepository {

    Optional<Promocode> findByNameIgnoreCase(String name);

    Optional<Promocode> findById(Long id);

    default Optional<Promocode> findActiveByName(String name) {
        return findByNameIgnoreCase(name)
            .filter(promo -> promo.getState() == PromocodeState.ACTIVE);
    }
}

