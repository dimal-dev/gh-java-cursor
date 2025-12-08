# Sub-Stage 2.8: Clients Feature

## Goal
Implement client list and user notes functionality.

---

## Endpoints

| Method | Route | Handler | Description |
|--------|-------|---------|-------------|
| GET | /therapist/clients | `clientsList()` | Clients page |
| GET | /therapist/clients/list | `getClientsData()` | AJAX: client list |
| GET | /therapist/clients/{userId}/notes | `viewNotes()` | Notes page |
| POST | /therapist/clients/{userId}/notes | `saveNotes()` | Save notes |

---

## Controller

```java
@Controller
@RequestMapping("/therapist/clients")
public class TherapistClientsController {
    
    private final GetClientsUseCase getClientsUseCase;
    private final GetUserNotesUseCase getNotesUseCase;
    private final SaveUserNotesUseCase saveNotesUseCase;
    
    @GetMapping
    public String clientsList() {
        return "therapist/clients";
    }
    
    @GetMapping("/list")
    @ResponseBody
    public ClientsResponse getClientsData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @AuthenticationPrincipal TherapistUserDetails user) {
        var query = new GetClientsQuery(user.getId(), page, 20, search);
        return getClientsUseCase.execute(query);
    }
    
    @GetMapping("/{userId}/notes")
    public String viewNotes(@PathVariable Long userId, Model model,
                           @AuthenticationPrincipal TherapistUserDetails user) {
        var notes = getNotesUseCase.execute(user.getId(), userId);
        model.addAttribute("notes", notes);
        model.addAttribute("userId", userId);
        return "therapist/user-notes";
    }
    
    @PostMapping("/{userId}/notes")
    public String saveNotes(@PathVariable Long userId,
                           @RequestParam String content,
                           @AuthenticationPrincipal TherapistUserDetails user,
                           RedirectAttributes redirectAttributes) {
        saveNotesUseCase.execute(new SaveNotesCommand(user.getId(), userId, content));
        redirectAttributes.addFlashAttribute("success", "Notes saved");
        return "redirect:/therapist/clients/" + userId + "/notes";
    }
}
```

---

## Response DTOs

```java
public record ClientsResponse(
    List<ClientDto> clients,
    int totalPages,
    int currentPage,
    long totalClients
) {}

public record ClientDto(
    Long userId,
    String name,
    String email,
    int totalConsultations,
    int completedConsultations,
    LocalDateTime lastConsultation,
    boolean hasNotes
) {}

public record UserNotesDto(
    Long userId,
    String userName,
    String content,
    LocalDateTime updatedAt
) {}
```

---

## PHP Reference

| PHP Controller | Java Handler |
|----------------|--------------|
| `ClientsController` | `clientsList()` |
| `ClientsListAjaxController` | `getClientsData()` |
| `UserNotesController::show` | `viewNotes()` |
| `UserNotesController::save` | `saveNotes()` |

---

## Templates

### clients.html
- Table with client list
- Search input
- Pagination
- Link to notes for each client
- Shows consultation count, last visit

### user-notes.html
- Client info header
- Textarea for notes
- Save button
- Back to clients link

---

## Verification
- [ ] Clients list loads with pagination
- [ ] Search filters clients
- [ ] Notes page shows existing notes
- [ ] Can save/update notes
- [ ] Empty notes handled properly

---

## Next
Proceed to **2.9: Chat Feature**

