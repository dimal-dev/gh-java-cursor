# Sub-Stage 6.2: Billing Repositories

## Goal
Create repository interfaces for billing module.

---

## Domain Interfaces

```java
public interface OrderRepository {
    Optional<Order> findById(Long id);
    Optional<Order> findByCheckoutSlug(String slug);
    Order save(Order order);
}

public interface CheckoutRepository {
    Optional<Checkout> findById(Long id);
    Optional<Checkout> findBySlug(String slug);
    Checkout save(Checkout checkout);
}

public interface UserWalletRepository {
    Optional<UserWallet> findByUserId(Long userId);
    UserWallet save(UserWallet wallet);
}

public interface UserWalletOperationRepository {
    List<UserWalletOperation> findByWalletId(Long walletId, Pageable pageable);
    UserWalletOperation save(UserWalletOperation operation);
}

public interface OrderLogRepository {
    OrderLog save(OrderLog log);
}
```

---

## JPA Repositories

```java
public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByCheckoutSlug(String slug);
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);
}

public interface CheckoutJpaRepository extends JpaRepository<Checkout, Long> {
    Optional<Checkout> findBySlug(String slug);
}

public interface UserWalletJpaRepository extends JpaRepository<UserWallet, Long> {
    Optional<UserWallet> findByUserId(Long userId);
}

public interface UserWalletOperationJpaRepository extends JpaRepository<UserWalletOperation, Long> {
    Page<UserWalletOperation> findByWalletIdOrderByCreatedAtDesc(Long walletId, Pageable pageable);
}

public interface OrderLogJpaRepository extends JpaRepository<OrderLog, Long> {}
```

---

## Verification
- [ ] All repositories compile
- [ ] Queries work correctly

---

## Next
Proceed to **6.3: WayForPay Service**

