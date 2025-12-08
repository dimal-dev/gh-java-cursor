# Sub-Stage 7.1: Telegram Service

## Goal
Implement Telegram Bot integration for sending notifications.

---

## Telegram Service

**Location:** `com.goodhelp.notification.infrastructure.telegram/`

```java
@Service
@Slf4j
public class TelegramService {
    
    @Value("${telegram.bot-token}")
    private String botToken;
    
    private static final String API_URL = "https://api.telegram.org/bot%s/%s";
    
    private final RestTemplate restTemplate;
    
    public void sendMessage(String chatId, String message) {
        String url = String.format(API_URL, botToken, "sendMessage");
        
        Map<String, Object> body = Map.of(
            "chat_id", chatId,
            "text", message,
            "parse_mode", "HTML"
        );
        
        try {
            restTemplate.postForObject(url, body, String.class);
            log.info("Telegram message sent to chat: {}", chatId);
        } catch (Exception e) {
            log.error("Failed to send Telegram message to {}: {}", chatId, e.getMessage());
        }
    }
    
    public void sendMessageWithMarkdown(String chatId, String message) {
        String url = String.format(API_URL, botToken, "sendMessage");
        
        Map<String, Object> body = Map.of(
            "chat_id", chatId,
            "text", message,
            "parse_mode", "Markdown"
        );
        
        try {
            restTemplate.postForObject(url, body, String.class);
        } catch (Exception e) {
            log.error("Failed to send Telegram message: {}", e.getMessage());
        }
    }
}
```

---

## Notification Templates

```java
@Service
public class TelegramNotificationFormatter {
    
    private final MessageSource messageSource;
    
    public String formatNewBooking(ConsultationInfo info, Locale locale) {
        return String.format("""
            🆕 <b>New Booking!</b>
            
            Client: %s
            Date: %s
            Time: %s
            Type: %s
            
            Check your schedule for details.
            """,
            info.clientName(),
            info.date(),
            info.time(),
            info.type()
        );
    }
    
    public String formatCancellation(ConsultationInfo info, Locale locale) {
        return String.format("""
            ❌ <b>Booking Cancelled</b>
            
            Client: %s cancelled their session
            Date: %s
            Time: %s
            
            The time slot is now available.
            """,
            info.clientName(),
            info.date(),
            info.time()
        );
    }
    
    public String formatUpcomingReminder(ConsultationInfo info) {
        return String.format("""
            ⏰ <b>Upcoming Session Reminder</b>
            
            You have a session in 1 hour:
            Client: %s
            Time: %s
            """,
            info.clientName(),
            info.time()
        );
    }
}
```

---

## Configuration

```yaml
# application.yml
telegram:
  bot-token: ${TELEGRAM_BOT_TOKEN}
  bot-username: ${TELEGRAM_BOT_USERNAME:GoodHelpBot}
```

---

## PHP Reference
- `original_php_project/src/Notification/Service/TelegramSender.php`
- `original_php_project/src/Psiholog/Service/TelegramNotifier.php`

---

## Verification
- [ ] Messages send successfully
- [ ] HTML formatting works
- [ ] Errors logged gracefully

---

## Next
Proceed to **7.2: Telegram Webhook**

