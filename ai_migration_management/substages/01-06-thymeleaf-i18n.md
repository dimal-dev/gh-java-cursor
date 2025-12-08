# Sub-Stage 1.6: Thymeleaf & i18n Setup

## Goal
Configure Thymeleaf templating engine and internationalization (i18n) support for English, Ukrainian, and Russian.

---

## Files to Create

### 1. ThymeleafConfig.java

**Path:** `src/main/java/com/goodhelp/config/ThymeleafConfig.java`

```java
package com.goodhelp.config;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Thymeleaf configuration.
 * Adds layout dialect for template inheritance.
 */
@Configuration
public class ThymeleafConfig {
    
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}
```

---

### 2. WebMvcConfig.java

**Path:** `src/main/java/com/goodhelp/config/WebMvcConfig.java`

```java
package com.goodhelp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * Web MVC configuration.
 * Configures locale resolution, interceptors, and static resources.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    /**
     * Session-based locale resolver.
     * Default locale is English.
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }
    
    /**
     * Interceptor to change locale based on "lang" parameter.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }
}
```

---

### 3. Message Files

#### messages.properties (English - Default)

**Path:** `src/main/resources/messages.properties`

```properties
# ============================================
# GoodHelp - English Translations (Default)
# ============================================

# Common
our_specialists=Our Specialists
Blog=Blog
Safe=Safe
Pages=Pages
Other=Other
Contacts=Contacts
Price=Price
from=from
Book=Book

# Header
landing.header.for_psiholog=For Psychologists
landing.header.user_login=Login
landing.header.user_dashboard=My Cabinet
landing.request_psiholog=Find a Psychologist

# Landing Index Page
landing.index.title=Online Psychologist GoodHelp. Online Consultation
landing.index.from_any_point=FROM ANY POINT OF THE WORLD
Consultation=Consultation
psihologa=psychologist
online=online
landing.index.first_screen_description=We will help you find a verified psychologist <br class="gh-ld-d-none">to solve your issue
landing.index.session_50_minutes=50 minute session
landing.index.psiholog_amount_working_with_us=10+ specialists work with us
landing.index.for_ukrainians_and_friends=By Ukrainians for Ukrainians and friends

# Problems
landing.index.you_have_encountered_with=You have encountered one of
landing.index.problem=problems
Relations=Relationships
Work=Work
Children=Children
Selfesteem=Self-esteem
Big_life_changes=Big life changes
Isolation_and_loneliness=Isolation and loneliness

# Psychologist list
landing.psiholog_list.title=Choose a psychologist for online consultation
landing.psiholog_list.our_specialists=Choose a psychologist for online consultation

# Experience
Experience=Experience
one_year=year
few_years=years
many_years=years
of_working_experience=of working experience

# Therapy types
Therapy=Therapy
Individual=Individual
Couple=Couple/Family
Teenager=For Teenagers

# Profile
About_therapist=About the therapist
About_psiholog=About the psychologist
Specialist_works_with_issues=Specialist works with topics
Look_closely=View profile

# Topics
My_state=My state
Work_career_study=Work, career, study
Life_events=Life events

# Qualifications
Qualification=Qualification
Education=Education
Methods_and_approaches=Methods and approaches
Association_and_supervision=Association and supervision
Reviews=Reviews
Diploma_psychologist=Psychology diploma

# Common issues
stress=Stress
anxiety=Anxiety
depression=Depression
panic_attacks=Panic attacks
burnout=Burnout
low_self_esteem=Low self-esteem
relationship_with_a_partner=Relationship with partner
war=War

# Footer
Consultation_conditions=Consultation conditions
Terms=Terms of use
Privacy_policy=Privacy policy
Refund=Refund policy
Write_us=Write to us
Messengers=Messengers
We_in_social=Follow us

# User Cabinet
user.dashboard.title=Dashboard
user.dashboard.upcoming_consultations=Upcoming Consultations
user.dashboard.no_consultations=You have no upcoming consultations
user.dashboard.book_new=Book a new consultation

# Psychologist Cabinet
psiholog.schedule.title=Schedule
psiholog.settings.title=Settings
psiholog.clients.title=Clients
psiholog.chat.title=Chat
psiholog.payments.title=Payments

# Staff Cabinet
staff.dashboard.title=Dashboard
staff.psihologs.title=Psychologists
staff.blog.title=Blog
staff.payouts.title=Payouts

# Forms
form.email=Email
form.name=Name
form.phone=Phone
form.submit=Submit
form.save=Save
form.cancel=Cancel

# Messages
message.success=Successfully saved
message.error=An error occurred
message.not_found=Not found
```

