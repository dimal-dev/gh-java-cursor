# Sub-Stage 3.2: Home Page

## Goal
Implement the landing page home with all sections.

---

## Route
- `GET /` (Ukrainian default)
- `GET /ru/` (Russian)
- `GET /en/` (English)

---

## Controller

```java
@Controller
public class HomeController {
    
    private final GetFeaturedTherapistsUseCase featuredUseCase;
    
    @GetMapping({"", "/", "/ru", "/ru/", "/en", "/en/"})
    public String index(Model model, Locale locale) {
        var featured = featuredUseCase.execute(6); // Top 6 therapists
        model.addAttribute("therapists", featured);
        addLanguageUrls(model, "");
        return "landing/index";
    }
}
```

---

## Page Sections

### 1. Hero Section
- Main headline
- Subheadline
- CTA button: "Find Your Therapist"
- Background image/illustration

### 2. Problem Categories
- Grid of common issues (anxiety, relationships, depression, etc.)
- Each links to filtered therapist list

### 3. How It Works
- 3-step process: Choose → Book → Consult
- Icons and descriptions

### 4. Featured Therapists
- 6 therapist cards
- Photo, name, specialty, rating
- "View Profile" button

### 5. Benefits/Trust Section
- Key benefits (confidential, professional, convenient)
- Stats if available

### 6. FAQ Accordion
- Common questions with expandable answers

### 7. CTA Block
- Final call-to-action
- Link to therapist list

---

## Template Structure

```html
<!-- landing/index.html -->
<html layout:decorate="~{landing/layout/main}">
<head>
    <title th:text="#{landing.index.title}">Psychology Help Online</title>
</head>
<body>
<main layout:fragment="content">
    
    <section class="hero">
        <div class="container">
            <h1 th:text="#{landing.index.hero.title}">Find Your Therapist</h1>
            <p th:text="#{landing.index.hero.subtitle}">Professional online therapy</p>
            <a th:href="@{/{lang}/therapist-list(lang=${lang})}" class="btn-primary">
                <span th:text="#{landing.index.hero.cta}">Get Started</span>
            </a>
        </div>
    </section>
    
    <section class="problems">
        <div class="container">
            <h2 th:text="#{landing.index.problems.title}">What brings you here?</h2>
            <div class="problems-grid">
                <a th:each="problem : ${problems}" 
                   th:href="@{/{lang}/therapist-list(lang=${lang}, topic=${problem.slug})}"
                   class="problem-card">
                    <span th:text="${problem.name}"></span>
                </a>
            </div>
        </div>
    </section>
    
    <section class="how-it-works">...</section>
    <section class="featured-therapists">...</section>
    <section class="faq">...</section>
    
</main>
</body>
</html>
```

---

## i18n Keys

```properties
landing.index.title=Online Psychology Help - GoodHelp
landing.index.hero.title=Find Your Therapist Today
landing.index.hero.subtitle=Professional online consultations with certified specialists
landing.index.hero.cta=Find a Therapist
landing.index.problems.title=What would you like to work on?
landing.index.howItWorks.title=How It Works
landing.index.faq.title=Frequently Asked Questions
```

---

## PHP Reference
- `original_php_project/src/Landing/Controller/IndexController.php`
- `original_php_project/src/Landing/Resources/templates/pages/index.html.twig`

---

## Verification
- [x] Page loads with all sections
- [x] Hero CTA links correctly
- [x] Problem categories display
- [ ] Featured therapists display (needs therapist list page first)
- [x] FAQ accordion works
- [x] i18n works for all 3 languages
- [x] Responsive design

## Implementation Summary (Completed 2024-12-08)

### Files Created:

**Java:**
- `HomeController.java` - Controller handling `/`, `/ru/`, `/en/` routes with FAQ data

**Templates:**
- `landing/index.html` - Full home page template with all 7 sections:
  - Hero section with social links, CTA, advantages
  - Problem categories section (6 categories with detailed problems)
  - Our therapists section
  - Benefits/selection criteria section
  - Reviews carousel (Swiper)
  - FAQ accordion (7 questions)
  - Final CTA section
- `landing/fragments/problem-category.html` - Reusable fragment for problem cards

**CSS:**
- `css/landing/index.css` - 600+ lines covering all page sections, responsive design

**i18n:**
- Added 60+ new translation keys to all 3 language files:
  - Problem descriptions for all 6 categories
  - Benefits section texts
  - Reviews section
  - FAQ questions and answers
  - Misc texts (Dima, Thanks for useful session, etc.)

---

## Next
Proceed to **3.3: Therapist List Page**

