package com.goodhelp.billing.infrastructure.persistence;

import com.goodhelp.billing.domain.model.OrderSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderScheduleRepository extends JpaRepository<OrderSchedule, Long> {
}

