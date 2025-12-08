package com.goodhelp.billing.domain.repository;

import com.goodhelp.billing.domain.model.Checkout;

import java.util.Optional;

/**
 * Repository for checkout sessions.
 */
public interface CheckoutRepository {

    Checkout save(Checkout checkout);

    Optional<Checkout> findBySlug(String slug);
}

