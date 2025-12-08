package com.goodhelp.billing.infrastructure.persistence;

import com.goodhelp.billing.domain.model.Order;
import com.goodhelp.billing.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderRepositoryAdapter implements OrderRepository {

    private final JpaOrderRepository jpaRepository;

    @Override
    @Transactional
    public Order save(Order order) {
        return jpaRepository.save(order);
    }
}

