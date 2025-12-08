# Sub-Stage 4.8: User Settings

## Goal
Implement user profile settings page.

---

## Endpoints

| Method | Route | Handler |
|--------|-------|---------|
| GET | /user/settings | `showSettings()` |
| POST | /user/settings | `saveSettings()` |
| GET | /user/rules | `showRules()` |

---

## Controller

```java
@Controller
@RequestMapping("/user")
public class UserSettingsController {
    
    @GetMapping("/settings")
    public String showSettings(Model model, @AuthenticationPrincipal GoodHelpUserDetails user) {
        var userEntity = userRepository.findById(user.getId()).orElseThrow();
        var form = new UserSettingsForm();
        form.setFullName(userEntity.getFullName());
        form.setTimezone(userEntity.getTimezone());
        
        model.addAttribute("form", form);
        model.addAttribute("email", userEntity.getEmail());
        model.addAttribute("timezones", timezoneService.getAvailableTimezones());
        return "user/settings";
    }
    
    @PostMapping("/settings")
    public String saveSettings(@Valid UserSettingsForm form,
                              BindingResult result,
                              @AuthenticationPrincipal GoodHelpUserDetails user,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user/settings";
        }
        
        updateSettingsUseCase.execute(new UpdateSettingsCommand(
            user.getId(), form.getFullName(), form.getTimezone()));
        
        redirectAttributes.addFlashAttribute("success", "Settings saved");
        return "redirect:/user/settings";
    }
    
    @GetMapping("/rules")
    public String showRules() {
        return "user/rules";
    }
}
```

---

## Form

```java
public class UserSettingsForm {
    @NotBlank(message = "{validation.name.required}")
    @Size(max = 100, message = "{validation.name.tooLong}")
    private String fullName;
    
    @NotBlank(message = "{validation.timezone.required}")
    private String timezone;
}
```

---

## Settings Template

```html
<!-- user/settings.html -->
<main layout:fragment="content">
    <h1 th:text="#{user.settings.title}">Settings</h1>
    
    <div th:if="${success}" class="alert success" th:text="${success}"></div>
    
    <form th:action="@{/user/settings}" th:object="${form}" method="post">
        <div class="form-group">
            <label th:text="#{user.settings.email}">Email</label>
            <input type="email" th:value="${email}" disabled readonly>
            <small th:text="#{user.settings.emailHelp}">Email cannot be changed</small>
        </div>
        
        <div class="form-group">
            <label th:text="#{user.settings.fullName}">Full Name</label>
            <input type="text" th:field="*{fullName}">
            <span th:if="${#fields.hasErrors('fullName')}" th:errors="*{fullName}" class="error"></span>
        </div>
        
        <div class="form-group">
            <label th:text="#{user.settings.timezone}">Timezone</label>
            <select th:field="*{timezone}">
                <option th:each="tz : ${timezones}" 
                        th:value="${tz.id}" 
                        th:text="${tz.displayName}"></option>
            </select>
        </div>
        
        <button type="submit" class="btn-primary" th:text="#{user.settings.save}">
            Save Changes
        </button>
    </form>
</main>
```

---

## Rules Page

Static informational page with booking/cancellation rules:
- Consultation duration
- Cancellation policy
- Rescheduling rules
- Payment information

---

## PHP Reference
- `original_php_project/src/User/Controller/SettingsController.php`
- `original_php_project/src/User/Controller/RulesController.php`

---

## Verification
- [ ] Settings page shows current values
- [ ] Email displayed as readonly
- [ ] Name can be changed
- [ ] Timezone dropdown works
- [ ] Settings saved successfully
- [ ] Rules page displays

---

## Next
Proceed to **4.9: Templates**

