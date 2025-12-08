# Sub-Stage 5.8: Blog Management

## Goal
Implement blog post listing, creation, and editing.

---

## Endpoints

| Method | Route | Handler |
|--------|-------|---------|
| GET | /staff/blog | `listPosts()` |
| GET | /staff/blog/list | `getPostsData()` (AJAX) |
| GET | /staff/blog/add | `showAddForm()` |
| POST | /staff/blog/add | `addPost()` |
| GET | /staff/blog/{id}/edit | `showEditForm()` |
| POST | /staff/blog/{id}/edit | `savePost()` |

---

## Controller

```java
@Controller
@RequestMapping("/staff/blog")
public class StaffBlogController {
    
    @GetMapping
    public String listPosts() {
        return "staff/blog/list";
    }
    
    @GetMapping("/list")
    @ResponseBody
    public BlogPostListResponse getPostsData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "") String search) {
        return getBlogPostsUseCase.execute(page, 20, search);
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("form", new BlogPostForm());
        model.addAttribute("therapists", getTherapistsForSelectUseCase.execute());
        return "staff/blog/edit";
    }
    
    @PostMapping("/add")
    public String addPost(@Valid BlogPostForm form,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "staff/blog/edit";
        }
        
        var post = createBlogPostUseCase.execute(CreateBlogPostCommand.from(form));
        redirectAttributes.addFlashAttribute("success", "Post created");
        return "redirect:/staff/blog/" + post.getId() + "/edit";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        var post = getBlogPostUseCase.execute(id);
        model.addAttribute("form", BlogPostForm.from(post));
        model.addAttribute("therapists", getTherapistsForSelectUseCase.execute());
        return "staff/blog/edit";
    }
    
    @PostMapping("/{id}/edit")
    public String savePost(@PathVariable Long id,
                          @Valid BlogPostForm form,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "staff/blog/edit";
        }
        
        updateBlogPostUseCase.execute(new UpdateBlogPostCommand(id, form));
        redirectAttributes.addFlashAttribute("success", "Post saved");
        return "redirect:/staff/blog/" + id + "/edit";
    }
}
```

---

## Blog Post Form

```java
public class BlogPostForm {
    @NotBlank
    private String header;
    
    @NotBlank
    private String preview;  // Short excerpt
    
    @NotBlank
    private String body;     // Rich text content (HTML)
    
    private Long therapistId;  // Author
    private Long mainImageId;
    
    @NotNull
    private Integer state;   // 0=draft, 1=published
    
    private String locale;   // Language: uk, ru, en
}
```

---

## Rich Text Editor

Use a JavaScript WYSIWYG editor (e.g., TinyMCE, Froala, or similar):

```html
<textarea id="body" th:field="*{body}"></textarea>
<script>
tinymce.init({
    selector: '#body',
    plugins: 'image link lists',
    toolbar: 'undo redo | formatselect | bold italic | bullist numlist | link image',
    images_upload_url: '/staff/image/upload'
});
</script>
```

---

## PHP Reference
- `original_php_project/src/Staff/Controller/Blog/`

---

## Verification
- [ ] Post list loads
- [ ] Search works
- [ ] Can create new post
- [ ] Rich text editor works
- [ ] Image upload in editor
- [ ] Can edit existing post
- [ ] Publish/unpublish works

---

## Next
Proceed to **5.9: Payouts Management**

