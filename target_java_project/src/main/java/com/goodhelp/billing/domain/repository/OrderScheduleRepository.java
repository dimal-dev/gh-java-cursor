package com.goodhelp.billing.domain.repository;

import com.goodhelp.billing.domain.model.OrderSchedule;

import java.util.Optional;

/**
 * Repository for order to schedule links.
 */
public interface OrderScheduleRepository {

    OrderSchedule save(OrderSchedule orderSchedule);
    
    Optional<OrderSchedule> findByOrderId(Long orderId);
}

