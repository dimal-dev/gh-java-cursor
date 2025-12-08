package com.goodhelp.therapist.application.dto;

/**
 * Response DTO for AJAX chat messages request.
 */
public record ChatMessagesResponse(
    String messagesHtml,
    Long latestChatMessageId
) {
    /**
     * Create empty response.
     */
    public static ChatMessagesResponse empty() {
        return new ChatMessagesResponse("", 0L);
    }
}

