# Sub-Stage 5.1: Staff Entities

## Goal
Create domain entities for staff/admin module.

---

## Entities to Create

**Location:** `com.goodhelp.staff.domain.model/`

### StaffUser
```java
@Entity
@Table(name = "staff_user")
public class StaffUser {
    @Id @GeneratedValue
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Convert(converter = StaffRoleConverter.class)
    private StaffRole role;
    
    @Convert(converter = StaffStateConverter.class)
    private StaffState state;
    
    public boolean isSuperuser() {
        return role == StaffRole.SUPERUSER;
    }
}
```

### StaffUserAutologinToken
```java
@Entity
@Table(name = "staff_user_autologin_token")
public class StaffUserAutologinToken {
    @Id @GeneratedValue
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String token;
    
    @OneToOne
    private StaffUser staffUser;
}
```

### TherapistPayout
```java
@Entity
@Table(name = "psiholog_payout")
public class TherapistPayout {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Therapist therapist;
    
    private int amount;
    private String currency;
    private String note;
    
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private StaffUser createdBy;
}
```

---

## Enums

```java
public enum StaffRole {
    USER(1),
    SUPERUSER(100);
}

public enum StaffState {
    ACTIVE(1);
}
```

---

## Related Entities
- `Therapist` - Full management (from Stage 2)
- `TherapistProfile` - Edit profile data
- `BlogPost` - Blog management

---

## PHP Reference
- `original_php_project/src/Staff/Entity/`

---

## Verification
- [ ] Entities compile
- [ ] Enum converters work
- [ ] Relationships correct

---

## Next
Proceed to **5.2: Repositories**

