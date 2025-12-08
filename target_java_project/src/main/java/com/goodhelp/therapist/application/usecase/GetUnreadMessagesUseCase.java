package com.goodhelp.therapist.application.usecase;

import com.goodhelp.therapist.application.dto.NewMessageItemDto;
import com.goodhelp.therapist.application.dto.UnreadCountResponse;
import com.goodhelp.therapist.domain.model.ChatMessage;
import com.goodhelp.therapist.domain.repository.ChatMessageRepository;
import com.goodhelp.therapist.domain.repository.ChatMessageRepository.UnreadConversationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Use case for retrieving unread messages summary for therapist.
 */
@Service
@Transactional(readOnly = true)
public class GetUnreadMessagesUseCase {

    private static final Logger logger = LoggerFactory.getLogger(GetUnreadMessagesUseCase.class);

    private final ChatMessageRepository chatMessageRepository;
    private final JdbcTemplate jdbcTemplate;

    public GetUnreadMessagesUseCase(
            ChatMessageRepository chatMessageRepository,
            JdbcTemplate jdbcTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Get the total count of unread messages for a therapist.
     */
    public UnreadCountResponse getUnreadCount(Long therapistId) {
        int count = chatMessageRepository.countUnreadFromUsers(therapistId);
        return new UnreadCountResponse(count);
    }

    /**
     * Get list of users with unread messages for a therapist.
     * Returns the latest message preview and unread count for each user.
     */
    public List<NewMessageItemDto> getNewMessagesList(Long therapistId) {
        logger.debug("Getting new messages list for therapist {}", therapistId);

        // Get unread conversations with latest message ID and count
        List<UnreadConversationInfo> unreadConversations = 
            chatMessageRepository.getUnreadConversationsForTherapist(therapistId);

        if (unreadConversations.isEmpty()) {
            return List.of();
        }

        // Get the message IDs
        List<Long> messageIds = unreadConversations.stream()
            .map(UnreadConversationInfo::latestMessageId)
            .toList();

        // Index unread info by message ID for easy lookup
        Map<Long, UnreadConversationInfo> unreadInfoByMessageId = new HashMap<>();
        for (UnreadConversationInfo info : unreadConversations) {
            unreadInfoByMessageId.put(info.latestMessageId(), info);
        }

        // Get the messages
        List<ChatMessage> messages = chatMessageRepository.findByIdIn(messageIds);

        // Get user info via JDBC for the user IDs
        Set<Long> userIds = new HashSet<>();
        for (ChatMessage msg : messages) {
            userIds.add(msg.getUserId());
        }

        // Fetch user data
        Map<Long, UserInfo> userInfoMap = fetchUserInfo(userIds, therapistId);

        // Build response
        List<NewMessageItemDto> result = new ArrayList<>();
        for (ChatMessage message : messages) {
            UnreadConversationInfo info = unreadInfoByMessageId.get(message.getId());
            UserInfo userInfo = userInfoMap.getOrDefault(message.getUserId(), UserInfo.empty());

            result.add(new NewMessageItemDto(
                message.getId(),
                message.getUserId(),
                userInfo.fullName(),
                userInfo.psihologUserNotesName(),
                userInfo.isFullNameSetByUser(),
                message.getBody(),
                message.getSentAt(),
                info != null ? info.unreadCount() : 0
            ));
        }

        return result;
    }

    /**
     * Fetch user info for a set of user IDs.
     */
    private Map<Long, UserInfo> fetchUserInfo(Set<Long> userIds, Long therapistId) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, UserInfo> result = new HashMap<>();

        // Join with user and psiholog_user_notes tables
        String sql = """
            SELECT u.id, u.full_name, u.is_full_name_set_by_user, pun.name as notes_name
            FROM "user" u
            LEFT JOIN psiholog_user_notes pun ON pun.user_id = u.id AND pun.psiholog_id = ?
            WHERE u.id IN (%s)
            """.formatted(
                String.join(",", userIds.stream().map(String::valueOf).toList())
            );

        try {
            jdbcTemplate.query(sql, rs -> {
                Long userId = rs.getLong("id");
                String fullName = rs.getString("full_name");
                boolean isFullNameSetByUser = rs.getBoolean("is_full_name_set_by_user");
                String notesName = rs.getString("notes_name");

                result.put(userId, new UserInfo(
                    fullName != null ? fullName : "",
                    notesName != null ? notesName : "",
                    isFullNameSetByUser
                ));
            }, therapistId);
        } catch (Exception e) {
            logger.warn("Error fetching user info: {}", e.getMessage());
            // Return empty map on error - chat will still work but without user names
        }

        return result;
    }

    private record UserInfo(String fullName, String psihologUserNotesName, boolean isFullNameSetByUser) {
        static UserInfo empty() {
            return new UserInfo("", "", false);
        }
    }
}

