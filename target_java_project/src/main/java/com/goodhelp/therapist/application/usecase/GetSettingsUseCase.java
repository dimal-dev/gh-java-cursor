package com.goodhelp.therapist.application.usecase;

import com.goodhelp.common.exception.ResourceNotFoundException;
import com.goodhelp.common.service.StringHelper;
import com.goodhelp.therapist.application.dto.SettingsDto;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistAutologinToken;
import com.goodhelp.therapist.domain.repository.TherapistAutologinTokenRepository;
import com.goodhelp.therapist.domain.repository.TherapistRepository;
import com.goodhelp.therapist.domain.repository.TherapistSettingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
@Transactional(readOnly = true)
public class GetSettingsUseCase {


    private final String setupPrefix;

    private final TherapistRepository therapistRepository;
    private final TherapistSettingsRepository settingsRepository;
    private final TherapistAutologinTokenRepository autologinTokenRepository;

    public GetSettingsUseCase(
            @Value("${goodhelp.telegram.setup-prefix}") String setupPrefix,
            TherapistRepository therapistRepository,
            TherapistSettingsRepository settingsRepository,
            TherapistAutologinTokenRepository autologinTokenRepository) {
        this.therapistRepository = therapistRepository;
        this.settingsRepository = settingsRepository;
        this.autologinTokenRepository = autologinTokenRepository;
        this.setupPrefix = setupPrefix;
    }

    public SettingsDto execute(Long therapistId) {
        Therapist therapist = therapistRepository.findById(therapistId)
            .orElseThrow(() -> new ResourceNotFoundException("Therapist", therapistId));

        String telegramSetupToken = getTelegramSetupToken(therapistId);

        return settingsRepository.findByTherapistId(therapistId)
            .map(settings -> SettingsDto.fromEntity(settings, therapist.getEmail(), telegramSetupToken))
            .orElseGet(() -> SettingsDto.defaultSettings(therapist.getEmail(), telegramSetupToken));
    }

    private String getTelegramSetupToken(Long therapistId) {
        TherapistAutologinToken autoLogin = autologinTokenRepository.findByTherapistId(therapistId)
                        .orElseThrow(() -> new ResourceNotFoundException("Please add autologin token in db for the therapist " + therapistId));
        return setupPrefix + StringHelper.md5(autoLogin.getToken());
    }
}

