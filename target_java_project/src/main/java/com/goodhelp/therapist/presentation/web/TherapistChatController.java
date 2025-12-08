package com.goodhelp.therapist.presentation.web;

import com.goodhelp.therapist.application.command.SendChatMessageCommand;
import com.goodhelp.therapist.application.dto.*;
import com.goodhelp.therapist.application.usecase.GetChatMessagesUseCase;
import com.goodhelp.therapist.application.usecase.GetUnreadMessagesUseCase;
import com.goodhelp.therapist.application.usecase.GetUserNotesUseCase;
import com.goodhelp.therapist.application.usecase.SendChatMessageUseCase;
import com.goodhelp.therapist.infrastructure.security.TherapistUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for therapist chat functionality.
 * 
 * Provides endpoints for:
 * - Viewing chat conversations with users
 * - Sending/receiving messages
 * - Getting unread message counts
 */
@Controller
@RequestMapping("/therapist/chat")
public class TherapistChatController {

    private final GetChatMessagesUseCase getChatMessagesUseCase;
    private final SendChatMessageUseCase sendChatMessageUseCase;
    private final GetUnreadMessagesUseCase getUnreadMessagesUseCase;
    private final GetUserNotesUseCase getUserNotesUseCase;

    public TherapistChatController(
            GetChatMessagesUseCase getChatMessagesUseCase,
            SendChatMessageUseCase sendChatMessageUseCase,
            GetUnreadMessagesUseCase getUnreadMessagesUseCase,
            GetUserNotesUseCase getUserNotesUseCase) {
        this.getChatMessagesUseCase = getChatMessagesUseCase;
        this.sendChatMessageUseCase = sendChatMessageUseCase;
        this.getUnreadMessagesUseCase = getUnreadMessagesUseCase;
        this.getUserNotesUseCase = getUserNotesUseCase;
    }

    /**
     * Display chat page with a specific user.
     * GET /therapist/chat?userId=X
     */
    @GetMapping
    public String chatPage(
            @RequestParam(required = false) Long userId,
            Model model,
            @AuthenticationPrincipal TherapistUserDetails user) {
        
        if (userId == null || userId <= 0) {
            // Redirect to new messages page if no user specified
            return "redirect:/therapist/chat/list";
        }

        // Get chat messages
        List<ChatMessageDto> messages = getChatMessagesUseCase.execute(user.getId(), userId, 0L);
        Long latestMessageId = getChatMessagesUseCase.getLatestMessageId(messages);

        // Get user name from notes
        String userName = getUserName(user.getId(), userId);

        model.addAttribute("chatMessages", messages);
        model.addAttribute("userName", userName);
        model.addAttribute("userId", userId);
        model.addAttribute("latestChatMessageId", latestMessageId);
        model.addAttribute("therapistId", user.getId());

        return "therapist/chat/chat";
    }

    /**
     * AJAX endpoint to get new messages after a certain ID.
     * GET /therapist/chat/get-messages-ajax?userId=X&latestChatMessageId=Y
     */
    @GetMapping("/get-messages-ajax")
    @ResponseBody
    public ResponseEntity<ChatMessagesAjaxResponse> getMessagesAjax(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") Long latestChatMessageId,
            @AuthenticationPrincipal TherapistUserDetails user) {

        List<ChatMessageDto> messages = getChatMessagesUseCase.execute(
            user.getId(), userId, latestChatMessageId
        );

        Long newLatestId = getChatMessagesUseCase.getLatestMessageId(messages);
        if (newLatestId == 0L && latestChatMessageId > 0) {
            newLatestId = latestChatMessageId;
        }

        // Return messages as data - the template will render them on the client
        return ResponseEntity.ok(new ChatMessagesAjaxResponse(messages, newLatestId));
    }

    /**
     * Send a message to a user.
     * POST /therapist/chat/send-message
     */
    @PostMapping("/send-message")
    public String sendMessage(
            @RequestParam Long userId,
            @RequestParam String body,
            @AuthenticationPrincipal TherapistUserDetails user) {

        if (body != null && !body.trim().isEmpty()) {
            var command = new SendChatMessageCommand(userId, body.trim());
            sendChatMessageUseCase.execute(user.getId(), command);
        }

        return "redirect:/therapist/chat?userId=" + userId;
    }

    /**
     * AJAX endpoint to send a message (returns JSON).
     * POST /therapist/chat/send-message-ajax
     */
    @PostMapping("/send-message-ajax")
    @ResponseBody
    public ResponseEntity<ChatMessageDto> sendMessageAjax(
            @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal TherapistUserDetails user) {

        var command = new SendChatMessageCommand(request.userId(), request.body());
        ChatMessageDto message = sendChatMessageUseCase.execute(user.getId(), command);

        return ResponseEntity.ok(message);
    }

    /**
     * Get unread messages count for sidebar badge.
     * GET /therapist/chat/get-unread-messages-amount-ajax
     */
    @GetMapping("/get-unread-messages-amount-ajax")
    @ResponseBody
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @AuthenticationPrincipal TherapistUserDetails user) {

        UnreadCountResponse response = getUnreadMessagesUseCase.getUnreadCount(user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Display new messages page (list of users with unread messages).
     * GET /therapist/chat - alias when no userId
     */
    @GetMapping("/list")
    public String newMessagesPageAlias(Model model, @AuthenticationPrincipal TherapistUserDetails user) {
        return "therapist/chat/new-messages";
    }

    /**
     * AJAX endpoint for new messages list (for datatable).
     * GET /therapist/chat/new-messages-list-ajax
     */
    @GetMapping("/new-messages-list-ajax")
    @ResponseBody
    public ResponseEntity<List<NewMessageItemDto>> getNewMessagesList(
            @AuthenticationPrincipal TherapistUserDetails user) {

        List<NewMessageItemDto> messages = getUnreadMessagesUseCase.getNewMessagesList(user.getId());
        return ResponseEntity.ok(messages);
    }

    // ==================== Helper Methods ====================

    /**
     * Get display name for a user (combines user's full name with therapist's custom name).
     */
    private String getUserName(Long therapistId, Long userId) {
        try {
            // Get therapist's custom name for this client from notes
            UserNotesDto notes = getUserNotesUseCase.execute(therapistId, userId, "");
            
            // If therapist has set a custom name, use it
            if (notes.clientName() != null && !notes.clientName().isBlank()) {
                return notes.clientName();
            }
            
            // Fallback to a default
            return "Не определен";
        } catch (Exception e) {
            return "Не определен";
        }
    }

    // ==================== Request/Response Records ====================

    /**
     * AJAX request body for sending a message.
     */
    public record SendMessageRequest(Long userId, String body) {}

    /**
     * AJAX response for getting messages.
     */
    public record ChatMessagesAjaxResponse(
        List<ChatMessageDto> messages,
        Long latestChatMessageId
    ) {}
}

