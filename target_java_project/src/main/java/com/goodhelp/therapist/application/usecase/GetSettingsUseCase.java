package com.goodhelp.therapist.application.usecase;

import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.therapist.application.dto.SettingsDto;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistAutologinToken;
import com.goodhelp.therapist.domain.repository.TherapistAutologinTokenRepository;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import com.goodhelp.therapist.domain.repository.TherapistSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for retrieving therapist settings.
 */
@Service
@Transactional(readOnly = true)
public class GetSettingsUseCase {

    private static final String TELEGRAM_SETUP_PREFIX = "psiholog_";

    private final TherapistRepository therapistRepository;
    private final TherapistSettingsRepository settingsRepository;
    private final TherapistAutologinTokenRepository autologinTokenRepository;

    public GetSettingsUseCase(
            TherapistRepository therapistRepository,
            TherapistSettingsRepository settingsRepository,
            TherapistAutologinTokenRepository autologinTokenRepository) {
        this.therapistRepository = therapistRepository;
        this.settingsRepository = settingsRepository;
        this.autologinTokenRepository = autologinTokenRepository;
    }

    /**
     * Execute get settings query.
     *
     * @param therapistId the therapist ID
     * @return settings DTO
     */
    public SettingsDto execute(Long therapistId) {
        Therapist therapist = therapistRepository.findById(therapistId)
            .orElseThrow(() -> new ResourceNotFoundException("Therapist", therapistId));

        String telegramSetupToken = getTelegramSetupToken(therapistId);

        return settingsRepository.findByTherapistId(therapistId)
            .map(settings -> SettingsDto.fromEntity(settings, therapist.getEmail(), telegramSetupToken))
            .orElseGet(() -> SettingsDto.defaultSettings(therapist.getEmail(), telegramSetupToken));
    }

    /**
     * Get the Telegram setup token for the therapist.
     * Format: psiholog_{autologin_token}
     */
    private String getTelegramSetupToken(Long therapistId) {
        return autologinTokenRepository.findByTherapistId(therapistId)
            .map(TherapistAutologinToken::getToken)
            .map(token -> TELEGRAM_SETUP_PREFIX + token)
            .orElse("");
    }
}

