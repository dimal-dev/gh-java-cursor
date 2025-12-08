# Sub-Stage 8.2: End-to-End Booking Flow Test

## Goal
Test complete booking flow from browsing to payment confirmation.

---

## E2E Test Scenarios

### Complete Booking Flow
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingE2ETest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private TestDataFactory testData;
    
    @Test
    void completeBookingFlow() {
        // 1. Create test data
        var therapist = testData.createActiveTherapistWithSlots();
        var slot = therapist.getAvailableSlots().get(0);
        var price = therapist.getPrices().get(0);
        
        // 2. View therapist list
        var listResponse = restTemplate.getForEntity("/therapist-list", String.class);
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // 3. View therapist profile
        var profileResponse = restTemplate.getForEntity(
            "/therapist/" + therapist.getId(), String.class);
        assertThat(profileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // 4. View booking page
        var bookingResponse = restTemplate.getForEntity(
            "/book-consultation/" + therapist.getId(), String.class);
        assertThat(bookingResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // 5. Submit booking form
        var bookingForm = new LinkedMultiValueMap<String, String>();
        bookingForm.add("priceId", price.getId().toString());
        bookingForm.add("slotId", slot.getId().toString());
        bookingForm.add("email", "testuser@example.com");
        bookingForm.add("name", "Test User");
        bookingForm.add("phone", "+380501234567");
        
        var checkoutResponse = restTemplate.postForEntity(
            "/checkout", bookingForm, String.class);
        
        // Should redirect to checkout page
        assertThat(checkoutResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        
        // 6. Verify checkout created
        var checkout = checkoutRepository.findByEmail("testuser@example.com");
        assertThat(checkout).isPresent();
        
        // 7. Simulate payment webhook
        simulatePaymentWebhook(checkout.get().getSlug(), "Approved");
        
        // 8. Verify consultation created
        var consultations = consultationRepository.findByUserEmail("testuser@example.com");
        assertThat(consultations).hasSize(1);
        assertThat(consultations.get(0).getState()).isEqualTo(ConsultationState.CREATED);
        
        // 9. Verify slot booked
        var updatedSlot = slotRepository.findById(slot.getId());
        assertThat(updatedSlot.get().getStatus()).isEqualTo(SlotStatus.BOOKED);
    }
    
    private void simulatePaymentWebhook(String orderReference, String status) {
        // Create webhook request matching WayForPay format
        var webhookRequest = new WebhookRequest(
            merchantAccount,
            orderReference,
            800,  // amount
            "UAH",
            "authCode123",
            "4111****1111",
            status,
            1100,
            generateSignature(...)
        );
        
        restTemplate.postForEntity("/billing/webhook", webhookRequest, String.class);
    }
}
```

---

## User Flow Scenarios

| Scenario | Steps | Expected Result |
|----------|-------|-----------------|
| New user booking | Browse → Book → Pay → Complete | User created, consultation booked |
| Existing user booking | Login → Book → Pay → Complete | Uses existing account |
| Booking with promocode | Apply code → Book → Pay | Discounted price |
| User cancellation | Book → Cancel in time | Full refund to wallet |
| Therapist cancellation | Therapist cancels | User notified, slot released |

---

## Verification Checklist

- [ ] Can browse therapist list
- [ ] Can view therapist profile
- [ ] Can access booking page
- [ ] Time slots display correctly
- [ ] Booking form validates
- [ ] Checkout creates correctly
- [ ] Payment webhook processes
- [ ] User created/found
- [ ] Consultation created
- [ ] Slot marked as booked
- [ ] Wallet operations recorded
- [ ] Notifications sent

---

## Next
Proceed to **8.3: Performance Optimization**

