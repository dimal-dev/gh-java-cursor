# Sub-Stage 5.5: Staff Dashboard

## Goal
Implement staff dashboard with quick stats and navigation.

---

## Endpoint
`GET /staff/` or `GET /staff/dashboard`

---

## Controller

```java
@Controller
@RequestMapping("/staff")
public class StaffDashboardController {
    
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        var stats = getDashboardStatsUseCase.execute();
        model.addAttribute("stats", stats);
        return "staff/dashboard";
    }
}
```

---

## Dashboard Stats

```java
public record StaffDashboardDto(
    int totalTherapists,
    int activeTherapists,
    int totalUsers,
    int totalConsultations,
    int blogPostsCount,
    int pendingPayouts
) {}
```

---

## Template

```html
<!-- staff/dashboard.html -->
<main layout:fragment="content">
    <h1>Staff Dashboard</h1>
    
    <div class="stats-grid">
        <div class="stat-card">
            <span class="number" th:text="${stats.activeTherapists}">0</span>
            <span class="label">Active Therapists</span>
        </div>
        <div class="stat-card">
            <span class="number" th:text="${stats.totalUsers}">0</span>
            <span class="label">Total Users</span>
        </div>
        <div class="stat-card">
            <span class="number" th:text="${stats.totalConsultations}">0</span>
            <span class="label">Consultations</span>
        </div>
        <div class="stat-card">
            <span class="number" th:text="${stats.blogPostsCount}">0</span>
            <span class="label">Blog Posts</span>
        </div>
    </div>
    
    <div class="quick-actions">
        <a th:href="@{/staff/therapists}" class="action-card">Manage Therapists</a>
        <a th:href="@{/staff/blog}" class="action-card">Manage Blog</a>
        <a th:href="@{/staff/payouts}" class="action-card"
           sec:authorize="hasRole('STAFF_SUPERUSER')">Payouts</a>
    </div>
</main>
```

---

## Verification
- [ ] Dashboard loads
- [ ] Stats display correctly
- [ ] Quick actions link correctly
- [ ] Superuser-only actions hidden for regular staff

---

## Next
Proceed to **5.6: Therapist List**

