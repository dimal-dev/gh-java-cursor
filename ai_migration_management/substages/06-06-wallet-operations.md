# Sub-Stage 6.6: Wallet Operations

## Goal
Implement user wallet management service.

---

## Wallet Service

```java
@Service
@Transactional
public class UserWalletService {
    
    private final UserWalletRepository walletRepository;
    private final UserWalletOperationRepository operationRepository;
    
    public UserWallet getOrCreateWallet(Long userId, String currency) {
        return walletRepository.findByUserId(userId)
            .orElseGet(() -> {
                UserWallet wallet = new UserWallet();
                wallet.setUserId(userId);
                wallet.setBalance(0);
                wallet.setCurrency(currency);
                return walletRepository.save(wallet);
            });
    }
    
    public void addFunds(Long userId, int amount, String currency, 
                        OperationReason reason, Long reasonId) {
        UserWallet wallet = getOrCreateWallet(userId, currency);
        wallet.addFunds(amount);
        walletRepository.save(wallet);
        
        recordOperation(wallet, amount, OperationType.ADD, reason, reasonId);
    }
    
    public void deductFunds(Long userId, int amount, String currency,
                           OperationReason reason, Long reasonId) {
        UserWallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new WalletNotFoundException(userId));
        
        wallet.deductFunds(amount);
        walletRepository.save(wallet);
        
        recordOperation(wallet, amount, OperationType.SUBTRACT, reason, reasonId);
    }
    
    public void refund(Long userId, int amount, String currency, Long consultationId) {
        addFunds(userId, amount, currency, OperationReason.REFUND, consultationId);
    }
    
    public int getBalance(Long userId) {
        return walletRepository.findByUserId(userId)
            .map(UserWallet::getBalance)
            .orElse(0);
    }
    
    public List<WalletOperationDto> getOperationHistory(Long userId, int page) {
        UserWallet wallet = walletRepository.findByUserId(userId).orElse(null);
        if (wallet == null) return List.of();
        
        return operationRepository.findByWalletId(wallet.getId(), PageRequest.of(page, 20))
            .stream()
            .map(WalletOperationDto::from)
            .toList();
    }
    
    private void recordOperation(UserWallet wallet, int amount, 
                                 OperationType type, OperationReason reason, Long reasonId) {
        UserWalletOperation operation = new UserWalletOperation();
        operation.setWallet(wallet);
        operation.setAmount(amount);
        operation.setCurrency(wallet.getCurrency());
        operation.setType(type);
        operation.setReasonType(reason);
        operation.setReasonId(reasonId);
        operation.setCreatedAt(LocalDateTime.now());
        operationRepository.save(operation);
    }
}
```

---

## Wallet Operation DTO

```java
public record WalletOperationDto(
    Long id,
    int amount,
    String currency,
    String type,
    String reason,
    LocalDateTime createdAt,
    String formattedAmount
) {
    public static WalletOperationDto from(UserWalletOperation op) {
        String sign = op.getType() == OperationType.ADD ? "+" : "-";
        return new WalletOperationDto(
            op.getId(),
            op.getAmount(),
            op.getCurrency(),
            op.getType().name(),
            op.getReasonType().name(),
            op.getCreatedAt(),
            sign + formatMoney(op.getAmount(), op.getCurrency())
        );
    }
}
```

---

## PHP Reference
- `original_php_project/src/Billing/Repository/UserWalletManager.php`

---

## Verification
- [ ] Wallet created on first use
- [ ] Funds added correctly
- [ ] Funds deducted correctly
- [ ] Operations recorded
- [ ] Balance calculated correctly
- [ ] Refunds work

---

## Next
Proceed to **6.7: Promocode Handling**

