package com.goodhelp.billing.domain.repository;

import com.goodhelp.billing.domain.model.Order;

/**
 * Repository for billing orders.
 */
public interface OrderRepository {

    Order save(Order order);
}

