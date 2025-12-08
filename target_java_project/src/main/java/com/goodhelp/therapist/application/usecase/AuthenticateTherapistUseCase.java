package com.goodhelp.therapist.application.usecase;

import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.therapist.application.command.AuthenticateCommand;
import com.goodhelp.therapist.application.dto.AuthenticationResultDto;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistAutologinToken;
import com.goodhelp.therapist.domain.repository.TherapistAutologinTokenRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Use case for authenticating a therapist using an auto-login token.
 * 
 * <p>Flow:</p>
 * <ol>
 *   <li>Receive token from auto-login link</li>
 *   <li>Validate token exists and is not expired</li>
 *   <li>Load associated therapist</li>
 *   <li>Return authentication result for session creation</li>
 * </ol>
 */
@Service
@Validated
@Transactional(readOnly = true)
public class AuthenticateTherapistUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthenticateTherapistUseCase.class);

    private final TherapistAutologinTokenRepository tokenRepository;

    public AuthenticateTherapistUseCase(TherapistAutologinTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Execute authentication with the given token.
     * 
     * @param command the authentication command containing the token
     * @return authentication result
     */
    public AuthenticationResultDto execute(@Valid AuthenticateCommand command) {
        log.debug("Attempting authentication with token: {}...", 
            command.token().substring(0, Math.min(8, command.token().length())));

        TherapistAutologinToken token = tokenRepository.findByToken(command.token())
            .orElse(null);

        if (token == null) {
            log.warn("Authentication failed: invalid token");
            return AuthenticationResultDto.failure("Invalid or expired token");
        }

        Therapist therapist = token.getTherapist();
        
        if (!therapist.isActive()) {
            log.warn("Authentication failed: therapist {} is not active", therapist.getId());
            return AuthenticationResultDto.failure("Account is not active");
        }

        log.info("Authentication successful for therapist: {}", therapist.getId());
        
        return AuthenticationResultDto.success(
            therapist.getId(),
            therapist.getEmail(),
            therapist.getFullName(),
            therapist.getTimezone()
        );
    }
}
