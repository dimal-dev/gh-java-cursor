# Sub-Stage 4.2: User Repositories

## Goal
Create repository interfaces and JPA implementations for User module.

---

## Domain Repository Interfaces

**Location:** `com.goodhelp.user.domain.repository/`

```java
public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
}

public interface UserConsultationRepository {
    Optional<UserConsultation> findById(Long id);
    List<UserConsultation> findUpcomingByUserId(Long userId);
    List<UserConsultation> findByUserIdAndTherapistId(Long userId, Long therapistId);
    UserConsultation save(UserConsultation consultation);
}

public interface UserAuthTokenRepository {
    Optional<UserAutologinToken> findByToken(String token);
    void save(UserAutologinToken token);
    void deleteByUserId(Long userId);
}
```

---

## JPA Repositories

**Location:** `com.goodhelp.user.infrastructure.persistence/`

```java
public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

public interface UserConsultationJpaRepository extends JpaRepository<UserConsultation, Long> {
    @Query("SELECT c FROM UserConsultation c WHERE c.user.id = :userId " +
           "AND c.state = 1 AND c.slot.startTime > :now ORDER BY c.slot.startTime")
    List<UserConsultation> findUpcoming(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    List<UserConsultation> findByUserIdAndTherapistIdOrderBySlotStartTimeDesc(
        Long userId, Long therapistId);
    
    @Query("SELECT COUNT(c) FROM UserConsultation c WHERE c.user.id = :userId AND c.state = 2")
    int countCompleted(@Param("userId") Long userId);
}

public interface UserAuthTokenJpaRepository extends JpaRepository<UserAutologinToken, Long> {
    Optional<UserAutologinToken> findByToken(String token);
    void deleteByUserId(Long userId);
}

public interface PromocodeJpaRepository extends JpaRepository<Promocode, Long> {
    Optional<Promocode> findByNameAndState(String name, PromocodeState state);
}

public interface UserPromocodeJpaRepository extends JpaRepository<UserPromocode, Long> {
    Optional<UserPromocode> findByUserIdAndPromocodeId(Long userId, Long promocodeId);
    List<UserPromocode> findByEmailAndState(String email, UserPromocodeState state);
}
```

---

## Repository Implementations

```java
@Repository
public class JpaUserRepository implements UserRepository {
    private final UserJpaRepository jpaRepository;
    
    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }
    
    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }
}
```

---

## Verification
- [ ] All repositories compile
- [ ] Custom queries work correctly
- [ ] Domain repos delegate to JPA repos

---

## Next
Proceed to **4.3: Security**

