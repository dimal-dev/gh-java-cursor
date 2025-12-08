# Sub-Stage 4.9: User Templates

## Goal
Create all Thymeleaf templates for user cabinet.

---

## Template Structure

```
templates/user/
├── layout/
│   └── main.html
├── fragments/
│   ├── sidebar.html
│   └── header.html
├── login.html
├── dashboard.html
├── consultations.html
├── settings.html
├── rules.html
├── chat/
│   ├── index.html
│   └── conversation.html
└── error/
    └── access-denied.html
```

---

## Layout

```html
<!-- layout/main.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title layout:title-pattern="$CONTENT_TITLE - GoodHelp">User Cabinet</title>
    <link rel="stylesheet" th:href="@{/css/user/main.css}">
</head>
<body>
    <div class="user-cabinet">
        <aside th:replace="~{user/fragments/sidebar :: sidebar}"></aside>
        <main class="content">
            <div th:replace="~{user/fragments/header :: header}"></div>
            <div class="page-content" layout:fragment="content"></div>
        </main>
    </div>
    <script th:src="@{/js/user/main.js}"></script>
    <th:block layout:fragment="scripts"></th:block>
</body>
</html>
```

---

## Sidebar

```html
<!-- fragments/sidebar.html -->
<aside th:fragment="sidebar" class="sidebar">
    <div class="sidebar-logo">
        <a th:href="@{/}">
            <img th:src="@{/img/logo.svg}" alt="GoodHelp">
        </a>
    </div>
    
    <nav class="sidebar-nav">
        <a th:href="@{/user/}" 
           th:classappend="${#httpServletRequest.requestURI == '/user/' || #httpServletRequest.requestURI == '/user'} ? 'active'">
            <span th:text="#{user.nav.dashboard}">Dashboard</span>
        </a>
        <a th:href="@{/user/chat}"
           th:classappend="${#strings.startsWith(#httpServletRequest.requestURI, '/user/chat')} ? 'active'">
            <span th:text="#{user.nav.chat}">Messages</span>
            <span th:if="${unreadMessages > 0}" class="badge" th:text="${unreadMessages}"></span>
        </a>
        <a th:href="@{/user/settings}"
           th:classappend="${#httpServletRequest.requestURI == '/user/settings'} ? 'active'">
            <span th:text="#{user.nav.settings}">Settings</span>
        </a>
        <a th:href="@{/user/rules}"
           th:classappend="${#httpServletRequest.requestURI == '/user/rules'} ? 'active'">
            <span th:text="#{user.nav.rules}">Rules</span>
        </a>
    </nav>
    
    <div class="sidebar-footer">
        <a th:href="@{/therapist-list}" class="btn-book" th:text="#{user.nav.bookNew}">
            Book Session
        </a>
        <form th:action="@{/user/logout}" method="post">
            <button type="submit" th:text="#{user.nav.logout}">Logout</button>
        </form>
    </div>
</aside>
```

---

## i18n Keys

```properties
user.nav.dashboard=My Sessions
user.nav.chat=Messages
user.nav.settings=Settings
user.nav.rules=Booking Rules
user.nav.bookNew=Book New Session
user.nav.logout=Sign Out
```

---

## PHP Reference
- `original_php_project/src/User/Resources/templates/`

---

## Verification
- [ ] Layout renders correctly
- [ ] Sidebar navigation works
- [ ] Active state highlights
- [ ] All pages render without errors
- [ ] i18n messages display

---

## Next
Proceed to **4.10: Styles**