---

#### messages_uk.properties (Ukrainian)

**Path:** `src/main/resources/messages_uk.properties`

```properties
# ============================================
# GoodHelp - Ukrainian Translations
# ============================================

# Note: Copy all keys from messages.properties and translate to Ukrainian
# The Ukrainian translations are already available in the original PHP project:
# original_php_project/resources/translations/messages.ua.yaml

# Common
our_specialists=Наші спеціалісти
Blog=Блог
Safe=Безпечно
Pages=Сторінки
Other=Інше
Contacts=Контакти
Price=Вартість
from=від
Book=Записатись

# Header
landing.header.for_psiholog=Для психологів
landing.header.user_login=Вхід
landing.header.user_dashboard=Особистий кабінет
landing.request_psiholog=Підібрати психолога

# Landing Index Page
landing.index.title=Психолог онлайн GoodHelp. Онлайн консультація психолога
landing.index.from_any_point=З БУДЬ-ЯКОЇ ТОЧКИ СВІТУ
Consultation=Консультація
psihologa=психолога
online=онлайн
landing.index.first_screen_description=Допоможемо підібрати перевіреного психолога <br class="gh-ld-d-none">для вирішення вашого питання
landing.index.session_50_minutes=Сесія 50 хвилин
landing.index.psiholog_amount_working_with_us=З нами працюють 10+ спеціалістів
landing.index.for_ukrainians_and_friends=Українцями для українців та друзів

# ... (continue with all translations from messages.ua.yaml)
# This file will be populated with all translations during migration
```

---

#### messages_ru.properties (Russian)

**Path:** `src/main/resources/messages_ru.properties`

```properties
# ============================================
# GoodHelp - Russian Translations
# ============================================

# Note: Copy all keys from messages.properties and translate to Russian
# The Russian translations are already available in the original PHP project:
# original_php_project/resources/translations/messages.ru.yaml

# Common
our_specialists=Наши специалисты
Blog=Блог
Safe=Безопасно
Pages=Страницы
Other=Другое
Contacts=Контакты
Price=Стоимость
from=от
Book=Записаться

# Header
landing.header.for_psiholog=Для психологов
landing.header.user_login=Вход
landing.header.user_dashboard=Личный кабинет
landing.request_psiholog=Подобрать психолога

# ... (continue with all translations from messages.ru.yaml)
# This file will be populated with all translations during migration
```

---

### 4. Base Layout Template

**Path:** `src/main/resources/templates/layout/main.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      th:lang="${#locale.language}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <title layout:title-pattern="$CONTENT_TITLE - GoodHelp">GoodHelp</title>
    
    <!-- Canonical URL -->
    <link rel="canonical" th:href="@{${currentUrl}}" />
    
    <!-- Language alternates -->
    <link rel="alternate" th:href="@{/(lang=en)}" hreflang="en" />
    <link rel="alternate" th:href="@{/(lang=uk)}" hreflang="uk" />
    <link rel="alternate" th:href="@{/(lang=ru)}" hreflang="ru" />
    <link rel="alternate" th:href="@{/(lang=en)}" hreflang="x-default" />
    
    <!-- Styles -->
    <link rel="stylesheet" th:href="@{/css/landing/main.css}" />
    
    <!-- Additional head content -->
    <th:block layout:fragment="head"></th:block>
</head>
<body>
    <!-- Header -->
    <header class="gh-header">
        <a th:href="@{/}" class="gh-header__logo">
            <img class="gh-img" th:src="@{/img/logo/logo-olive.svg}" alt="GoodHelp">
        </a>
        <nav class="gh-header__nav">
            <ul class="gh-header__nav-menu">
                <li>
                    <a th:href="@{/therapist-list}" th:text="#{our_specialists}">Our Specialists</a>
                </li>
                <li>
                    <a th:href="@{/blog}" th:text="#{Blog}">Blog</a>
                </li>
                <li>
                    <a href="https://forms.gle/kVM9qC4Q2tMaD9Xe7" target="_blank" 
                       th:text="#{landing.header.for_psiholog}">For Psychologists</a>
                </li>
                <li th:if="${#authorization.expression('isAuthenticated()')}">
                    <a th:href="@{/user/}" th:text="#{landing.header.user_dashboard}">My Cabinet</a>
                </li>
                <li th:unless="${#authorization.expression('isAuthenticated()')}">
                    <a th:href="@{/user/login}" th:text="#{landing.header.user_login}">Login</a>
                </li>
            </ul>
        </nav>
        <div class="gh-header__lang-picker">
            <a th:href="@{''(lang='en')}" th:classappend="${#locale.language == 'en'} ? 'active'">EN</a>
            <a th:href="@{''(lang='uk')}" th:classappend="${#locale.language == 'uk'} ? 'active'">UA</a>
            <a th:href="@{''(lang='ru')}" th:classappend="${#locale.language == 'ru'} ? 'active'">RU</a>
        </div>
    </header>
    
    <!-- Main Content -->
    <main class="gh-main" layout:fragment="content">
        <p>Content goes here</p>
    </main>
    
    <!-- Footer -->
    <footer th:replace="~{fragments/footer :: footer}"></footer>
    
    <!-- Scripts -->
    <script th:src="@{/js/landing.js}"></script>
    <th:block layout:fragment="scripts"></th:block>
</body>
</html>
```

