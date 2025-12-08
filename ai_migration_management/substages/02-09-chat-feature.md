# Sub-Stage 2.9: Chat Feature

## Goal
Implement chat functionality between therapists and their clients.

---

## Endpoints

| Method | Route | Handler | Description |
|--------|-------|---------|-------------|
| GET | /therapist/chat | `chatIndex()` | Chat list page |
| GET | /therapist/chat/{userId} | `chatWithUser()` | Conversation view |
| GET | /therapist/chat/{userId}/messages | `getMessages()` | AJAX: messages |
| POST | /therapist/chat/{userId}/send | `sendMessage()` | AJAX: send msg |
| GET | /therapist/chat/new | `newMessages()` | New messages page |
| GET | /therapist/chat/unread | `getUnreadCount()` | AJAX: unread count |

---

## Controller

```java
@Controller
@RequestMapping("/therapist/chat")
public class TherapistChatController {
    
    private final GetConversationsUseCase getConversationsUseCase;
    private final GetMessagesUseCase getMessagesUseCase;
    private final SendMessageUseCase sendMessageUseCase;
    
    @GetMapping
    public String chatIndex(Model model, @AuthenticationPrincipal TherapistUserDetails user) {
        var conversations = getConversationsUseCase.execute(user.getId());
        model.addAttribute("conversations", conversations);
        return "therapist/chat/index";
    }
    
    @GetMapping("/{userId}")
    public String chatWithUser(@PathVariable Long userId, Model model,
                              @AuthenticationPrincipal TherapistUserDetails user) {
        var messages = getMessagesUseCase.execute(user.getId(), userId, 0);
        model.addAttribute("messages", messages);
        model.addAttribute("userId", userId);
        return "therapist/chat/conversation";
    }
    
    @GetMapping("/{userId}/messages")
    @ResponseBody
    public MessagesResponse getMessages(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Long afterId,
            @AuthenticationPrincipal TherapistUserDetails user) {
        return getMessagesUseCase.execute(user.getId(), userId, page, afterId);
    }
    
    @PostMapping("/{userId}/send")
    @ResponseBody
    public MessageDto sendMessage(
            @PathVariable Long userId,
            @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal TherapistUserDetails user) {
        var command = new SendMessageCommand(user.getId(), userId, request.content(), SenderType.PSYCHOLOGIST);
        return sendMessageUseCase.execute(command);
    }
    
    @GetMapping("/unread")
    @ResponseBody
    public UnreadCountResponse getUnreadCount(@AuthenticationPrincipal TherapistUserDetails user) {
        int count = getConversationsUseCase.getUnreadCount(user.getId());
        return new UnreadCountResponse(count);
    }
}
```

---

## DTOs

```java
public record ConversationDto(
    Long userId,
    String userName,
    String lastMessage,
    LocalDateTime lastMessageTime,
    int unreadCount
) {}

public record MessageDto(
    Long id,
    String content,
    SenderType sender,  // USER or PSYCHOLOGIST
    LocalDateTime sentAt,
    boolean read
) {}

public record MessagesResponse(
    List<MessageDto> messages,
    boolean hasMore,
    Long lastMessageId
) {}
```

---

## Chat Entity (Shared)

```java
@Entity
@Table(name = "chat_message")
public class ChatMessage {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Therapist therapist;
    
    @Enumerated(EnumType.STRING)
    private SenderType senderType;  // USER, PSYCHOLOGIST
    
    @Enumerated(EnumType.STRING)
    private MessageStatus status;   // UNREAD, READ
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private LocalDateTime sentAt;
}
```

---

## JavaScript Features

- Load messages via AJAX
- Poll for new messages (every 5s)
- Send message without page reload
- Mark messages as read when viewed
- Unread badge in sidebar

---

## Verification
- [ ] Conversation list shows all users with messages
- [ ] Can view message history
- [ ] Can send new messages
- [ ] Messages marked read when viewed
- [ ] Unread count updates in real-time
- [ ] Polling works for new messages

---

## Next
Proceed to **2.10: Settings & Payments**

