# Sub-Stage 3.4: Therapist Profile Page

## Goal
Implement detailed therapist profile page.

---

## Routes
- `GET /{lang}/therapist/{id}` - Profile by ID

---

## Controller

```java
@Controller
public class TherapistProfileController {
    
    private final GetTherapistProfileUseCase profileUseCase;
    
    @GetMapping({"/{lang}/therapist/{id}", "/therapist/{id}"})
    public String profile(@PathVariable(required = false) String lang,
                         @PathVariable Long id,
                         Model model) {
        var profile = profileUseCase.execute(id)
            .orElseThrow(() -> new ResourceNotFoundException("Therapist not found"));
        
        model.addAttribute("therapist", profile);
        addLanguageUrls(model, "therapist/" + id);
        
        return "landing/therapist-profile";
    }
}
```

---

## Profile Sections

### 1. Header
- Large photo
- Name and title
- Specialties tags
- "Book Consultation" CTA

### 2. About
- Full biography
- Education and certifications
- Approach/methods

### 3. Specializations
- List of areas of expertise
- Linked to topics

### 4. Experience
- Years of practice
- Notable achievements

### 5. Pricing
- Individual session price
- Couple session price (if offered)
- Session duration

### 6. Reviews (if any)
- Client testimonials

### 7. Book Consultation CTA
- Price summary
- "Book Now" button linking to booking page

---

## Template Structure

```html
<!-- landing/therapist-profile.html -->
<main layout:fragment="content">
    <section class="profile-header">
        <div class="profile-photo">
            <img th:src="${therapist.photoUrl}" th:alt="${therapist.fullName}">
        </div>
        <div class="profile-intro">
            <h1 th:text="${therapist.fullName}">Name</h1>
            <div class="specialties">
                <span th:each="s : ${therapist.specialties}" th:text="${s}"></span>
            </div>
            <a th:href="@{/{lang}/book-consultation/{id}(lang=${lang}, id=${therapist.id})}" 
               class="btn-primary btn-large">
                <span th:text="#{landing.profile.bookConsultation}">Book Consultation</span>
            </a>
        </div>
    </section>
    
    <section class="profile-about">
        <h2 th:text="#{landing.profile.about}">About</h2>
        <div th:utext="${therapist.biography}"></div>
    </section>
    
    <section class="profile-education">
        <h2 th:text="#{landing.profile.education}">Education</h2>
        <div th:utext="${therapist.education}"></div>
    </section>
    
    <section class="profile-prices">
        <h2 th:text="#{landing.profile.pricing}">Pricing</h2>
        <div class="price-card" th:each="price : ${therapist.prices}">
            <span class="price-type" th:text="${price.typeName}">Individual</span>
            <span class="price-amount" th:text="${price.formatted}">800 UAH</span>
            <span class="price-duration" th:text="#{landing.profile.perSession}">per session</span>
        </div>
    </section>
</main>
```

---

## DTO

```java
public record TherapistProfileDto(
    Long id,
    String fullName,
    String photoUrl,
    List<String> specialties,
    String biography,
    String education,
    String methods,
    int yearsExperience,
    List<PriceDto> prices
) {}

public record PriceDto(
    Long id,
    String slug,
    String typeName,     // "Individual" or "Couple"
    String formatted,    // "800 UAH"
    int amountCents
) {}
```

---

## PHP Reference
- `original_php_project/src/Landing/Controller/PsihologProfileController.php`
- `original_php_project/src/Landing/Service/PsihologProfileInfo.php`
- `original_php_project/src/Landing/Resources/templates/pages/psiholog-profile.html.twig`

---

## Verification
- [x] Profile loads with all sections
- [x] Photo displays correctly
- [x] Biography and education render HTML
- [x] Prices shown correctly
- [x] Book button links to booking page
- [x] 404 for invalid therapist ID
- [x] SEO meta tags set

## Implementation Notes
**Status: COMPLETED** (2024-12-08)

### Files Created:
- `com/goodhelp/landing/application/dto/TherapistProfileDto.java` - Main DTO with nested records
- `com/goodhelp/landing/application/dto/TherapistProfileData.java` - Static profile data provider
- `com/goodhelp/landing/application/usecase/GetTherapistProfileUseCase.java` - Use case
- `com/goodhelp/landing/presentation/web/TherapistProfileController.java` - Controller with 3 language routes
- `templates/landing/therapist-profile.html` - Full profile template
- `templates/landing/fragments/profile-icons.html` - SVG icons fragment
- `static/css/landing/therapist-profile.css` - Responsive CSS styles
- i18n messages added to all 3 language files (methods, topics, profile sections)

---

## Next
Proceed to **3.5: Book Consultation Page**

