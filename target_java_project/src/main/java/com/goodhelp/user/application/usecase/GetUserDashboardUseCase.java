package com.goodhelp.user.application.usecase;

import com.goodhelp.common.service.DateLocalizedHelper;
import com.goodhelp.common.service.TimezoneHelper;
import com.goodhelp.therapist.domain.model.Therapist;
import com.goodhelp.therapist.domain.model.TherapistProfile;
import com.goodhelp.user.application.dto.ConsultationDto;
import com.goodhelp.user.application.dto.LatestTherapistDto;
import com.goodhelp.user.application.dto.UserDashboardDto;
import com.goodhelp.user.domain.model.User;
import com.goodhelp.user.domain.model.UserConsultation;
import com.goodhelp.user.domain.repository.UserConsultationRepository;
import com.goodhelp.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Use case for fetching user dashboard data.
 */
@Service
@Transactional(readOnly = true)
public class GetUserDashboardUseCase {

    private final UserRepository userRepository;
    private final UserConsultationRepository consultationRepository;
    private final DateLocalizedHelper dateLocalizedHelper;
    private final TimezoneHelper timezoneHelper;

    public GetUserDashboardUseCase(
            UserRepository userRepository,
            UserConsultationRepository consultationRepository,
            DateLocalizedHelper dateLocalizedHelper,
            TimezoneHelper timezoneHelper) {
        this.userRepository = userRepository;
        this.consultationRepository = consultationRepository;
        this.dateLocalizedHelper = dateLocalizedHelper;
        this.timezoneHelper = timezoneHelper;
    }

    /**
     * Get dashboard data for a user.
     * 
     * @param userId the user ID
     * @param rulesRead whether the user has read the rules (from cookie)
     * @param showSuccessfullyBookedMessage whether to show success message
     * @return dashboard DTO
     */
    public UserDashboardDto execute(Long userId, boolean rulesRead, boolean showSuccessfullyBookedMessage) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Get user timezone info
        String timezone = user.getTimezone();
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime userTime = ZonedDateTime.now(zoneId);
        int timezoneOffset = userTime.getOffset().getTotalSeconds();
        String timezoneLabel = timezoneHelper.getLabelForOffset(timezoneOffset, timezone);

        // Get upcoming consultations
        List<UserConsultation> upcomingConsultations = consultationRepository
            .findUpcomingByUserIdOrderByStartTime(userId);

        // Get closest consultation (first one)
        ConsultationDto nextConsultation = null;
        List<ConsultationDto> remainingConsultations = new ArrayList<>();
        
        if (!upcomingConsultations.isEmpty()) {
            UserConsultation closest = upcomingConsultations.get(0);
            nextConsultation = toConsultationDto(closest, user.getLocale(), timezone);
            
            // Remaining consultations (skip first)
            for (int i = 1; i < upcomingConsultations.size(); i++) {
                remainingConsultations.add(toConsultationDto(upcomingConsultations.get(i), user.getLocale(), timezone));
            }
        }

        // Get latest therapist if no consultations
        LatestTherapistDto latestTherapist = null;
        if (upcomingConsultations.isEmpty()) {
            latestTherapist = findLatestTherapist(userId);
        }

        // Wallet balance (billing module not implemented yet, so null for now)
        Integer walletBalance = null;
        String walletCurrency = "UAH";

        // User name
        String userName = user.getFullName();
        if (userName == null || userName.isBlank()) {
            userName = "User";
        }

        return new UserDashboardDto(
            userName,
            user.isFullNameSetByUser(),
            rulesRead,
            walletBalance,
            walletCurrency,
            nextConsultation,
            remainingConsultations,
            0, // unreadMessages - will be implemented in chat sub-stage
            timezone,
            timezoneOffset,
            timezoneLabel,
            latestTherapist,
            showSuccessfullyBookedMessage
        );
    }

    private ConsultationDto toConsultationDto(UserConsultation consultation, String locale, String timezone) {
        Therapist therapist = consultation.getTherapist();
        TherapistProfile profile = therapist.getProfile();
        
        String therapistName = profile != null ? profile.getFullName() : "Unknown";
        String therapistPhotoUrl = getTherapistPhotoUrl(profile);
        
        LocalDateTime scheduledAt = consultation.getStartTime();
        if (scheduledAt == null) {
            scheduledAt = LocalDateTime.now(); // Fallback
        }
        
        // Convert to user's timezone for display
        ZonedDateTime scheduledAtZoned = scheduledAt.atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ZoneId.of(timezone));
        LocalDateTime scheduledAtUserTz = scheduledAtZoned.toLocalDateTime();
        
        String scheduledAtFormatted = dateLocalizedHelper.getDateTimeGoodLookingLabel(
            scheduledAtUserTz, locale
        );
        
        // Check if can be cancelled (24+ hours before)
        LocalDateTime now = LocalDateTime.now();
        boolean canBeCancelled = consultation.canBeCancelledByUser(now);
        
        // Timestamp for JavaScript
        long scheduledAtTimestamp = scheduledAtZoned.toEpochSecond();
        
        return new ConsultationDto(
            consultation.getId(),
            therapistName,
            therapistPhotoUrl,
            scheduledAtUserTz,
            scheduledAtFormatted,
            canBeCancelled,
            consultation.getState().name(),
            scheduledAtTimestamp
        );
    }

    private String getTherapistPhotoUrl(TherapistProfile profile) {
        if (profile == null || profile.getProfileTemplate() == null) {
            return "/assets/landing/img/placeholder-therapist.jpg";
        }
        return "/assets/landing/img/select-psiholog/" + profile.getProfileTemplate() + ".jpg";
    }

    /**
     * Find the latest therapist the user interacted with.
     * For now, returns null - this would require OrderRepository which is in billing module.
     * TODO: Implement when billing module is available
     */
    private LatestTherapistDto findLatestTherapist(Long userId) {
        // Try to find from latest consultation (even if completed)
        Optional<UserConsultation> latestConsultation = consultationRepository
            .findByUserId(userId)
            .stream()
            .findFirst();
        
        if (latestConsultation.isPresent()) {
            Therapist therapist = latestConsultation.get().getTherapist();
            TherapistProfile profile = therapist.getProfile();
            if (profile != null) {
                return new LatestTherapistDto(
                    therapist.getId(),
                    profile.getFirstName(),
                    profile.getLastName(),
                    getTherapistPhotoUrl(profile)
                );
            }
        }
        
        // TODO: If no consultations, check OrderRepository for latest purchase
        // This requires billing module
        
        return null;
    }
}

