# Sub-Stage 3.9: Request Therapist Form

## Goal
Implement the "Help me find a therapist" request form.

---

## Routes
- `GET /{lang}/request-therapist` - Show form
- `POST /request-therapist` - Submit form
- `GET /{lang}/request-therapist/success` - Success page

---

## Controller

```java
@Controller
public class RequestTherapistController {
    
    private final SubmitRequestUseCase submitRequestUseCase;
    
    @GetMapping({"/{lang}/request-therapist", "/request-therapist"})
    public String showForm(Model model) {
        model.addAttribute("form", new RequestTherapistForm());
        model.addAttribute("topics", topicService.getAllTopics());
        addLanguageUrls(model, "request-therapist");
        return "landing/request-therapist";
    }
    
    @PostMapping({"/request-therapist", "/{lang}/request-therapist"})
    public String submitForm(@Valid RequestTherapistForm form,
                            BindingResult result,
                            Model model,
                            Locale locale) {
        if (result.hasErrors()) {
            model.addAttribute("topics", topicService.getAllTopics());
            return "landing/request-therapist";
        }
        
        submitRequestUseCase.execute(SubmitRequestCommand.from(form));
        
        String lang = locale.getLanguage().equals("uk") ? "" : "/" + locale.getLanguage();
        return "redirect:" + lang + "/request-therapist/success";
    }
    
    @GetMapping({"/{lang}/request-therapist/success", "/request-therapist/success"})
    public String success() {
        return "landing/request-therapist-success";
    }
}
```

---

## Form

```java
public class RequestTherapistForm {
    @NotBlank(message = "{validation.name.required}")
    private String name;
    
    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;
    
    private String phone;
    
    @NotEmpty(message = "{validation.topics.required}")
    private List<String> topics;  // Selected topic slugs
    
    @Size(max = 2000, message = "{validation.message.tooLong}")
    private String message;
    
    private String preferredTime;  // "morning", "afternoon", "evening"
}
```

---

## Template

```html
<!-- request-psiholog.html -->
<main layout:fragment="content">
    <section class="request-form-page">
        <div class="container">
            <h1 th:text="#{landing.request.title}">Help Me Find a Therapist</h1>
            <p th:text="#{landing.request.subtitle}">
                Tell us about your situation and we'll match you with the right specialist
            </p>
            
            <form th:action="@{/request-therapist}" th:object="${form}" method="post">
                <div class="form-group">
                    <label th:text="#{landing.request.name}">Your Name</label>
                    <input type="text" th:field="*{name}">
                    <span th:if="${#fields.hasErrors('name')}" th:errors="*{name}" class="error"></span>
                </div>
                
                <div class="form-group">
                    <label th:text="#{landing.request.email}">Email</label>
                    <input type="email" th:field="*{email}">
                    <span th:if="${#fields.hasErrors('email')}" th:errors="*{email}" class="error"></span>
                </div>
                
                <div class="form-group">
                    <label th:text="#{landing.request.phone}">Phone (optional)</label>
                    <input type="tel" th:field="*{phone}">
                </div>
                
                <div class="form-group">
                    <label th:text="#{landing.request.topics}">What would you like to work on?</label>
                    <div class="topics-grid">
                        <label th:each="topic : ${topics}" class="topic-checkbox">
                            <input type="checkbox" th:field="*{topics}" th:value="${topic.slug}">
                            <span th:text="${topic.name}"></span>
                        </label>
                    </div>
                    <span th:if="${#fields.hasErrors('topics')}" th:errors="*{topics}" class="error"></span>
                </div>
                
                <div class="form-group">
                    <label th:text="#{landing.request.message}">Tell us more (optional)</label>
                    <textarea th:field="*{message}" rows="4"></textarea>
                </div>
                
                <button type="submit" class="btn-primary" th:text="#{landing.request.submit}">
                    Send Request
                </button>
            </form>
        </div>
    </section>
</main>
```

---

## Entity

```java
@Entity
@Table(name = "user_request_psiholog")
public class UserRequestTherapist {
    @Id @GeneratedValue
    private Long id;
    
    private String name;
    private String email;
    private String phone;
    private String topics;  // JSON array or comma-separated
    private String message;
    private String preferredTime;
    private String locale;
    
    private LocalDateTime createdAt;
}
```

---

## PHP Reference
- `original_php_project/src/Landing/Controller/RequestPsihologController.php`
- `original_php_project/src/Landing/Resources/templates/pages/request-psiholog.html.twig`

---

## Verification
- [ ] Form displays with all fields
- [ ] Topic checkboxes work
- [ ] Validation errors shown
- [ ] Submission creates database record
- [ ] Redirects to success page
- [ ] Success page displays correctly

---

## Next
Proceed to **3.10: Landing Styles**

