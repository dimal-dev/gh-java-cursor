# Sub-Stage 5.10: Staff Templates

## Goal
Create all Thymeleaf templates for staff cabinet.

---

## Template Structure

```
templates/staff/
├── layout/
│   └── main.html
├── fragments/
│   ├── sidebar.html
│   └── header.html
├── login.html
├── dashboard.html
├── therapists/
│   ├── list.html
│   ├── add.html
│   ├── edit-profile.html
│   └── edit-settings.html
├── blog/
│   ├── list.html
│   └── edit.html
└── payouts/
    └── index.html
```

---

## Layout

```html
<!-- layout/main.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <meta charset="UTF-8">
    <title layout:title-pattern="$CONTENT_TITLE - Staff">Staff Cabinet</title>
    <link rel="stylesheet" th:href="@{/css/staff/main.css}">
</head>
<body>
    <div class="staff-cabinet">
        <aside th:replace="~{staff/fragments/sidebar :: sidebar}"></aside>
        <main class="content">
            <header th:replace="~{staff/fragments/header :: header}"></header>
            <div class="page-content" layout:fragment="content"></div>
        </main>
    </div>
    <script th:src="@{/js/staff/main.js}"></script>
    <th:block layout:fragment="scripts"></th:block>
</body>
</html>
```

---

## Sidebar with Role Check

```html
<!-- fragments/sidebar.html -->
<aside th:fragment="sidebar" class="sidebar">
    <div class="logo">
        <span>GoodHelp Staff</span>
    </div>
    
    <nav>
        <a th:href="@{/staff/}" th:classappend="${active == 'dashboard'} ? 'active'">
            Dashboard
        </a>
        <a th:href="@{/staff/therapists}" th:classappend="${active == 'therapists'} ? 'active'">
            Therapists
        </a>
        <a th:href="@{/staff/blog}" th:classappend="${active == 'blog'} ? 'active'">
            Blog
        </a>
        <a th:href="@{/staff/payouts}" th:classappend="${active == 'payouts'} ? 'active'"
           sec:authorize="hasRole('STAFF_SUPERUSER')">
            Payouts
        </a>
    </nav>
    
    <div class="sidebar-footer">
        <form th:action="@{/staff/logout}" method="post">
            <button type="submit">Logout</button>
        </form>
    </div>
</aside>
```

---

## Data Table Pattern

```html
<!-- Common pattern for list pages -->
<div class="table-controls">
    <input type="text" id="search" placeholder="Search...">
    <select id="filter-status">
        <option value="">All</option>
        <option value="active">Active</option>
        <option value="inactive">Inactive</option>
    </select>
</div>

<table class="data-table" id="data-table">
    <thead>
        <tr>
            <th>Column 1</th>
            <th>Column 2</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
        <!-- Populated via JS -->
    </tbody>
</table>

<div class="pagination" id="pagination"></div>
```

---

## Verification
- [ ] All templates render
- [ ] Layout applied correctly
- [ ] Sidebar navigation works
- [ ] Role-based visibility works
- [ ] Data tables display

---

## Next
Proceed to **5.11: Styles**

