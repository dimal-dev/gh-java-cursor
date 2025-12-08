# Sub-Stage 8.1: Integration Tests

## Goal
Create comprehensive integration tests for cross-module functionality.

---

## Test Categories

### 1. Repository Integration Tests
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TherapistRepositoryIntegrationTest {
    
    @Autowired
    private TherapistJpaRepository repository;
    
    @Test
    void shouldFindTherapistByEmail() {
        // Given
        var therapist = createTestTherapist("test@example.com");
        repository.save(therapist);
        
        // When
        var found = repository.findByEmail("test@example.com");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }
}
```

### 2. Service Integration Tests
```java
@SpringBootTest
@Transactional
class BookConsultationIntegrationTest {
    
    @Autowired
    private BookConsultationUseCase bookConsultationUseCase;
    
    @Autowired
    private TestDataFactory testData;
    
    @Test
    void shouldCreateConsultationWhenSlotAvailable() {
        // Given
        var therapist = testData.createTherapist();
        var slot = testData.createAvailableSlot(therapist.getId());
        var user = testData.createUser();
        var price = testData.createPrice(therapist.getId());
        
        // When
        var command = new BookConsultationCommand(
            therapist.getId(), slot.getId(), price.getId(), user.getId());
        var result = bookConsultationUseCase.execute(command);
        
        // Then
        assertThat(result.consultationId()).isNotNull();
        // Verify slot is now booked
        var updatedSlot = slotRepository.findById(slot.getId());
        assertThat(updatedSlot.get().getStatus()).isEqualTo(SlotStatus.BOOKED);
    }
}
```

### 3. Controller Integration Tests
```java
@WebMvcTest(TherapistListController.class)
class TherapistListControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private GetTherapistCatalogUseCase catalogUseCase;
    
    @Test
    void shouldReturnTherapistListPage() throws Exception {
        // Given
        when(catalogUseCase.execute(any())).thenReturn(List.of());
        
        // When/Then
        mockMvc.perform(get("/therapist-list"))
            .andExpect(status().isOk())
            .andExpect(view().name("landing/therapist-list"));
    }
}
```

---

## Test Data Factory

```java
@Component
public class TestDataFactory {
    
    @Autowired
    private TherapistRepository therapistRepository;
    
    @Autowired
    private ScheduleSlotRepository slotRepository;
    
    public Therapist createTherapist() {
        var p = Therapist.builder()
            .email("test" + UUID.randomUUID() + "@example.com")
            .role(TherapistRole.PSIHOLOG)
            .status(TherapistStatus.ACTIVE)
            .build();
        return therapistRepository.save(p);
    }
    
    public ScheduleSlot createAvailableSlot(Long therapistId) {
        var slot = new ScheduleSlot();
        slot.setTherapistId(therapistId);
        slot.setStartTime(LocalDateTime.now().plusDays(1));
        slot.setStatus(SlotStatus.AVAILABLE);
        return slotRepository.save(slot);
    }
    
    // ... more factory methods
}
```

---

## Test Configuration

```java
@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public Clock testClock() {
        return Clock.fixed(Instant.parse("2024-01-15T10:00:00Z"), ZoneId.of("UTC"));
    }
}
```

---

## Verification
- [ ] Repository tests pass
- [ ] Service integration tests pass
- [ ] Controller tests pass
- [ ] Test coverage adequate

---

## Next
Proceed to **8.2: End-to-End Booking Flow**

