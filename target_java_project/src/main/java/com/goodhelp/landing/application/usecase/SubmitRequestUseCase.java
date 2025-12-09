package com.goodhelp.landing.application.usecase;

import com.goodhelp.landing.application.command.SubmitRequestCommand;
import com.goodhelp.landing.domain.model.UserRequestTherapist;
import com.goodhelp.landing.domain.repository.UserRequestTherapistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Use case for submitting a therapist request.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubmitRequestUseCase {

    private final UserRequestTherapistRepository repository;

    @Transactional
    public Long execute(SubmitRequestCommand command) {
        // Build problem string from topics
        String problem;
        if (command.topics() != null && !command.topics().isEmpty()) {
            problem = command.topics().stream()
                    .collect(Collectors.joining(", "));
        } else {
            problem = "Не указана проблема"; // Default message
        }

        // Create entity
        UserRequestTherapist request = UserRequestTherapist.create(
                command.name(),
                command.email(),
                command.phone(),
                command.channel(),
                problem,
                command.consultationType(),
                command.sex(),
                command.price(),
                command.promocode(),
                command.therapistId(),
                command.lgbtq()
        );

        // Save
        UserRequestTherapist saved = repository.save(request);
        log.info("Created therapist request with ID: {}", saved.getId());

        // TODO: Send notification to admins via Telegram (Stage 7)
        // telegramNotifier.notifyAdminsPsihologRequested(saved);

        return saved.getId();
    }
}

