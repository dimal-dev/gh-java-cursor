# Sub-Stage 7.5: Scheduled Notifications

## Goal
Implement scheduled tasks for session reminders and daily summaries.

---

## Scheduled Tasks

```java
@Component
@Slf4j
public class NotificationScheduler {
    
    private final ConsultationRepository consultationRepository;
    private final TherapistSettingsRepository settingsRepository;
    private final TelegramService telegramService;
    private final TelegramNotificationFormatter formatter;
    
    /**
     * Check for upcoming consultations every 15 minutes
     * Send reminder 1 hour before session
     */
    @Scheduled(fixedRate = 900000) // 15 minutes
    public void checkUpcomingConsultations() {
        log.debug("Checking for upcoming consultations...");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);
        
        // Find consultations starting in ~1 hour (window: 45 min to 1 hour 15 min)
        LocalDateTime windowStart = now.plusMinutes(45);
        LocalDateTime windowEnd = now.plusMinutes(75);
        
        var upcomingConsultations = consultationRepository
            .findBySlotStartTimeBetweenAndState(windowStart, windowEnd, ConsultationState.CREATED);
        
        for (var consultation : upcomingConsultations) {
            sendUpcomingReminder(consultation);
        }
    }
    
    /**
     * Daily summary at 9 AM for therapists
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailySummary() {
        log.info("Sending daily summaries...");
        
        LocalDate today = LocalDate.now();
        
        var therapistsWithSessions = consultationRepository
            .findTherapistIdsWithConsultationsOnDate(today);
        
        for (Long therapistId : therapistsWithSessions) {
            sendDailySummaryToTherapist(therapistId, today);
        }
    }
    
    /**
     * Cleanup expired schedule slots daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupExpiredSlots() {
        log.info("Cleaning up expired schedule slots...");
        
        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);
        int updated = scheduleSlotRepository.markExpired(cutoff);
        
        log.info("Marked {} slots as expired", updated);
    }
    
    private void sendUpcomingReminder(UserConsultation consultation) {
        settingsRepository.findByTherapistId(consultation.getTherapistId())
            .filter(s -> s.getTelegramChatId() != null)
            .ifPresent(settings -> {
                String message = formatter.formatUpcomingReminder(consultation.toInfo());
                telegramService.sendMessage(settings.getTelegramChatId(), message);
                log.info("Sent reminder for consultation {} to therapist {}", 
                    consultation.getId(), consultation.getTherapistId());
            });
    }
    
    private void sendDailySummaryToTherapist(Long therapistId, LocalDate date) {
        var sessions = consultationRepository
            .findByTherapistIdAndDate(therapistId, date);
        
        if (sessions.isEmpty()) return;
        
        settingsRepository.findByTherapistId(therapistId)
            .filter(s -> s.getTelegramChatId() != null)
            .ifPresent(settings -> {
                String message = formatter.formatDailySummary(sessions, date);
                telegramService.sendMessage(settings.getTelegramChatId(), message);
            });
    }
}
```

---

## Daily Summary Format

```java
public String formatDailySummary(List<ConsultationInfo> sessions, LocalDate date) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("📅 <b>Your Schedule for %s</b>\n\n", 
        date.format(DateTimeFormatter.ofPattern("MMMM d"))));
    
    sb.append(String.format("You have %d session(s) today:\n\n", sessions.size()));
    
    for (ConsultationInfo session : sessions) {
        sb.append(String.format("⏰ %s - %s (%s)\n", 
            session.time(), 
            session.clientName(),
            session.type()));
    }
    
    sb.append("\nHave a productive day! 💪");
    return sb.toString();
}
```

---

## Repository Methods Needed

```java
// ConsultationRepository
List<UserConsultation> findBySlotStartTimeBetweenAndState(
    LocalDateTime start, LocalDateTime end, ConsultationState state);

List<Long> findTherapistIdsWithConsultationsOnDate(LocalDate date);

List<UserConsultation> findByTherapistIdAndDate(Long therapistId, LocalDate date);

// ScheduleSlotRepository
@Modifying
@Query("UPDATE ScheduleSlot s SET s.status = 'EXPIRED' " +
       "WHERE s.status = 'AVAILABLE' AND s.startTime < :cutoff")
int markExpired(@Param("cutoff") LocalDateTime cutoff);
```

---

## PHP Reference
- `original_php_project/src/Notification/Command/NotifyTelegramCommand.php`

---

## Verification
- [ ] Reminders sent 1 hour before
- [ ] Daily summaries sent at 9 AM
- [ ] Expired slots cleaned up
- [ ] Only therapists with Telegram linked receive notifications
- [ ] Timezone handling correct

---

## Stage 7 Complete
Verify all notification features work, then proceed to **Stage 8: Integration & Polish**.

