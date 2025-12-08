# Stage 7: Notification Module

## Overview

This stage implements the notification system including Telegram integration, email sending, and scheduled notifications via Kafka.

**Estimated Sub-stages:** 5  
**Dependencies:** Stage 1, Stage 2  
**Next Stage:** Stage 8 - Integration & Polish

---

## PHP Source Reference

| Type | Path |
|------|------|
| Controllers | `original_php_project/src/Notification/Controller/` |
| Services | `original_php_project/src/Notification/Service/` |
| Commands | `original_php_project/src/Notification/Command/` |

---

## Sub-Stage Summary

| Sub-Stage | Description | Status |
|-----------|-------------|--------|
| 7.1 | Telegram service | NOT_STARTED |
| 7.2 | Telegram webhook controller | NOT_STARTED |
| 7.3 | Email service | NOT_STARTED |
| 7.4 | Kafka integration | NOT_STARTED |
| 7.5 | Scheduled notifications | NOT_STARTED |

---

## Telegram Integration

### Webhook Endpoint
`POST /notification/telegram/webhook`

### Features
- Receive messages from Telegram
- Link therapist accounts to Telegram
- Send notifications to therapists

### Therapist Setup Flow
1. Therapist clicks setup link in settings
2. Link contains token: `therapist_setup_{autologinToken}`
3. Webhook receives message with token
4. Extract token, find therapist
5. Save Telegram chat ID to therapist settings

### Notification Types
- New consultation booked
- Consultation cancelled
- New chat messages

---

## Email Service

### Email Types
- Auto-login links (users)
- Auto-login links (therapists)
- Booking confirmations
- Cancellation notices

### Implementation
- Spring Mail
- Thymeleaf email templates
- Async sending

---

## Kafka Integration

### Topics
- `consultation-events` - Booking, cancellation events
- `notification-requests` - Notification requests
- `email-queue` - Email sending queue

### Producers
- Billing module (consultation created)
- User module (cancellation)
- Therapist module (cancellation)

### Consumers
- Notification service (process notifications)
- Email service (send emails)
- Telegram service (send Telegram messages)

---

## Scheduled Tasks

Replace PHP cron commands with Spring `@Scheduled`:

```java
@Scheduled(fixedRate = 60000) // Every minute
public void checkUpcomingConsultations() {
    // Notify therapists of upcoming consultations
}

@Scheduled(cron = "0 0 9 * * *") // Daily at 9 AM
public void sendDailySummary() {
    // Send daily summary to therapists
}
```

---

## Verification Checklist

- [ ] Telegram webhook receives messages
- [ ] Therapist can link Telegram
- [ ] Notifications sent on booking
- [ ] Email auto-login links work
- [ ] Kafka producers send events
- [ ] Kafka consumers process events
- [ ] Scheduled tasks run correctly

