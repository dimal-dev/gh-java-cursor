package com.goodhelp.therapist.presentation.web;

import com.goodhelp.therapist.application.command.SaveUserNotesCommand;
import com.goodhelp.therapist.application.dto.ClientDto;
import com.goodhelp.therapist.application.dto.UserNotesDto;
import com.goodhelp.therapist.application.query.GetClientsQuery;
import com.goodhelp.therapist.application.usecase.GetClientsUseCase;
import com.goodhelp.therapist.application.usecase.GetUserNotesUseCase;
import com.goodhelp.therapist.application.usecase.SaveUserNotesUseCase;
import com.goodhelp.therapist.infrastructure.security.TherapistUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for therapist client management.
 * 
 * Provides endpoints for:
 * - Viewing list of clients (users who had consultations)
 * - Managing private notes about clients
 */
@Controller
@RequestMapping("/therapist/clients")
public class TherapistClientsController {

    private final GetClientsUseCase getClientsUseCase;
    private final GetUserNotesUseCase getUserNotesUseCase;
    private final SaveUserNotesUseCase saveUserNotesUseCase;

    public TherapistClientsController(
            GetClientsUseCase getClientsUseCase,
            GetUserNotesUseCase getUserNotesUseCase,
            SaveUserNotesUseCase saveUserNotesUseCase) {
        this.getClientsUseCase = getClientsUseCase;
        this.getUserNotesUseCase = getUserNotesUseCase;
        this.saveUserNotesUseCase = saveUserNotesUseCase;
    }

    /**
     * Display the clients list page.
     * GET /therapist/clients
     */
    @GetMapping
    public String clientsList(
            Model model,
            @AuthenticationPrincipal TherapistUserDetails user) {
        
        return "therapist/clients";
    }

    /**
     * AJAX endpoint to get clients list data.
     * GET /therapist/clients/list
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<ClientsListResponse> getClientsList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int perpage,
            @RequestParam(defaultValue = "") String search,
            @AuthenticationPrincipal TherapistUserDetails user) {

        var query = new GetClientsQuery(user.getId(), page, perpage, search.isBlank() ? null : search);
        var result = getClientsUseCase.execute(query);

        return ResponseEntity.ok(new ClientsListResponse(
            result.clients(),
            new MetaInfo(result.currentPage(), result.pageSize(), result.totalCount(), result.totalPages())
        ));
    }

    /**
     * Display user notes page.
     * GET /therapist/clients/{userId}/notes or /therapist/user-notes?userId=X
     */
    @GetMapping("/{userId}/notes")
    public String viewNotes(
            @PathVariable Long userId,
            Model model,
            @AuthenticationPrincipal TherapistUserDetails user) {
        
        // Get notes for this user (empty if none exist)
        UserNotesDto notes = getUserNotesUseCase.execute(user.getId(), userId, "");
        
        model.addAttribute("notes", notes);
        model.addAttribute("userId", userId);
        
        return "therapist/user-notes";
    }

    /**
     * Alternative route for user notes (matches PHP: /therapist/user-notes?userId=X).
     * GET /therapist/user-notes
     */
    @GetMapping("/user-notes")
    public String viewNotesAlternative(
            @RequestParam Long userId,
            Model model,
            @AuthenticationPrincipal TherapistUserDetails user) {
        
        return viewNotes(userId, model, user);
    }

    /**
     * Save user notes.
     * POST /therapist/clients/{userId}/notes
     */
    @PostMapping("/{userId}/notes")
    public String saveNotes(
            @PathVariable Long userId,
            @RequestParam(name = "newName", required = false) String clientName,
            @RequestParam(name = "notes", required = false) String notes,
            @AuthenticationPrincipal TherapistUserDetails user,
            RedirectAttributes redirectAttributes) {
        
        var command = new SaveUserNotesCommand(userId, notes, clientName);
        saveUserNotesUseCase.execute(user.getId(), command);
        
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/therapist/clients/" + userId + "/notes";
    }

    /**
     * Alternative route for saving notes (matches PHP: /therapist/user-notes POST).
     * POST /therapist/user-notes
     */
    @PostMapping("/user-notes")
    public String saveNotesAlternative(
            @RequestParam Long userId,
            @RequestParam(name = "newName", required = false) String clientName,
            @RequestParam(name = "notes", required = false) String notes,
            @AuthenticationPrincipal TherapistUserDetails user,
            RedirectAttributes redirectAttributes) {
        
        var command = new SaveUserNotesCommand(userId, notes, clientName);
        saveUserNotesUseCase.execute(user.getId(), command);
        
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/therapist/user-notes?userId=" + userId;
    }

    // ==================== Request/Response Records ====================

    /**
     * Response for clients list AJAX endpoint.
     */
    public record ClientsListResponse(
        List<ClientDto> data,
        MetaInfo meta
    ) {}

    /**
     * Pagination meta info.
     */
    public record MetaInfo(int page, int perpage, int total, int totalPages) {}
}

