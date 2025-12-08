# Sub-Stage 3.8: Static Pages

## Goal
Implement informational static pages.

---

## Pages to Create

| Route | Controller | Template | Content |
|-------|------------|----------|---------|
| `/about` | `AboutController` | `about.html` | Company info |
| `/prices` | `PricesController` | `prices.html` | Pricing info |
| `/consultation-conditions` | `ConditionsController` | `conditions.html` | Booking rules |
| `/privacy` | `PrivacyController` | `privacy.html` | Privacy policy |
| `/terms-of-use` | `TermsController` | `terms.html` | Terms of service |
| `/refund-policy` | `RefundController` | `refund.html` | Refund policy |
| `/family-psiholog` | `FamilyController` | `family.html` | Family therapy landing |
| `/teenage-psiholog` | `TeenageController` | `teenage.html` | Teen therapy landing |
| `/therapist-apply` | `ApplyController` | `apply.html` | Therapist application |

---

## Static Page Controller Pattern

```java
@Controller
public class StaticPagesController {
    
    @GetMapping({"/{lang}/about", "/about"})
    public String about(@PathVariable(required = false) String lang, Model model) {
        addLanguageUrls(model, "about");
        return "landing/static/about";
    }
    
    @GetMapping({"/{lang}/prices", "/prices"})
    public String prices(@PathVariable(required = false) String lang, Model model) {
        addLanguageUrls(model, "prices");
        return "landing/static/prices";
    }
    
    @GetMapping({"/{lang}/privacy", "/privacy"})
    public String privacy(@PathVariable(required = false) String lang, Model model) {
        addLanguageUrls(model, "privacy");
        return "landing/static/privacy";
    }
    
    // ... other static pages
}
```

---

## Template Structure

```html
<!-- static/about.html -->
<html layout:decorate="~{landing/layout/main}">
<head>
    <title th:text="#{landing.about.title}">About Us</title>
</head>
<body>
<main layout:fragment="content">
    <section class="static-page">
        <div class="container">
            <h1 th:text="#{landing.about.heading}">About GoodHelp</h1>
            
            <div class="content" th:utext="#{landing.about.content}">
                <!-- Content from messages.properties -->
            </div>
        </div>
    </section>
</main>
</body>
</html>
```

---

## Long Content in i18n

For pages with long content, use message properties with HTML:

```properties
# messages.properties
landing.about.content=<p>GoodHelp is a platform...</p><p>Our mission...</p>

# Or reference template content from files
```

Alternative: Store content in templates directly with i18n for headings only.

---

## SEO Pages

Family and Teenage therapist pages are SEO landing pages:
- Targeted content for specific audiences
- Featured therapists filtered by specialty
- CTA to therapist list with filter

```java
@GetMapping({"/{lang}/family-psiholog", "/family-psiholog"})
public String familyTherapist(Model model) {
    var therapists = catalogUseCase.execute(new GetCatalogQuery("family"));
    model.addAttribute("therapists", therapists.subList(0, Math.min(6, therapists.size())));
    return "landing/seo/family";
}
```

---

## PHP Reference
- `original_php_project/src/Landing/Controller/AboutController.php`
- `original_php_project/src/Landing/Controller/PricesController.php`
- etc.

---

## Verification
- [ ] All static pages render
- [ ] Content displays correctly
- [ ] Language switcher works
- [ ] SEO meta tags set
- [ ] Links to other pages work
- [ ] Mobile responsive

---

## Next
Proceed to **3.9: Request Therapist Form**

