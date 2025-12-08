# Sub-Stage 5.7: Add/Edit Therapist

## Goal
Implement forms for adding new therapists and editing existing ones.

---

## Endpoints

| Method | Route | Handler |
|--------|-------|---------|
| GET | /staff/add-therapist | `showAddForm()` |
| POST | /staff/add-therapist | `addTherapist()` |
| GET | /staff/therapist/{id}/profile | `showEditProfile()` |
| POST | /staff/therapist/{id}/profile | `saveProfile()` |
| GET | /staff/therapist/{id}/settings | `showEditSettings()` |
| POST | /staff/therapist/{id}/settings | `saveSettings()` |

---

## Add Therapist Form

```java
public class AddTherapistForm {
    @NotBlank @Email
    private String email;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    private String birthDate;
    private String worksFrom;
    
    @NotNull
    private Integer sex;  // 1=woman, 2=man
    
    private String profileTemplate;
    
    // Pricing
    private Integer priceIndividual;
    private Integer priceCouple;
    private String currency = "UAH";
}
```

---

## Edit Profile Form

```java
public class EditProfileForm {
    private String firstName;
    private String lastName;
    private String birthDate;
    private String worksFrom;
    private Integer sex;
    private String profileTemplate;
    
    // Rich text fields
    private String biography;
    private String education;
    private String methods;
    private String specializations; // JSON array
    
    // Photo management handled separately
}
```

---

## Controller

```java
@Controller
@RequestMapping("/staff")
public class StaffTherapistEditController {
    
    @GetMapping("/add-therapist")
    @PreAuthorize("hasRole('STAFF_SUPERUSER')")
    public String showAddForm(Model model) {
        model.addAttribute("form", new AddTherapistForm());
        return "staff/therapists/add";
    }
    
    @PostMapping("/add-therapist")
    @PreAuthorize("hasRole('STAFF_SUPERUSER')")
    public String addTherapist(@Valid AddTherapistForm form,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "staff/therapists/add";
        }
        
        var therapist = createTherapistUseCase.execute(CreateTherapistCommand.from(form));
        redirectAttributes.addFlashAttribute("success", "Therapist added");
        return "redirect:/staff/therapist/" + therapist.getId() + "/profile";
    }
    
    @GetMapping("/therapist/{id}/profile")
    public String showEditProfile(@PathVariable Long id, Model model) {
        var therapist = getTherapistUseCase.execute(id);
        model.addAttribute("therapist", therapist);
        model.addAttribute("form", EditProfileForm.from(therapist));
        return "staff/therapists/edit-profile";
    }
    
    @PostMapping("/therapist/{id}/profile")
    public String saveProfile(@PathVariable Long id,
                             @Valid EditProfileForm form,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "staff/therapists/edit-profile";
        }
        
        updateProfileUseCase.execute(new UpdateProfileCommand(id, form));
        redirectAttributes.addFlashAttribute("success", "Profile saved");
        return "redirect:/staff/therapist/" + id + "/profile";
    }
}
```

---

## Image Upload

```java
@PostMapping("/image/upload")
@ResponseBody
public ImageUploadResponse uploadImage(@RequestParam("file") MultipartFile file) {
    var image = imageUploadService.upload(file);
    return new ImageUploadResponse(image.getUrl());
}
```

---

## PHP Reference
- `original_php_project/src/Staff/Controller/AddPsihologController.php`
- `original_php_project/src/Staff/Controller/Psiholog/ProfileController.php`
- `original_php_project/src/Staff/Controller/Psiholog/SettingsController.php`

---

## Verification
- [ ] Add form shows all fields
- [ ] Validation works
- [ ] Profile edit loads existing data
- [ ] Rich text editor works
- [ ] Photo upload works
- [ ] Changes saved correctly

---

## Next
Proceed to **5.8: Blog Management**

