# Sub-Stage 2.6: Auth Controllers

## Goal
Implement login page and auto-login endpoints for therapist cabinet.

---

## Controllers to Create

**Location:** `com.goodhelp.therapist.presentation.web/`

### TherapistLoginController
```java
@Controller
@RequestMapping("/therapist")
public class TherapistLoginController {
    
    private final RequestLoginLinkUseCase requestLoginLinkUseCase;
    
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "therapist/login";
    }
    
    @PostMapping("/login")
    public String processLogin(@Valid LoginForm form, 
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Locale locale) {
        if (result.hasErrors()) {
            return "therapist/login";
        }
        
        try {
            requestLoginLinkUseCase.execute(new RequestLoginCommand(form.getEmail()));
            redirectAttributes.addFlashAttribute("success", 
                messageSource.getMessage("therapist.login.emailSent", null, locale));
        } catch (TherapistNotFoundException e) {
            result.rejectValue("email", "error.notFound");
            return "therapist/login";
        }
        
        return "redirect:/therapist/login";
    }
    
    @GetMapping("/auto-login")
    public String autoLogin(@RequestParam("t") String token,
                           RedirectAttributes redirectAttributes) {
        // Authentication handled by filter
        // This just handles redirect and error cases
        return "redirect:/therapist/schedule";
    }
}
```

### LoginForm
```java
public class LoginForm {
    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;
    
    // Getter/setter
}
```

---

## PHP Controller Reference

| PHP Controller | Route | Java Mapping |
|----------------|-------|--------------|
| `LoginController::show` | GET /therapist/login | `showLoginPage()` |
| `LoginController::login` | POST /therapist/login | `processLogin()` |
| `AutoLoginController` | GET /therapist/auto-login | `autoLogin()` + Filter |
| `LogoutController` | POST /therapist/logout | Spring Security |

---

## Template: login.html

**Path:** `templates/therapist/login.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="#{therapist.login.title}">Login</title>
    <!-- Include styles -->
</head>
<body>
    <div class="login-container">
        <h1 th:text="#{therapist.login.heading}">Sign In</h1>
        
        <div th:if="${success}" class="alert alert-success" th:text="${success}"></div>
        
        <form th:action="@{/therapist/login}" th:object="${loginForm}" method="post">
            <div class="form-group">
                <label th:text="#{therapist.login.email}">Email</label>
                <input type="email" th:field="*{email}" class="form-control"/>
                <span th:if="${#fields.hasErrors('email')}" 
                      th:errors="*{email}" class="error"></span>
            </div>
            <button type="submit" th:text="#{therapist.login.submit}">Send Login Link</button>
        </form>
    </div>
</body>
</html>
```

---

## i18n Keys

```properties
# messages.properties (English)
therapist.login.title=Therapist Login
therapist.login.heading=Sign In to Cabinet
therapist.login.email=Your Email
therapist.login.submit=Send Login Link
therapist.login.emailSent=Login link sent to your email
therapist.login.invalidToken=Invalid or expired login link
```

---

## Verification
- [x] Login page displays correctly
- [x] Email validation works
- [x] Success message shows after form submission
- [x] Auto-login with valid token redirects to schedule
- [x] Invalid token shows error message
- [x] i18n messages work in all languages

## Implementation Notes

### Created Files:

**Presentation Layer (`com.goodhelp.therapist.presentation.web`):**
- `TherapistLoginController.java` - Handles login page and login link requests
- `form/LoginForm.java` - Form object with validation

**Templates (`templates/therapist/`):**
- `login.html` - Standalone login page with modern styling

**Layouts (`templates/layout/`):**
- `therapist.html` - Cabinet layout with sidebar navigation

**i18n Messages (all 3 languages):**
- `therapist.login.*` - All login-related messages
- `therapist.nav.*` - Navigation menu items
- `validation.*` - Form validation messages

### Authentication Flow:
1. User visits `/therapist/login`
2. User enters email and submits form to `/therapist/request-login`
3. System generates token and sends notification (email/Telegram)
4. User clicks link `/therapist/auto-login?t={token}`
5. `TherapistAutoLoginFilter` validates token and creates session
6. User redirected to `/therapist/schedule`

## Status: COMPLETED ✅

---

## Next
Proceed to **2.7: Schedule Feature**

