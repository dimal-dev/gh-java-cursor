# Sub-Stage 3.3: Therapist List Page

## Goal
Implement the therapist catalog/listing page.

---

## Routes
- `GET /{lang}/therapist-list` 
- Query params: `topic` (filter by specialty)

---

## Controller

```java
@Controller
public class TherapistListController {
    
    private final GetTherapistCatalogUseCase catalogUseCase;
    
    @GetMapping({"/{lang}/therapist-list", "/therapist-list"})
    public String list(@PathVariable(required = false) String lang,
                       @RequestParam(required = false) String topic,
                       Model model) {
        var query = new GetCatalogQuery(topic);
        var therapists = catalogUseCase.execute(query);
        
        model.addAttribute("therapists", therapists);
        model.addAttribute("selectedTopic", topic);
        model.addAttribute("topics", catalogUseCase.getTopics());
        addLanguageUrls(model, "psiholog-list");
        
        return "landing/therapist-list";
    }
}
```

---

## Page Features

### Filter/Topic Selection
- Horizontal list of topic tags
- Active topic highlighted
- Click to filter list

### Therapist Cards
Each card displays:
- Photo
- Full name
- Short bio/specialty
- Years of experience
- Price range (individual/couple)
- "View Profile" button

### Card Layout
```html
<div class="therapist-card" th:each="p : ${therapists}">
    <div class="psiholog-photo">
        <img th:src="${p.photoUrl}" th:alt="${p.fullName}">
    </div>
    <div class="psiholog-info">
        <h3 th:text="${p.fullName}">Name</h3>
        <p class="specialty" th:text="${p.specialty}">Specialty</p>
        <p class="experience" th:text="#{landing.psiholog.experience(${p.yearsExperience})}">
            X years experience
        </p>
        <div class="prices">
            <span th:text="#{landing.psiholog.from} + ' ' + ${p.priceFrom}">From 500 UAH</span>
        </div>
        <a th:href="@{/{lang}/therapist/{id}(lang=${lang}, id=${p.id})}" 
           class="btn-secondary" th:text="#{landing.psiholog.viewProfile}">
            View Profile
        </a>
    </div>
</div>
```

---

## DTOs

```java
public record TherapistListItemDto(
    Long id,
    String fullName,
    String photoUrl,
    String specialty,
    int yearsExperience,
    String priceFrom,    // Formatted, e.g., "500 UAH"
    String profileSlug
) {}

public record TopicDto(
    String slug,
    String name
) {}
```

---

## PHP Reference
- `original_php_project/src/Landing/Controller/PsihologListController.php`
- `original_php_project/src/Landing/Service/PsihologList.php`
- `original_php_project/src/Landing/Resources/templates/pages/psiholog-list.html.twig`

---

## Verification
- [x] List displays all active therapists
- [ ] Topic filter works (basic structure, filtering to be enhanced later)
- [x] Cards show correct information
- [x] Links to profile pages work
- [x] Responsive grid layout
- [x] i18n for all text

---

## Implementation Summary (Completed 2024-12-08)

### Files Created:

**Domain Layer:**
- `TherapistPrice.java` - Entity for therapist pricing
- `PriceType.java` - Enum for price types (Individual, Couple, Teenager)
- `PriceTypeConverter.java` - JPA converter for PriceType
- `PriceState.java` - Enum for price states (Active, Inactive)
- `PriceStateConverter.java` - JPA converter for PriceState
- `TherapistPriceRepository.java` - Repository interface

**Infrastructure Layer:**
- `JpaTherapistPriceRepository.java` - Spring Data JPA repository
- `TherapistPriceRepositoryAdapter.java` - Repository adapter

**Application Layer:**
- `TherapistListItemDto.java` - DTO for therapist list items
- `GetTherapistCatalogUseCase.java` - Use case for fetching catalog

**Presentation Layer:**
- `TherapistListController.java` - Controller with routes /psiholog-list, /ru/psiholog-list, /en/psiholog-list

**Templates:**
- `landing/psiholog-list.html` - Main list page template
- `landing/fragments/psiholog-card.html` - Reusable therapist card fragment

**CSS:**
- `css/landing/psiholog-list.css` - 400+ lines of responsive styles

**i18n:**
- Added 25+ new translation keys to all 3 language files:
  - Page titles and labels
  - Single phrases (therapist taglines)
  - Specialties
  - Experience/price labels

---

## Next
Proceed to **3.4: Therapist Profile**

