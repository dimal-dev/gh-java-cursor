# Sub-Stage 4.5: User Dashboard

## Goal
Implement the user dashboard showing upcoming consultations and status messages.

---

## Endpoint
`GET /user/` or `GET /user/dashboard`

---

## Controller

```java
@Controller
@RequestMapping("/user")
public class UserDashboardController {
    
    private final GetUserDashboardUseCase dashboardUseCase;
    
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model, @AuthenticationPrincipal GoodHelpUserDetails user) {
        var dashboard = dashboardUseCase.execute(user.getId());
        model.addAttribute("dashboard", dashboard);
        return "user/dashboard";
    }
}
```

---

## Dashboard Content

### Upcoming Consultations
- List of scheduled consultations
- Date, time, therapist name
- "Cancel" button (if cancellable)
- "Join" button (when time comes)

### Quick Actions
- "Book New Consultation" button
- "Chat with Therapist" link

### Status Messages
- "Set your name" reminder if not set
- "Read the rules" reminder
- Wallet balance display

---

## Dashboard DTO

```java
public record UserDashboardDto(
    String userName,
    boolean profileComplete,
    boolean rulesRead,
    int walletBalance,
    String walletCurrency,
    ConsultationDto nextConsultation,
    List<ConsultationDto> upcomingConsultations,
    int unreadMessages
) {}

public record ConsultationDto(
    Long id,
    String therapistName,
    String therapistPhotoUrl,
    LocalDateTime scheduledAt,
    String scheduledAtFormatted,
    boolean canBeCancelled,
    String status
) {}
```

---

## Template

```html
<!-- user/dashboard.html -->
<main layout:fragment="content">
    <h1 th:text="#{user.dashboard.welcome(${dashboard.userName ?: 'User'})}">
        Welcome back, User
    </h1>
    
    <!-- Profile reminder -->
    <div th:if="${!dashboard.profileComplete}" class="alert info">
        <span th:text="#{user.dashboard.setNameReminder}">Please set your name</span>
        <a th:href="@{/user/settings}" th:text="#{user.dashboard.goToSettings}">Go to settings</a>
    </div>
    
    <!-- Next consultation -->
    <section th:if="${dashboard.nextConsultation != null}" class="next-consultation">
        <h2 th:text="#{user.dashboard.nextConsultation}">Your Next Session</h2>
        <div class="consultation-card highlight">
            <img th:src="${dashboard.nextConsultation.therapistPhotoUrl}" alt="">
            <div class="info">
                <h3 th:text="${dashboard.nextConsultation.therapistName}">Name</h3>
                <p th:text="${dashboard.nextConsultation.scheduledAtFormatted}">Date Time</p>
            </div>
            <div class="actions">
                <button th:if="${dashboard.nextConsultation.canBeCancelled}"
                        th:attr="data-id=${dashboard.nextConsultation.id}"
                        class="btn-cancel" th:text="#{user.dashboard.cancel}">Cancel</button>
            </div>
        </div>
    </section>
    
    <!-- All upcoming -->
    <section class="upcoming-consultations">
        <h2 th:text="#{user.dashboard.upcoming}">Upcoming Sessions</h2>
        <div th:if="${#lists.isEmpty(dashboard.upcomingConsultations)}" class="empty-state">
            <p th:text="#{user.dashboard.noUpcoming}">No upcoming sessions</p>
            <a th:href="@{/therapist-list}" class="btn-primary" 
               th:text="#{user.dashboard.bookNew}">Book a Session</a>
        </div>
        <div th:each="c : ${dashboard.upcomingConsultations}" class="consultation-card">
            <!-- Similar structure -->
        </div>
    </section>
    
    <!-- Wallet -->
    <section class="wallet-summary">
        <h3 th:text="#{user.dashboard.wallet}">Wallet Balance</h3>
        <span class="balance" th:text="${dashboard.walletBalance + ' ' + dashboard.walletCurrency}">
            0 UAH
        </span>
    </section>
</main>
```

---

## PHP Reference
- `original_php_project/src/User/Controller/IndexController.php`
- `original_php_project/src/User/Resources/templates/pages/index.html.twig`

---

## Verification
- [ ] Dashboard loads correctly
- [ ] Upcoming consultations displayed
- [ ] Profile reminder shows when needed
- [ ] Cancel button works
- [ ] Empty state shown when no consultations

---

## Next
Proceed to **4.6: Chat**

