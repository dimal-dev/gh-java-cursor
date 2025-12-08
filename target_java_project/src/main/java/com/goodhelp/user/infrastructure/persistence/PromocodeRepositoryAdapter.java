package com.goodhelp.user.infrastructure.persistence;

import com.goodhelp.user.domain.model.Promocode;
import com.goodhelp.user.domain.model.PromocodeState;
import com.goodhelp.user.domain.repository.PromocodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromocodeRepositoryAdapter implements PromocodeRepository {

    private final JpaPromocodeRepository jpaRepository;

    @Override
    public Optional<Promocode> findByNameIgnoreCase(String name) {
        return jpaRepository.findByNameIgnoreCaseAndState(name, PromocodeState.ACTIVE)
            .or(() -> jpaRepository.findByNameIgnoreCase(name));
    }

    @Override
    public Optional<Promocode> findById(Long id) {
        return jpaRepository.findById(id);
    }
}

