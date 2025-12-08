# Stage 6: Billing Module

## Overview

This stage implements the payment processing system, including WayForPay integration, webhook handling, wallet management, and order processing.

**Estimated Sub-stages:** 7  
**Dependencies:** Stage 1, Stage 2, Stage 3, Stage 4  
**Next Stage:** Stage 7 - Notification Module

---

## PHP Source Reference

| Type | Path |
|------|------|
| Controllers | `original_php_project/src/Billing/Controller/` |
| Entities | `original_php_project/src/Billing/Entity/` |
| Services | `original_php_project/src/Billing/Service/` |
| Routes | `original_php_project/config/routes/billing.yaml` |

---

## Sub-Stage Summary

| Sub-Stage | Description | Status |
|-----------|-------------|--------|
| 6.1 | Billing entities | NOT_STARTED |
| 6.2 | Repositories | NOT_STARTED |
| 6.3 | WayForPay signature service | NOT_STARTED |
| 6.4 | Webhook controller | NOT_STARTED |
| 6.5 | Order processing | NOT_STARTED |
| 6.6 | Wallet operations | NOT_STARTED |
| 6.7 | Promocode handling | NOT_STARTED |

---

## Entities

| Entity | Purpose |
|--------|---------|
| `Order` | Payment order |
| `Checkout` | Checkout session |
| `OrderLog` | Raw webhook logs |
| `OrderPsihologSchedule` | Link order to schedule |
| `UserWallet` | User balance |
| `UserWalletOperation` | Wallet transactions |
| `Currency` | Currency enum |

---

## WayForPay Integration

### Webhook Endpoint
`POST /billing/webhook`

### Request Validation
```java
// Signature fields for request validation
String[] REQUEST_FIELDS = {
    "merchantAccount",
    "orderReference",
    "amount",
    "currency",
    "authCode",
    "cardPan",
    "transactionStatus",
    "reasonCode"
};
```

### Response Format
```json
{
    "orderReference": "...",
    "status": "accept",
    "time": 1234567890,
    "signature": "..."
}
```

### Transaction States
- `Approved` → Mark order approved, create consultation
- `Pending` → Mark order pending
- `Declined` → Mark order failed

---

## Order Processing Flow

1. **Checkout Created** (from Landing)
   - Contains price, schedule, user info
   - Generates unique slug

2. **Order Created** (when payment initiated)
   - Links to checkout
   - Contains payment details

3. **Webhook Received**
   - Validate signature
   - Log raw request
   - Process based on status

4. **Approved Transaction**
   - Update order state
   - Find/create user
   - Create user wallet if needed
   - Add funds to wallet
   - Create consultation
   - Book schedule slot
   - Mark promocode used

---

## Services

| PHP Service | Java Service | Purpose |
|-------------|--------------|---------|
| `WayForPaySignatureMaker` | `WayForPayService` | Generate/verify signatures |
| `UserWalletManager` | `WalletService` | Wallet operations |
| N/A | `OrderService` | Order management |
| N/A | `CheckoutService` | Checkout operations |

---

## Wallet Operations

Types:
- `ADD` - Add funds (purchase)
- `SUBTRACT` - Deduct funds (consultation payment)

Reason Types:
- `PURCHASE` - From payment
- `CONSULTATION` - For consultation
- `REFUND` - Refund operation

---

## Verification Checklist

- [ ] Webhook receives and logs requests
- [ ] Signature validation works
- [ ] Approved payments create users
- [ ] Wallet receives funds
- [ ] Consultations are created
- [ ] Schedule slots are booked
- [ ] Promocodes are marked used
- [ ] Failed payments are handled
- [ ] Response format is correct

