# Sub-Stage 5.4: Staff Login/Logout

## Goal
Implement staff authentication endpoints.

---

## Endpoints

| Method | Route | Handler |
|--------|-------|---------|
| GET | /staff/login | `showLogin()` |
| POST | /staff/login | `processLogin()` |
| GET | /staff/auto-login | `autoLogin()` |

---

## Controller

```java
@Controller
@RequestMapping("/staff")
public class StaffLoginController {
    
    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("form", new StaffLoginForm());
        return "staff/login";
    }
    
    @PostMapping("/login")
    public String processLogin(@Valid StaffLoginForm form,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "staff/login";
        }
        
        try {
            requestLoginUseCase.execute(new RequestLoginCommand(form.getEmail()));
            redirectAttributes.addFlashAttribute("success", "Login link sent");
        } catch (StaffUserNotFoundException e) {
            result.rejectValue("email", "error.notFound");
            return "staff/login";
        }
        
        return "redirect:/staff/login";
    }
    
    @GetMapping("/auto-login")
    public String autoLogin(@RequestParam("t") String token) {
        return "redirect:/staff/";
    }
}
```

---

## Template

```html
<!-- staff/login.html -->
<div class="staff-login">
    <h1>Staff Login</h1>
    <form th:action="@{/staff/login}" th:object="${form}" method="post">
        <div class="form-group">
            <label>Email</label>
            <input type="email" th:field="*{email}">
            <span th:if="${#fields.hasErrors('email')}" th:errors="*{email}" class="error"></span>
        </div>
        <button type="submit">Send Login Link</button>
    </form>
</div>
```

---

## Verification
- [ ] Login page displays
- [ ] Email validation works
- [ ] Auto-login works
- [ ] Redirects to dashboard

---

## Next
Proceed to **5.5: Dashboard**

