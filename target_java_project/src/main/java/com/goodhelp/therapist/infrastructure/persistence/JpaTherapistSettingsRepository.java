package com.goodhelp.therapist.infrastructure.persistence;

import com.goodhelp.therapist.domain.model.TherapistSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for TherapistSettings entity.
 * Manages therapist configuration and preferences.
 */
@Repository
public interface JpaTherapistSettingsRepository extends JpaRepository<TherapistSettings, Long> {

    /**
     * Find settings by therapist ID.
     */
    @Query("SELECT s FROM TherapistSettings s WHERE s.therapist.id = :therapistId")
    Optional<TherapistSettings> findByTherapistId(@Param("therapistId") Long therapistId);

    /**
     * Find settings by Telegram chat ID.
     * Used to find therapist when receiving Telegram messages.
     */
    Optional<TherapistSettings> findByTelegramChatId(String telegramChatId);
}

