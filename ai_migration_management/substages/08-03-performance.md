# Sub-Stage 8.3: Performance Optimization

## Goal
Optimize database queries, add caching, and improve response times.

---

## Database Optimization

### Add Missing Indexes
```sql
-- Frequently queried columns
CREATE INDEX idx_psiholog_status ON psiholog(status);
CREATE INDEX idx_psiholog_email ON psiholog(email);

CREATE INDEX idx_schedule_psiholog_time ON psiholog_schedule(psiholog_id, available_at);
CREATE INDEX idx_schedule_status ON psiholog_schedule(status);

CREATE INDEX idx_consultation_user ON user_consultation(user_id);
CREATE INDEX idx_consultation_psiholog ON user_consultation(psiholog_id);
CREATE INDEX idx_consultation_state ON user_consultation(state);

CREATE INDEX idx_chat_message_user_psiholog ON chat_message(user_id, psiholog_id);
CREATE INDEX idx_chat_message_status ON chat_message(status);

CREATE INDEX idx_order_checkout_slug ON "order"(checkout_slug);
```

### Optimize N+1 Queries
```java
// Bad: N+1 problem
@Query("SELECT p FROM Therapist p")
List<Therapist> findAll(); // Then profile loaded separately

// Good: Fetch join
@Query("SELECT p FROM Therapist p LEFT JOIN FETCH p.profile WHERE p.status = :status")
List<Therapist> findAllActiveWithProfile(@Param("status") TherapistStatus status);
```

### Use Projections for Read-Only
```java
// Instead of loading full entity for list view
public interface TherapistListProjection {
    Long getId();
    String getEmail();
    String getProfileFirstName();
    String getProfileLastName();
}

@Query("SELECT p.id as id, p.email as email, pr.firstName as profileFirstName " +
       "FROM Therapist p JOIN p.profile pr WHERE p.status = :status")
List<TherapistListProjection> findActiveForList(@Param("status") TherapistStatus status);
```

---

## Caching

### Spring Cache Configuration
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        var caffeine = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(10))
            .maximumSize(1000);
        
        return new CaffeineCacheManager("therapists", "profiles", "prices");
    }
}
```

### Cache Usage
```java
@Service
public class TherapistCatalogService {
    
    @Cacheable(value = "therapists", key = "'active-list'")
    public List<TherapistListDto> getActiveTherapists() {
        // Expensive query cached for 10 minutes
    }
    
    @Cacheable(value = "profiles", key = "#id")
    public TherapistProfileDto getProfile(Long id) {
        // Individual profile cached
    }
    
    @CacheEvict(value = "therapists", allEntries = true)
    public void updateTherapist(Long id, UpdateCommand cmd) {
        // Invalidate list cache on update
    }
}
```

---

## Static Asset Optimization

### Resource Configuration
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(31536000)  // 1 year
            .resourceChain(true)
            .addResolver(new VersionResourceResolver()
                .addContentVersionStrategy("/**"));
    }
}
```

### GZIP Compression
```yaml
server:
  compression:
    enabled: true
    mime-types: text/html,text/css,application/javascript,application/json
    min-response-size: 1024
```

---

## Verification
- [ ] Database indexes created
- [ ] No N+1 queries in critical paths
- [ ] Caching reduces DB load
- [ ] Static assets cached
- [ ] Response times acceptable (<200ms for pages)

---

## Next
Proceed to **8.4: Security Hardening**

