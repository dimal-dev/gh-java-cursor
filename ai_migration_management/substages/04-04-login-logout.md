# Sub-Stage 4.4: User Login/Logout

## Goal
Implement login page and authentication endpoints.

---

## Endpoints

| Method | Route | Handler |
|--------|-------|---------|
| GET | /user/login | `showLogin()` |
| POST | /user/login | `processLogin()` |
| GET | /user/auto-login | `autoLogin()` |
| POST | /user/logout | Spring Security |

---

## Controller

```java
@Controller
@RequestMapping("/user")
public class UserLoginController {
    
    private final RequestUserLoginLinkUseCase requestLoginUseCase;
    
    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("form", new UserLoginForm());
        return "user/login";
    }
    
    @PostMapping("/login")
    public String processLogin(@Valid UserLoginForm form,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Locale locale) {
        if (result.hasErrors()) {
            return "user/login";
        }
        
        try {
            requestLoginUseCase.execute(new RequestLoginCommand(form.getEmail()));
            redirectAttributes.addFlashAttribute("success", 
                messageSource.getMessage("user.login.emailSent", null, locale));
        } catch (UserNotFoundException e) {
            result.rejectValue("email", "error.notFound");
            return "user/login";
        }
        
        return "redirect:/user/login";
    }
    
    @GetMapping("/auto-login")
    public String autoLogin(@RequestParam("t") String token) {
        // Authentication handled by filter
        return "redirect:/user/";
    }
}
```

---

## Login Form

```java
public class UserLoginForm {
    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;
}
```

---

## Template

```html
<!-- user/login.html -->
<main class="login-page">
    <div class="login-container">
        <h1 th:text="#{user.login.title}">Sign In</h1>
        <p th:text="#{user.login.subtitle}">Enter your email to receive login link</p>
        
        <div th:if="${success}" class="alert success" th:text="${success}"></div>
        
        <form th:action="@{/user/login}" th:object="${form}" method="post">
            <div class="form-group">
                <label th:text="#{user.login.email}">Email</label>
                <input type="email" th:field="*{email}" autofocus>
                <span th:if="${#fields.hasErrors('email')}" th:errors="*{email}" class="error"></span>
            </div>
            <button type="submit" class="btn-primary" th:text="#{user.login.submit}">
                Send Login Link
            </button>
        </form>
        
        <p class="login-help">
            <a th:href="@{/}" th:text="#{user.login.backToHome}">Back to home</a>
        </p>
    </div>
</main>
```

---

## i18n Keys

```properties
user.login.title=Sign In to Your Account
user.login.subtitle=We'll send you a magic link to sign in
user.login.email=Your Email
user.login.submit=Send Login Link
user.login.emailSent=Check your email for the login link
user.login.backToHome=Back to homepage
```

---

## PHP Reference
- `original_php_project/src/User/Controller/LoginController.php`
- `original_php_project/src/User/Controller/AutoLoginController.php`

---

## Verification
- [ ] Login page displays
- [ ] Email validation works
- [ ] Email sent on valid submission
- [ ] Auto-login redirects to dashboard
- [ ] Invalid token handled

---

## Next
Proceed to **4.5: Dashboard**

