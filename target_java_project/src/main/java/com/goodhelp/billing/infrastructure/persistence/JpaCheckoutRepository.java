package com.goodhelp.billing.infrastructure.persistence;

import com.goodhelp.billing.domain.model.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCheckoutRepository extends JpaRepository<Checkout, Long> {

    Optional<Checkout> findBySlug(String slug);
}

