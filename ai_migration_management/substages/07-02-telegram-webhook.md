# Sub-Stage 7.2: Telegram Webhook Controller

## Goal
Implement webhook to receive messages from Telegram and link therapist accounts.

---

## Endpoint
`POST /notification/telegram/webhook`

---

## Controller

```java
@RestController
@RequestMapping("/notification/telegram")
@Slf4j
public class TelegramWebhookController {
    
    private final LinkTelegramUseCase linkTelegramUseCase;
    private final TelegramService telegramService;
    
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody TelegramUpdate update) {
        log.info("Received Telegram update: {}", update.updateId());
        
        if (update.message() == null || update.message().text() == null) {
            return ResponseEntity.ok("OK");
        }
        
        String text = update.message().text();
        String chatId = String.valueOf(update.message().chat().id());
        
        // Handle /start command with token
        if (text.startsWith("/start")) {
            handleStartCommand(text, chatId);
        }
        
        return ResponseEntity.ok("OK");
    }
    
    private void handleStartCommand(String text, String chatId) {
        // Expected format: /start psiholog_setup_{token}
        String[] parts = text.split(" ");
        if (parts.length < 2) {
            telegramService.sendMessage(chatId, 
                "Welcome! Use the setup link from your cabinet to connect.");
            return;
        }
        
        String payload = parts[1];
        if (payload.startsWith("psiholog_setup_")) {
            String token = payload.substring("psiholog_setup_".length());
            try {
                linkTelegramUseCase.execute(new LinkTelegramCommand(token, chatId));
                telegramService.sendMessage(chatId, 
                    "✅ Your Telegram is now linked! You'll receive notifications here.");
            } catch (Exception e) {
                telegramService.sendMessage(chatId, 
                    "❌ Invalid or expired link. Please get a new link from your cabinet.");
            }
        }
    }
}
```

---

## Link Telegram Use Case

```java
@Service
@Transactional
public class LinkTelegramUseCase {
    
    private final TherapistAuthTokenRepository tokenRepository;
    private final TherapistSettingsRepository settingsRepository;
    
    public void execute(LinkTelegramCommand command) {
        // Find therapist by token
        var token = tokenRepository.findByToken(command.token())
            .orElseThrow(() -> new InvalidTokenException());
        
        // Update settings with chat ID
        var settings = settingsRepository.findByTherapistId(token.getTherapistId())
            .orElseThrow();
        
        settings.setTelegramChatId(command.chatId());
        settingsRepository.save(settings);
    }
}
```

---

## Telegram Update DTO

```java
public record TelegramUpdate(
    @JsonProperty("update_id") long updateId,
    TelegramMessage message
) {}

public record TelegramMessage(
    @JsonProperty("message_id") long messageId,
    String text,
    TelegramChat chat,
    TelegramUser from
) {}

public record TelegramChat(
    long id,
    String type
) {}

public record TelegramUser(
    long id,
    @JsonProperty("first_name") String firstName,
    String username
) {}
```

---

## Webhook Setup

Register webhook with Telegram API:
```
POST https://api.telegram.org/bot{TOKEN}/setWebhook
{
    "url": "https://yourdomain.com/notification/telegram/webhook"
}
```

---

## PHP Reference
- `original_php_project/src/Notification/Controller/Telegram/WebhookController.php`

---

## Verification
- [ ] Webhook receives updates
- [ ] /start command handled
- [ ] Therapist linked successfully
- [ ] Invalid tokens handled
- [ ] Confirmation message sent

---

## Next
Proceed to **7.3: Email Service**

