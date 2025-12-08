# Sub-Stage 7.3: Email Service

## Goal
Implement email sending for auto-login links and notifications.

---

## Email Service

```java
@Service
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    
    @Value("${app.email.from}")
    private String fromAddress;
    
    @Value("${app.email.from-name}")
    private String fromName;
    
    @Async
    public void sendAutoLoginLink(String to, String name, String loginUrl, String type) {
        String templateName = switch (type) {
            case "user" -> "email/user-login";
            case "therapist" -> "email/therapist-login";
            case "staff" -> "email/staff-login";
            default -> "email/login";
        };
        
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("loginUrl", loginUrl);
        
        String html = templateEngine.process(templateName, context);
        sendHtmlEmail(to, "Your Login Link - GoodHelp", html);
    }
    
    @Async
    public void sendBookingConfirmation(String to, BookingConfirmationData data) {
        Context context = new Context();
        context.setVariable("data", data);
        
        String html = templateEngine.process("email/booking-confirmation", context);
        sendHtmlEmail(to, "Booking Confirmed - GoodHelp", html);
    }
    
    @Async
    public void sendCancellationNotice(String to, CancellationData data) {
        Context context = new Context();
        context.setVariable("data", data);
        
        String html = templateEngine.process("email/cancellation-notice", context);
        sendHtmlEmail(to, "Session Cancelled - GoodHelp", html);
    }
    
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(new InternetAddress(fromAddress, fromName));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Email sent to: {}, subject: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
```

---

## Email Templates

**Location:** `templates/email/`

```html
<!-- email/user-login.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <div style="max-width: 600px; margin: 0 auto; font-family: sans-serif;">
        <h2>Hello<span th:if="${name}" th:text="', ' + ${name}"></span>!</h2>
        
        <p>Click the button below to sign in to your GoodHelp account:</p>
        
        <p style="text-align: center; margin: 30px 0;">
            <a th:href="${loginUrl}" 
               style="background: #EF9665; color: white; padding: 14px 28px; 
                      text-decoration: none; border-radius: 8px; display: inline-block;">
                Sign In
            </a>
        </p>
        
        <p style="color: #666; font-size: 14px;">
            This link will expire in 24 hours. If you didn't request this email, 
            you can safely ignore it.
        </p>
        
        <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">
        
        <p style="color: #999; font-size: 12px;">
            GoodHelp - Professional Psychology Consultations
        </p>
    </div>
</body>
</html>
```

---

## Configuration

```yaml
# application.yml
spring:
  mail:
    host: ${MAIL_HOST:smtp.gmail.com}
    port: ${MAIL_PORT:587}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

app:
  email:
    from: ${MAIL_FROM:noreply@goodhelp.com}
    from-name: GoodHelp
```

---

## PHP Reference
- `original_php_project/src/Notification/Service/EmailSender.php`

---

## Verification
- [ ] Auto-login emails sent
- [ ] HTML templates render
- [ ] Async sending works
- [ ] Booking confirmations sent
- [ ] Cancellation notices sent

---

## Next
Proceed to **7.4: Kafka Integration**