---

### 5. Footer Fragment

**Path:** `src/main/resources/templates/fragments/footer.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<footer th:fragment="footer" class="gh-footer">
    <div class="gh-footer__content">
        <div class="gh-footer__section">
            <h4 th:text="#{Pages}">Pages</h4>
            <ul>
                <li><a th:href="@{/about}" th:text="#{About}">About</a></li>
                <li><a th:href="@{/consultation-conditions}" th:text="#{Consultation_conditions}">Consultation conditions</a></li>
            </ul>
        </div>
        <div class="gh-footer__section">
            <h4 th:text="#{Other}">Other</h4>
            <ul>
                <li><a th:href="@{/terms-of-use}" th:text="#{Terms}">Terms</a></li>
                <li><a th:href="@{/privacy}" th:text="#{Privacy_policy}">Privacy</a></li>
                <li><a th:href="@{/refund-policy}" th:text="#{Refund}">Refund</a></li>
            </ul>
        </div>
        <div class="gh-footer__section">
            <h4 th:text="#{Contacts}">Contacts</h4>
            <p><a href="mailto:info@goodhelp.com.ua">info@goodhelp.com.ua</a></p>
        </div>
    </div>
    <div class="gh-footer__copyright">
        <p>&copy; <span th:text="${#dates.year(#dates.createNow())}">2024</span> GoodHelp</p>
    </div>
</footer>
</html>
```

---

## Verification

1. **Create a test controller:**

```java
@Controller
public class TestController {
    @GetMapping("/test-i18n")
    public String test() {
        return "test";
    }
}
```

2. **Create test template:**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/main}">
<head>
    <title th:text="#{landing.index.title}">Test</title>
</head>
<body>
<main layout:fragment="content">
    <h1 th:text="#{landing.index.title}">Title</h1>
    <p>Current locale: <span th:text="${#locale}">en</span></p>
    <p>
        <a th:href="@{/test-i18n(lang='en')}">English</a> |
        <a th:href="@{/test-i18n(lang='uk')}">Ukrainian</a> |
        <a th:href="@{/test-i18n(lang='ru')}">Russian</a>
    </p>
</main>
</body>
</html>
```

3. **Test:**
   - Visit `/test-i18n` - should show English
   - Visit `/test-i18n?lang=uk` - should switch to Ukrainian
   - Visit `/test-i18n?lang=ru` - should switch to Russian

---

## Checklist

- [ ] ThymeleafConfig.java created
- [ ] WebMvcConfig.java created
- [ ] messages.properties created (English)
- [ ] messages_uk.properties created (Ukrainian base)
- [ ] messages_ru.properties created (Russian base)
- [ ] Base layout template created
- [ ] Footer fragment created
- [ ] Language switching works

---

## Next Sub-Stage
Proceed to **1.7: Common Module**

