package com.goodhelp.therapist.application.dto;

import java.time.LocalDateTime;

/**
 * DTO for a single item in the new messages list.
 * Contains the latest message preview and unread count for a conversation.
 */
public record NewMessageItemDto(
    Long id,
    Long userId,
    String fullName,
    String psihologUserNotesName,
    boolean isFullNameSetByUser,
    String body,
    LocalDateTime sentAt,
    int unreadMessagesAmount
) {
    /**
     * Get the display name for the user.
     * Combines full name and therapist's custom name if both exist.
     */
    public String getDisplayName() {
        StringBuilder name = new StringBuilder();
        if (fullName != null && !fullName.isBlank()) {
            name.append(fullName);
        }
        if (psihologUserNotesName != null && !psihologUserNotesName.isBlank()) {
            if (name.length() > 0) {
                name.append(" / ");
            }
            name.append(psihologUserNotesName);
        }
        return name.toString();
    }
}

