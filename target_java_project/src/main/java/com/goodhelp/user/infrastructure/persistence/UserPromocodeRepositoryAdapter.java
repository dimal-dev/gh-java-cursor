package com.goodhelp.user.infrastructure.persistence;

import com.goodhelp.user.domain.model.UserPromocode;
import com.goodhelp.user.domain.model.UserPromocodeState;
import com.goodhelp.user.domain.repository.UserPromocodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPromocodeRepositoryAdapter implements UserPromocodeRepository {

    private final JpaUserPromocodeRepository jpaRepository;

    @Override
    public long countByEmailAndPromocodeIdAndState(String email, Long promocodeId, UserPromocodeState state) {
        return jpaRepository.countByEmailIgnoreCaseAndPromocodeIdAndState(email, promocodeId, state);
    }

    @Override
    @Transactional
    public UserPromocode save(UserPromocode userPromocode) {
        return jpaRepository.save(userPromocode);
    }

    @Override
    public Optional<UserPromocode> findById(Long id) {
        return jpaRepository.findById(id);
    }
}

