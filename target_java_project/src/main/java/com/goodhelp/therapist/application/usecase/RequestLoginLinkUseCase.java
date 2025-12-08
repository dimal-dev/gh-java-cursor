package com.goodhelp.therapist.application.usecase;

import com.goodhelp.therapist.application.command.RequestLoginCommand;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistAutologinToken;
import com.goodhelp.therapist.domain.repository.TherapistAutologinTokenRepository;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * Use case for requesting a login link.
 * 
 * <p>Flow:</p>
 * <ol>
 *   <li>Validate email exists in system</li>
 *   <li>Generate or update auto-login token</li>
 *   <li>Send login link (email/Telegram)</li>
 * </ol>
 * 
 * <p>Note: This use case intentionally does not reveal if email exists
 * to prevent email enumeration attacks. It always succeeds.</p>
 */
@Service
@Validated
@Transactional
public class RequestLoginLinkUseCase {

    private static final Logger log = LoggerFactory.getLogger(RequestLoginLinkUseCase.class);

    private final TherapistRepository therapistRepository;
    private final TherapistAutologinTokenRepository tokenRepository;
    // Note: NotificationService will be injected in later stages

    public RequestLoginLinkUseCase(
            TherapistRepository therapistRepository,
            TherapistAutologinTokenRepository tokenRepository) {
        this.therapistRepository = therapistRepository;
        this.tokenRepository = tokenRepository;
    }

    /**
     * Result of login link request.
     */
    public record Result(boolean sentByEmail, boolean sentByTelegram, String message) {
        public static Result viaEmail() {
            return new Result(true, false, "Login link sent to email");
        }
        
        public static Result viaTelegram() {
            return new Result(false, true, "Login link sent to Telegram");
        }
        
        public static Result notFound() {
            // Don't reveal that email wasn't found
            return new Result(false, false, "If email exists, login link will be sent");
        }
    }

    /**
     * Execute login link request.
     * 
     * @param command the command containing email
     * @return result indicating how login link was sent (or not found message)
     */
    public Result execute(@Valid RequestLoginCommand command) {
        String email = command.email().toLowerCase().trim();
        log.debug("Login link requested for: {}", email);

        Optional<Therapist> therapistOpt = therapistRepository.findByEmail(email);
        
        if (therapistOpt.isEmpty()) {
            log.debug("No therapist found for email: {}", email);
            return Result.notFound();
        }

        Therapist therapist = therapistOpt.get();
        
        if (!therapist.isActive()) {
            log.warn("Login link requested for inactive therapist: {}", therapist.getId());
            return Result.notFound();
        }

        // Generate or regenerate token
        String newToken = TherapistAutologinToken.generateToken();
        therapist.regenerateAutologinToken(newToken);
        therapistRepository.save(therapist);

        String loginPath = "/therapist/auto-login?t=" + newToken;
        log.info("Generated login token for therapist: {}", therapist.getId());

        // Send notification (Telegram preferred if linked)
        if (therapist.isTelegramLinked()) {
            // TODO: Send via Telegram in notification module
            log.debug("Would send login link via Telegram for therapist: {}", therapist.getId());
            return Result.viaTelegram();
        } else {
            // TODO: Send via email in notification module
            log.debug("Would send login link via email for therapist: {}", therapist.getId());
            return Result.viaEmail();
        }
    }
}
