# Sub-Stage 5.6: Therapist List Management

## Goal
Implement therapist listing page with search and pagination.

---

## Endpoints

| Method | Route | Handler |
|--------|-------|---------|
| GET | /staff/therapists | `list()` |
| GET | /staff/therapists/list | `getListData()` (AJAX) |

---

## Controller

```java
@Controller
@RequestMapping("/staff/therapists")
public class StaffTherapistsController {
    
    @GetMapping
    public String list() {
        return "staff/therapists/list";
    }
    
    @GetMapping("/list")
    @ResponseBody
    public TherapistListResponse getListData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String status) {
        var query = new GetTherapistsQuery(page, 20, search, status);
        return getTherapistsUseCase.execute(query);
    }
}
```

---

## Response DTO

```java
public record TherapistListResponse(
    List<TherapistListItemDto> items,
    int currentPage,
    int totalPages,
    long totalItems
) {}

public record TherapistListItemDto(
    Long id,
    String email,
    String fullName,
    String status,
    int consultationsCount,
    LocalDateTime lastActive,
    String photoUrl
) {}
```

---

## Template Features

- Table with sortable columns
- Search input
- Status filter (active/inactive)
- Pagination
- Click row to edit therapist
- Actions column (edit, view profile)

```html
<table class="data-table">
    <thead>
        <tr>
            <th>Photo</th>
            <th>Name</th>
            <th>Email</th>
            <th>Status</th>
            <th>Consultations</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody id="therapists-table-body">
        <!-- Populated via JavaScript -->
    </tbody>
</table>
```

---

## PHP Reference
- `original_php_project/src/Staff/Controller/PsihologsController.php`
- `original_php_project/src/Staff/Controller/PsihologsListAjaxController.php`

---

## Verification
- [ ] Table loads with data
- [ ] Search filters results
- [ ] Pagination works
- [ ] Edit links work

---

## Next
Proceed to **5.7: Add/Edit Therapist**

