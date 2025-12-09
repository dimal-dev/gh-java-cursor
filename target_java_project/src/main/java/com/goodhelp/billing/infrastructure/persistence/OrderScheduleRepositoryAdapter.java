package com.goodhelp.billing.infrastructure.persistence;

import com.goodhelp.billing.domain.model.OrderSchedule;
import com.goodhelp.billing.domain.repository.OrderScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderScheduleRepositoryAdapter implements OrderScheduleRepository {

    private final JpaOrderScheduleRepository jpaRepository;

    @Override
    @Transactional
    public OrderSchedule save(OrderSchedule orderSchedule) {
        return jpaRepository.save(orderSchedule);
    }
    
    @Override
    public Optional<OrderSchedule> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId);
    }
}

