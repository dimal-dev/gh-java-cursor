package com.goodhelp.billing.infrastructure.persistence;

import com.goodhelp.billing.domain.model.Checkout;
import com.goodhelp.billing.domain.repository.CheckoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckoutRepositoryAdapter implements CheckoutRepository {

    private final JpaCheckoutRepository jpaRepository;

    @Override
    @Transactional
    public Checkout save(Checkout checkout) {
        return jpaRepository.save(checkout);
    }

    @Override
    public Optional<Checkout> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug);
    }
}

