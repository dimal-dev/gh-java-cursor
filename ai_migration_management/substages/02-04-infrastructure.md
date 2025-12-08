# Sub-Stage 2.4: Infrastructure Layer

## Goal
Implement JPA repositories and persistence mapping for therapist domain.

---

## JPA Entities

**Location:** `com.goodhelp.therapist.infrastructure.persistence/`

### Therapist JPA Entity
```java
@Entity
@Table(name = "psiholog")
public class TherapistJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Convert(converter = TherapistRoleConverter.class)
    private TherapistRole role;
    
    @Convert(converter = TherapistStatusConverter.class)
    private TherapistStatus status;
    
    @OneToOne(mappedBy = "therapist", cascade = CascadeType.ALL)
    private TherapistProfileJpaEntity profile;
    
    @OneToOne(mappedBy = "therapist", cascade = CascadeType.ALL)
    private TherapistSettingsJpaEntity settings;
}
```

### Enum Converters
```java
@Converter(autoApply = true)
public class TherapistRoleConverter implements AttributeConverter<TherapistRole, Integer> {
    @Override
    public Integer convertToDatabaseColumn(TherapistRole role) {
        return role == null ? null : role.getValue();
    }
    
    @Override
    public TherapistRole convertToEntityAttribute(Integer value) {
        return value == null ? null : TherapistRole.fromValue(value);
    }
}
```

---

## Spring Data Repositories

**Location:** `com.goodhelp.therapist.infrastructure.persistence/`

```java
public interface TherapistJpaRepository extends JpaRepository<TherapistJpaEntity, Long> {
    Optional<TherapistJpaEntity> findByEmail(String email);
    
    @Query("SELECT p FROM TherapistJpaEntity p WHERE p.status = :status AND p.role IN :roles")
    List<TherapistJpaEntity> findByStatusAndRoleIn(TherapistStatus status, List<TherapistRole> roles);
}

public interface ScheduleSlotJpaRepository extends JpaRepository<ScheduleSlotJpaEntity, Long> {
    List<ScheduleSlotJpaEntity> findByTherapistIdAndAvailableAtBetween(
        Long therapistId, LocalDateTime from, LocalDateTime to);
    
    @Query("SELECT s FROM ScheduleSlotJpaEntity s WHERE s.therapist.id = :psihologId " +
           "AND s.availableAt > :after AND s.status = :status ORDER BY s.availableAt")
    List<ScheduleSlotJpaEntity> findAvailable(Long psihologId, LocalDateTime after, SlotStatus status);
}

public interface PriceOptionJpaRepository extends JpaRepository<PriceOptionJpaEntity, Long> {
    List<PriceOptionJpaEntity> findByTherapistIdAndStatusOrderByTypeAsc(Long therapistId, PriceStatus status);
    Optional<PriceOptionJpaEntity> findBySlug(String slug);
}

public interface AuthTokenJpaRepository extends JpaRepository<AuthTokenJpaEntity, Long> {
    Optional<AuthTokenJpaEntity> findByToken(String token);
    void deleteByTherapistId(Long therapistId);
}

public interface UserNotesJpaRepository extends JpaRepository<UserNotesJpaEntity, Long> {
    Optional<UserNotesJpaEntity> findByTherapistIdAndUserId(Long therapistId, Long userId);
}
```

---

## Domain Repository Implementations

```java
@Repository
public class JpaTherapistRepository implements TherapistRepository {
    
    private final TherapistJpaRepository jpaRepository;
    private final TherapistMapper mapper;
    
    @Override
    public Optional<Therapist> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    
    @Override
    public Therapist save(Therapist therapist) {
        var entity = mapper.toJpaEntity(therapist);
        return mapper.toDomain(jpaRepository.save(entity));
    }
}
```

---

## Database Tables

| Table | Primary Entity | Notes |
|-------|----------------|-------|
| `psiholog` | Therapist | Core therapist data |
| `psiholog_profile` | Profile | 1:1 with psiholog |
| `psiholog_settings` | Settings | 1:1 with psiholog |
| `psiholog_schedule` | ScheduleSlot | Time slots |
| `psiholog_price` | PriceOption | Pricing options |
| `psiholog_autologin_token` | AuthToken | Login tokens |
| `psiholog_user_notes` | UserNotes | Notes about clients |

---

## Verification
- [ ] All JPA entities map correctly to database
- [ ] Enum converters work for state fields
- [ ] Domain repositories delegate to JPA repos
- [ ] Mappers convert between domain and JPA entities

---

## Next
Proceed to **2.5: Security (Authentication)**

