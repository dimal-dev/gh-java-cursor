package com.goodhelp.billing.infrastructure.persistence;

import com.goodhelp.billing.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderRepository extends JpaRepository<Order, Long> {
}

