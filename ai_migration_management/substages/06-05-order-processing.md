# Sub-Stage 6.5: Order Processing

## Goal
Implement the complete order processing flow when payment is approved.

---

## Use Case

```java
@Service
@Transactional
public class ProcessPaymentWebhookUseCase {
    
    private final OrderRepository orderRepository;
    private final CheckoutRepository checkoutRepository;
    private final UserRepository userRepository;
    private final UserWalletService walletService;
    private final ConsultationService consultationService;
    private final ScheduleSlotRepository slotRepository;
    private final PromocodeService promocodeService;
    private final DomainEventPublisher eventPublisher;
    
    public void execute(ProcessWebhookCommand command) {
        // 1. Find order by checkout slug
        Order order = orderRepository.findByCheckoutSlug(command.orderReference())
            .orElseThrow(() -> new OrderNotFoundException(command.orderReference()));
        
        // Handle based on transaction status
        switch (command.transactionStatus()) {
            case "Approved" -> processApproved(order, command);
            case "Pending" -> processPending(order);
            case "Declined", "Expired" -> processFailed(order, command);
        }
    }
    
    private void processApproved(Order order, ProcessWebhookCommand command) {
        // Skip if already processed
        if (order.getState() == OrderState.APPROVED) {
            return;
        }
        
        // 1. Update order state
        order.approve(command.cardPan(), command.cardType(), command.paymentSystem());
        orderRepository.save(order);
        
        // 2. Load checkout
        Checkout checkout = checkoutRepository.findById(order.getCheckoutId())
            .orElseThrow();
        
        // 3. Find or create user
        User user = findOrCreateUser(checkout);
        order.setUser(user);
        
        // 4. Add funds to wallet
        walletService.addFunds(user.getId(), order.getPrice(), order.getCurrency(), 
            OperationReason.PURCHASE, order.getId());
        
        // 5. Create consultation
        UserConsultation consultation = consultationService.create(
            user.getId(),
            checkout.getTherapistId(),
            checkout.getPriceId(),
            checkout.getSlotId()
        );
        order.setUserConsultation(consultation);
        
        // 6. Book schedule slot
        ScheduleSlot slot = slotRepository.findById(checkout.getSlotId()).orElseThrow();
        slot.book(consultation.getId());
        slotRepository.save(slot);
        
        // 7. Deduct from wallet for consultation
        walletService.deductFunds(user.getId(), order.getPrice(), order.getCurrency(),
            OperationReason.CONSULTATION, consultation.getId());
        
        // 8. Mark promocode as used
        if (checkout.getUserPromocodeId() != null) {
            promocodeService.markUsed(checkout.getUserPromocodeId());
        }
        
        // 9. Publish event
        eventPublisher.publish(new PaymentCompletedEvent(
            order.getId(), 
            consultation.getId(),
            user.getId(),
            checkout.getTherapistId()
        ));
        
        orderRepository.save(order);
    }
    
    private User findOrCreateUser(Checkout checkout) {
        if (checkout.getUserId() != null) {
            return userRepository.findById(checkout.getUserId()).orElseThrow();
        }
        
        // Create new user
        User user = new User();
        user.setEmail(checkout.getEmail());
        user.setFullName(checkout.getName());
        user.setTimezone(checkout.getTimezone());
        user.setLocale(checkout.getLocale());
        return userRepository.save(user);
    }
    
    private void processPending(Order order) {
        order.markPending();
        orderRepository.save(order);
    }
    
    private void processFailed(Order order, ProcessWebhookCommand command) {
        order.fail(command.reason(), String.valueOf(command.reasonCode()));
        orderRepository.save(order);
    }
}
```

---

## Processing Flow

```
1. Webhook received
2. Order found by checkout slug
3. For APPROVED status:
   a. Update order → APPROVED
   b. Find/create user account
   c. Create/update user wallet
   d. Add purchase funds to wallet
   e. Create consultation record
   f. Book schedule slot
   g. Deduct consultation cost from wallet
   h. Mark promocode as used (if any)
   i. Publish PaymentCompletedEvent
4. For PENDING: Update order → PENDING
5. For FAILED: Update order → FAILED with reason
```

---

## PHP Reference
- Logic spread across `WayForPayController.php` and services

---

## Verification
- [ ] Approved payments create user
- [ ] Wallet operations recorded
- [ ] Consultation created
- [ ] Slot marked as booked
- [ ] Promocode marked used
- [ ] Events published
- [ ] Failed payments handled

---

## Next
Proceed to **6.6: Wallet Operations**

