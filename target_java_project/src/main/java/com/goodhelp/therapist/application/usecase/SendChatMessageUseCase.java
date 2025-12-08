package com.goodhelp.therapist.application.usecase;

import com.goodhelp.therapist.application.command.SendChatMessageCommand;
import com.goodhelp.therapist.application.dto.ChatMessageDto;
import com.goodhelp.therapist.domain.model.ChatMessage;
import com.goodhelp.therapist.domain.repository.ChatMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for sending a chat message from therapist to user.
 */
@Service
@Transactional
public class SendChatMessageUseCase {

    private static final Logger logger = LoggerFactory.getLogger(SendChatMessageUseCase.class);

    private final ChatMessageRepository chatMessageRepository;

    public SendChatMessageUseCase(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * Send a message from therapist to user.
     * 
     * @param therapistId the therapist ID
     * @param command the send message command
     * @return the saved message as DTO
     */
    public ChatMessageDto execute(Long therapistId, SendChatMessageCommand command) {
        logger.info("Therapist {} sending message to user {}", therapistId, command.userId());

        String body = command.sanitizedBody();
        if (body.isEmpty()) {
            throw new IllegalArgumentException("Message body cannot be empty");
        }

        ChatMessage message = ChatMessage.fromTherapist(therapistId, command.userId(), body);
        ChatMessage saved = chatMessageRepository.save(message);

        logger.debug("Message saved with ID {}", saved.getId());

        return ChatMessageDto.fromEntity(saved);
    }
}

