# Sub-Stage 5.2: Staff Repositories

## Goal
Create repository interfaces and JPA implementations for staff module.

---

## Domain Interfaces

```java
public interface StaffUserRepository {
    Optional<StaffUser> findById(Long id);
    Optional<StaffUser> findByEmail(String email);
    StaffUser save(StaffUser user);
}

public interface StaffAuthTokenRepository {
    Optional<StaffUserAutologinToken> findByToken(String token);
    void save(StaffUserAutologinToken token);
}

public interface TherapistPayoutRepository {
    List<TherapistPayout> findByTherapistId(Long therapistId, Pageable pageable);
    TherapistPayout save(TherapistPayout payout);
    int sumByTherapistId(Long therapistId);
}
```

---

## JPA Repositories

```java
public interface StaffUserJpaRepository extends JpaRepository<StaffUser, Long> {
    Optional<StaffUser> findByEmail(String email);
}

public interface StaffAuthTokenJpaRepository extends JpaRepository<StaffUserAutologinToken, Long> {
    Optional<StaffUserAutologinToken> findByToken(String token);
}

public interface TherapistPayoutJpaRepository extends JpaRepository<TherapistPayout, Long> {
    Page<TherapistPayout> findByTherapistIdOrderByCreatedAtDesc(
        Long therapistId, Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM TherapistPayout p WHERE p.therapist.id = :psihologId")
    int sumAmountByTherapistId(@Param("psihologId") Long therapistId);
}
```

---

## Additional Repository Needs

Staff module also uses repositories from other modules:
- `TherapistRepository` - List and edit therapists
- `TherapistProfileRepository` - Edit profiles
- `BlogPostRepository` - Manage blog

---

## Verification
- [ ] All repositories compile
- [ ] Custom queries work

---

## Next
Proceed to **5.3: Security**

