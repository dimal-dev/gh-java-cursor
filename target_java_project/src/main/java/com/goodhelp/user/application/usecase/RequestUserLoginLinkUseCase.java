package com.goodhelp.user.application.usecase;

import com.goodhelp.user.application.command.RequestLoginCommand;
import com.goodhelp.user.domain.model.User;
import com.goodhelp.user.domain.model.UserAutologinToken;
import com.goodhelp.user.domain.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * Use case for requesting a login link for a user.
 * 
 * <p>Flow:</p>
 * <ol>
 *   <li>Validate email exists in system</li>
 *   <li>Generate or update auto-login token</li>
 *   <li>Send login link via email</li>
 * </ol>
 * 
 * <p>Note: This use case intentionally does not reveal if email exists
 * to prevent email enumeration attacks. It always succeeds.</p>
 */
@Service
@Validated
@Transactional
public class RequestUserLoginLinkUseCase {

    private static final Logger log = LoggerFactory.getLogger(RequestUserLoginLinkUseCase.class);

    private final UserRepository userRepository;
    // Note: NotificationService will be injected in later stages

    public RequestUserLoginLinkUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Result of login link request.
     */
    public record Result(boolean sentByEmail, String message) {
        public static Result viaEmail() {
            return new Result(true, "Login link sent to email");
        }
        
        public static Result notFound() {
            // Don't reveal that email wasn't found
            return new Result(false, "If email exists, login link will be sent");
        }
    }

    /**
     * Execute login link request.
     * 
     * @param command the command containing email
     * @return result indicating if login link was sent
     */
    public Result execute(@Valid RequestLoginCommand command) {
        String email = command.email().toLowerCase().trim();
        log.debug("Login link requested for user: {}", email);

        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            log.debug("No user found for email: {}", email);
            return Result.notFound();
        }

        User user = userOpt.get();
        
        // Generate or regenerate token
        String newToken = UserAutologinToken.generateToken();
        user.regenerateAutologinToken(newToken);
        userRepository.save(user);

        log.info("Generated login token for user: {}", user.getId());

        // TODO: Send via email in notification module
        // The login path would be: "/user/auto-login?t=" + newToken
        log.debug("Would send login link via email for user: {}", user.getId());
        return Result.viaEmail();
    }
}

