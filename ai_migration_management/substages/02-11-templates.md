# Sub-Stage 2.11: Therapist Templates

## Goal
Create all Thymeleaf templates for therapist cabinet, matching original PHP/Twig design.

---

## Template Structure

```
templates/therapist/
├── layout/
│   └── main.html           # Base layout with sidebar
├── fragments/
│   ├── sidebar.html        # Navigation sidebar
│   ├── topmenu.html        # Top navigation
│   └── footer.html         # Footer
├── login.html
├── schedule.html
├── schedule-settings.html
├── schedule-settings-week.html
├── clients.html
├── user-notes.html
├── settings.html
├── chat/
│   ├── index.html          # Conversation list
│   └── conversation.html   # Message view
└── payments/
    ├── index.html          # Earnings overview
    └── history.html        # Payment history
```

---

## Main Layout

```html
<!-- layout/main.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" 
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title layout:title-pattern="$CONTENT_TITLE - GoodHelp">GoodHelp</title>
    <link rel="stylesheet" th:href="@{/css/therapist/main.css}">
</head>
<body>
    <div class="cabinet-wrapper">
        <aside class="sidebar" th:replace="~{therapist/fragments/sidebar :: sidebar}"></aside>
        <main class="content">
            <div th:replace="~{therapist/fragments/topmenu :: topmenu}"></div>
            <div class="page-content" layout:fragment="content"></div>
        </main>
    </div>
    <script th:src="@{/js/therapist/main.js}"></script>
    <th:block layout:fragment="scripts"></th:block>
</body>
</html>
```

---

## Sidebar Fragment

```html
<!-- fragments/sidebar.html -->
<aside class="sidebar" th:fragment="sidebar">
    <div class="sidebar-logo">
        <a th:href="@{/therapist/schedule}">GoodHelp</a>
    </div>
    <nav class="sidebar-nav">
        <a th:href="@{/therapist/schedule}" 
           th:classappend="${#strings.startsWith(#httpServletRequest.requestURI, '/therapist/schedule')} ? 'active'">
            <span th:text="#{psiholog.nav.schedule}">Schedule</span>
        </a>
        <a th:href="@{/therapist/clients}"
           th:classappend="${#strings.startsWith(#httpServletRequest.requestURI, '/therapist/clients')} ? 'active'">
            <span th:text="#{psiholog.nav.clients}">Clients</span>
        </a>
        <a th:href="@{/therapist/chat}"
           th:classappend="${#strings.startsWith(#httpServletRequest.requestURI, '/therapist/chat')} ? 'active'">
            <span th:text="#{psiholog.nav.chat}">Chat</span>
            <span th:if="${unreadCount > 0}" class="badge" th:text="${unreadCount}"></span>
        </a>
        <a th:href="@{/therapist/payments}"
           th:classappend="${#strings.startsWith(#httpServletRequest.requestURI, '/therapist/payments')} ? 'active'">
            <span th:text="#{psiholog.nav.payments}">Payments</span>
        </a>
        <a th:href="@{/therapist/settings}"
           th:classappend="${#httpServletRequest.requestURI == '/therapist/settings'} ? 'active'">
            <span th:text="#{psiholog.nav.settings}">Settings</span>
        </a>
    </nav>
    <div class="sidebar-footer">
        <form th:action="@{/therapist/logout}" method="post">
            <button type="submit" th:text="#{psiholog.nav.logout}">Logout</button>
        </form>
    </div>
</aside>
```

---

## Page Template Example

```html
<!-- schedule.html -->
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{therapist/layout/main}">
<head>
    <title th:text="#{psiholog.schedule.title}">Schedule</title>
</head>
<body>
    <div layout:fragment="content">
        <h1 th:text="#{psiholog.schedule.heading}">My Schedule</h1>
        
        <div class="schedule-controls">
            <button id="prev-week" th:text="#{common.previousWeek}">← Previous</button>
            <span id="current-week"></span>
            <button id="next-week" th:text="#{common.nextWeek}">Next →</button>
        </div>
        
        <div id="schedule-grid" class="schedule-grid">
            <!-- Populated by JavaScript -->
        </div>
    </div>
    
    <th:block layout:fragment="scripts">
        <script th:src="@{/js/therapist/schedule.js}"></script>
    </th:block>
</body>
</html>
```

---

## PHP Template Reference

| PHP Template | Java Template |
|--------------|---------------|
| `pages/schedule.html.twig` | `schedule.html` |
| `pages/clients.html.twig` | `clients.html` |
| `pages/chat/chat.html.twig` | `chat/index.html` |
| `pages/settings.html.twig` | `settings.html` |
| `layout/main.html.twig` | `layout/main.html` |

---

## i18n Integration

All text uses message keys:
```html
<span th:text="#{psiholog.schedule.available}">Available</span>
```

Message files already set up in Stage 1.

---

## Verification
- [x] All pages render without errors
- [x] Layout applied consistently
- [x] Sidebar navigation works
- [x] Active menu item highlighted
- [x] i18n messages display correctly
- [x] Responsive design works

## Implementation Summary (Completed 2024-12-08)

### Templates Created:
**Layout:**
- `layout/therapist.html` - Main layout with sidebar navigation

**Pages:**
- `login.html` - Therapist login with language picker
- `schedule.html` - Upcoming consultations list with AJAX loading
- `schedule-settings.html` - Schedule settings with week selector
- `clients.html` - Client list with search and pagination
- `user-notes.html` - Client notes form
- `settings.html` - Timezone settings and Telegram integration
- `chat/chat.html` - Chat conversation view
- `chat/chat-messages.html` - Message fragment for AJAX
- `chat/new-messages.html` - New messages list
- `payments/index.html` - Current payout summary
- `payments/history.html` - Payout history table

**Fragments:**
- `fragments/schedule-settings-week.html` - Week grid fragment for AJAX loading

### Static Assets:
- `css/therapist/cabinet.css` - Main stylesheet
- `js/therapist/cabinet.js` - Common JavaScript (sidebar toggle, tooltips, AJAX forms)

---

## Next
Proceed to **2.12: Styles**

