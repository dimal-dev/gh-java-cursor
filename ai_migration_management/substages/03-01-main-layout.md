# Sub-Stage 3.1: Landing Main Layout

## Goal
Create the base Thymeleaf layout for all public landing pages.

---

## Files to Create

```
templates/landing/
├── layout/
│   └── main.html       # Base layout
├── fragments/
│   ├── header.html     # Site header with navigation
│   ├── footer.html     # Site footer
│   └── language-switcher.html
└── error/
    ├── 404.html
    └── 500.html
```

---

## Main Layout

```html
<!-- layout/main.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      th:lang="${#locale.language}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title layout:title-pattern="$CONTENT_TITLE | GoodHelp">GoodHelp</title>
    
    <!-- SEO Meta -->
    <meta name="description" th:content="${metaDescription ?: #{landing.defaultDescription}}">
    <link rel="canonical" th:href="${canonicalUrl}">
    
    <!-- Alternate languages -->
    <link rel="alternate" hreflang="uk" th:href="${ukUrl}">
    <link rel="alternate" hreflang="ru" th:href="${ruUrl}">
    <link rel="alternate" hreflang="en" th:href="${enUrl}">
    
    <link rel="stylesheet" th:href="@{/css/landing/main.css}">
    <th:block layout:fragment="head"></th:block>
</head>
<body>
    <header th:replace="~{landing/fragments/header :: header}"></header>
    
    <main layout:fragment="content"></main>
    
    <footer th:replace="~{landing/fragments/footer :: footer}"></footer>
    
    <script th:src="@{/js/landing/main.js}"></script>
    <th:block layout:fragment="scripts"></th:block>
</body>
</html>
```

---

## Header Fragment

```html
<!-- fragments/header.html -->
<header th:fragment="header" class="site-header">
    <div class="container">
        <a th:href="@{/}" class="logo">
            <img th:src="@{/img/logo.svg}" alt="GoodHelp">
        </a>
        
        <nav class="main-nav">
            <a th:href="@{/{lang}/therapist-list(lang=${lang})}" 
               th:text="#{landing.nav.therapists}">Find Therapist</a>
            <a th:href="@{/{lang}/blog(lang=${lang})}" 
               th:text="#{landing.nav.blog}">Blog</a>
            <a th:href="@{/{lang}/about(lang=${lang})}" 
               th:text="#{landing.nav.about}">About</a>
        </nav>
        
        <div class="header-actions">
            <div th:replace="~{landing/fragments/language-switcher :: switcher}"></div>
            <a th:href="@{/user/login}" class="btn-login" 
               th:text="#{landing.nav.login}">Login</a>
        </div>
        
        <button class="mobile-menu-toggle" aria-label="Menu">☰</button>
    </div>
</header>
```

---

## Language Switcher

```html
<!-- fragments/language-switcher.html -->
<div th:fragment="switcher" class="language-switcher">
    <a th:href="${ukUrl}" th:classappend="${#locale.language == 'uk'} ? 'active'">UA</a>
    <a th:href="${ruUrl}" th:classappend="${#locale.language == 'ru'} ? 'active'">RU</a>
    <a th:href="${enUrl}" th:classappend="${#locale.language == 'en'} ? 'active'">EN</a>
</div>
```

---

## URL Locale Pattern

Routes follow pattern:
- Ukrainian (default): `/page-name`
- Russian: `/ru/page-name`  
- English: `/en/page-name`

Controller base class provides URL generation for all languages:
```java
protected void addLanguageUrls(Model model, String pagePath) {
    model.addAttribute("ukUrl", "/" + pagePath);
    model.addAttribute("ruUrl", "/ru/" + pagePath);
    model.addAttribute("enUrl", "/en/" + pagePath);
}
```

---

## PHP Reference
- `original_php_project/src/Landing/Resources/templates/layout/main.html.twig`
- `original_php_project/src/Landing/Resources/templates/layout/_header.html.twig`
- `original_php_project/src/Landing/Resources/templates/layout/_footer.html.twig`

---

## Verification
- [x] Layout renders correctly
- [x] Header navigation works
- [x] Language switcher changes locale
- [x] Footer displays correctly
- [x] Mobile menu toggle works
- [x] SEO meta tags present

## Implementation Summary (Completed 2024-12-08)

### Files Created:

**Templates:**
- `landing/layout/main.html` - Main Thymeleaf layout with header, footer, mobile menu
- `landing/fragments/header.html` - Header fragment for custom usage
- `landing/fragments/footer.html` - Footer with navigation links, contacts, social media
- `landing/fragments/language-switcher.html` - Desktop and mobile language switcher
- `landing/error/404.html` - 404 error page
- `landing/error/500.html` - 500 error page

**Static Assets:**
- `css/landing/main.css` - Main stylesheet (500+ lines) with:
  - CSS variables for colors, spacing, typography
  - Header and navigation styles
  - Mobile menu styles
  - Footer styles
  - Button components
  - Card components
  - Form styles
  - Typography
  - Utilities
  - Responsive breakpoints
- `js/landing/main.js` - Main JavaScript with:
  - Mobile menu toggle
  - Smooth scroll
  - FAQ accordion
  - Header scroll effects
  - Utility functions

**Java:**
- `BaseLandingController.java` - Base controller with common functionality:
  - Language URL generation
  - SEO meta tag helpers
  - Timezone detection
  - Locale handling

**i18n:**
- Added error page messages and footer translations

---

## Next
Proceed to **3.2: Home Page**

