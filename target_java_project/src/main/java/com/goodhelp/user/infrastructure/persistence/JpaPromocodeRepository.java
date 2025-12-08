package com.goodhelp.user.infrastructure.persistence;

import com.goodhelp.user.domain.model.Promocode;
import com.goodhelp.user.domain.model.PromocodeState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPromocodeRepository extends JpaRepository<Promocode, Long> {

    Optional<Promocode> findByNameIgnoreCase(String name);

    Optional<Promocode> findByNameIgnoreCaseAndState(String name, PromocodeState state);
}

