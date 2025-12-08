package com.goodhelp.user.infrastructure.persistence;

import com.goodhelp.user.domain.model.UserPromocode;
import com.goodhelp.user.domain.model.UserPromocodeState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserPromocodeRepository extends JpaRepository<UserPromocode, Long> {

    long countByEmailIgnoreCaseAndPromocodeIdAndState(String email, Long promocodeId, UserPromocodeState state);
}

