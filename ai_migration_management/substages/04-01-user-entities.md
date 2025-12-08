# Sub-Stage 4.1: User Entities

## Goal
Create domain entities for the User bounded context.

---

## Entities to Create

**Location:** `com.goodhelp.user.domain.model/`

### User (Aggregate Root)
```java
@Entity
@Table(name = "user")
public class User {
    @Id @GeneratedValue
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String fullName;
    private boolean fullNameSetByUser;
    
    private String timezone;
    private String locale;
    private boolean emailReal;
    
    // Domain methods
    public void updateProfile(String fullName, String timezone) {
        this.fullName = fullName;
        this.fullNameSetByUser = true;
        this.timezone = timezone;
    }
    
    public boolean needsProfileSetup() {
        return !fullNameSetByUser || fullName == null || fullName.isBlank();
    }
}
```

### UserConsultation
```java
@Entity
@Table(name = "user_consultation")
public class UserConsultation {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Therapist therapist;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private PriceOption price;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private ScheduleSlot slot;
    
    @Convert(converter = ConsultationStateConverter.class)
    private ConsultationState state;
    
    @Convert(converter = ConsultationTypeConverter.class)
    private ConsultationType type;
    
    // Domain methods
    public boolean canBeCancelledByUser(LocalDateTime now) {
        return state.isActive() && slot.getStartTime().isAfter(now.plusHours(24));
    }
    
    public void cancelByUser(LocalDateTime now, boolean inTime) {
        this.state = inTime ? 
            ConsultationState.CANCELLED_BY_USER_IN_TIME : 
            ConsultationState.CANCELLED_BY_USER_NOT_IN_TIME;
        slot.release();
    }
}
```

### UserAutologinToken
```java
@Entity
@Table(name = "user_autologin_token")
public class UserAutologinToken {
    @Id @GeneratedValue
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String token;
    
    @OneToOne
    private User user;
}
```

---

## Enums

```java
public enum ConsultationState {
    CREATED(1),
    COMPLETED(2),
    CANCELLED_BY_USER_IN_TIME(5),
    CANCELLED_BY_USER_NOT_IN_TIME(6),
    CANCELLED_BY_PSIHOLOG_IN_TIME(7),
    CANCELLED_BY_PSIHOLOG_NOT_IN_TIME(8);
    
    public boolean isActive() {
        return this == CREATED;
    }
}

public enum ConsultationType {
    INDIVIDUAL(1),
    COUPLE(2);
}
```

---

## Related Entities (Shared)
- `ChatMessage` - Already defined in Stage 2
- `Promocode` - Promocode entity
- `UserPromocode` - User's applied promocodes
- `UserRequestPsiholog` - From Stage 3

---

## PHP Reference
See `02-ENTITY-MAPPING.md` for complete field mapping:
- `original_php_project/src/User/Entity/`

---

## Verification
- [ ] All entities compile
- [ ] Enum converters work
- [ ] Relationships mapped correctly
- [ ] Domain methods implemented

---

## Next
Proceed to **4.2: Repositories**

