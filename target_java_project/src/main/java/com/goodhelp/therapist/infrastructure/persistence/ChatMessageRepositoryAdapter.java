package com.goodhelp.therapist.infrastructure.persistence;

import com.goodhelp.therapist.domain.model.ChatMessage;
import com.goodhelp.therapist.domain.model.MessageStatus;
import com.goodhelp.therapist.domain.model.SenderType;
import com.goodhelp.therapist.domain.repository.ChatMessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementing ChatMessageRepository using JPA.
 */
@Component
public class ChatMessageRepositoryAdapter implements ChatMessageRepository {

    private final JpaChatMessageRepository jpaRepository;

    public ChatMessageRepositoryAdapter(JpaChatMessageRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public ChatMessage save(ChatMessage message) {
        return jpaRepository.save(message);
    }

    @Override
    public Optional<ChatMessage> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ChatMessage> findConversationMessages(Long userId, Long therapistId, Long afterId, int limit) {
        return jpaRepository.findConversationMessagesAfterId(
            userId, 
            therapistId, 
            afterId != null ? afterId : 0L, 
            PageRequest.of(0, limit)
        );
    }

    @Override
    public int countUnreadFromUsers(Long therapistId) {
        return jpaRepository.countUnreadFromUsers(
            therapistId, 
            SenderType.USER, 
            MessageStatus.UNREAD
        );
    }

    @Override
    public List<UnreadConversationInfo> getUnreadConversationsForTherapist(Long therapistId) {
        List<Object[]> results = jpaRepository.getUnreadConversationsNative(
            therapistId,
            SenderType.USER.getValue(),
            MessageStatus.UNREAD.getValue()
        );
        
        return results.stream()
            .map(row -> new UnreadConversationInfo(
                ((Number) row[0]).longValue(),  // latestMessageId
                ((Number) row[1]).longValue(),  // userId
                ((Number) row[2]).intValue()    // unreadCount
            ))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(List<Long> messageIds) {
        if (messageIds != null && !messageIds.isEmpty()) {
            jpaRepository.updateStatusByIds(messageIds, MessageStatus.READ);
        }
    }

    @Override
    public List<ChatMessage> findByIdIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findByIdInOrderByIdDesc(ids);
    }
}

