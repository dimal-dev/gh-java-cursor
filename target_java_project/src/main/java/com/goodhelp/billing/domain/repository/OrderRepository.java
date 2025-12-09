package com.goodhelp.billing.domain.repository;

import com.goodhelp.billing.domain.model.Order;

import java.util.Optional;

/**
 * Repository for billing orders.
 */
public interface OrderRepository {

    Order save(Order order);
    
    Optional<Order> findByCheckoutSlug(String checkoutSlug);
}

