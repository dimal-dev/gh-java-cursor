package com.goodhelp.booking.domain.service;

import com.goodhelp.booking.domain.model.ScheduleSlot;
import com.goodhelp.booking.domain.model.SlotStatus;
import com.goodhelp.therapist.domain.model.Therapist;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Domain service for schedule-related business logic that spans multiple entities.
 * 
 * <p>Handles:</p>
 * <ul>
 *   <li>Generating time slots based on weekly templates</li>
 *   <li>Calculating available slots considering time caps</li>
 *   <li>Slot availability validation</li>
 * </ul>
 */
@Service
public class ScheduleDomainService {

    /**
     * Generate available time slots for a therapist for a week.
     * 
     * @param therapist the therapist
     * @param weekStart the start of the week (Monday)
     * @param hoursPerDay set of hours (0-23) that should be available each day
     * @return list of available schedule slots
     */
    public List<ScheduleSlot> generateWeeklySlots(
            Therapist therapist,
            LocalDate weekStart,
            Set<Integer> hoursPerDay) {
        
        List<ScheduleSlot> slots = new ArrayList<>();
        
        // Generate slots for each day of the week
        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            LocalDate date = weekStart.plusDays(dayOffset);
            
            for (Integer hour : hoursPerDay) {
                LocalDateTime slotTime = LocalDateTime.of(date, LocalTime.of(hour, 0));
                
                // Convert from therapist's timezone to UTC for storage
                ZonedDateTime therapistZoned = slotTime.atZone(ZoneId.of(therapist.getTimezone()));
                LocalDateTime utcTime = therapistZoned.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
                
                ScheduleSlot slot = ScheduleSlot.createAvailable(therapist, utcTime);
                slots.add(slot);
            }
        }
        
        return slots;
    }

    /**
     * Check if a slot is bookable considering time cap.
     * 
     * <p>A slot is bookable if:</p>
     * <ul>
     *   <li>Its status is AVAILABLE</li>
     *   <li>It's in the future</li>
     *   <li>It's after the time cap (minimum booking advance)</li>
     * </ul>
     * 
     * @param slot the slot to check
     * @param now current time
     * @param timeCapHours minimum hours in advance for booking
     * @return true if slot can be booked
     */
    public boolean isSlotBookable(ScheduleSlot slot, LocalDateTime now, int timeCapHours) {
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            return false;
        }
        
        LocalDateTime earliestBookableTime = now.plusHours(timeCapHours);
        return slot.getAvailableAt().isAfter(earliestBookableTime);
    }

    /**
     * Filter slots to only those that are bookable.
     * 
     * @param slots list of slots to filter
     * @param now current time
     * @param timeCapHours minimum hours in advance for booking
     * @return filtered list of bookable slots
     */
    public List<ScheduleSlot> filterBookableSlots(
            List<ScheduleSlot> slots, 
            LocalDateTime now, 
            int timeCapHours) {
        
        return slots.stream()
            .filter(slot -> isSlotBookable(slot, now, timeCapHours))
            .toList();
    }

    /**
     * Group slots by date (in specified timezone).
     * 
     * @param slots the slots to group
     * @param timezone the timezone for grouping
     * @return slots grouped by local date
     */
    public java.util.Map<LocalDate, List<ScheduleSlot>> groupSlotsByDate(
            List<ScheduleSlot> slots,
            String timezone) {
        
        return slots.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                slot -> slot.getAvailableAtInZone(timezone).toLocalDate()
            ));
    }

    /**
     * Calculate the next available slot time for a therapist.
     * 
     * @param slots sorted list of available slots
     * @param now current time
     * @param timeCapHours minimum hours in advance
     * @return the next bookable slot time, or empty if none
     */
    public java.util.Optional<LocalDateTime> findNextAvailableTime(
            List<ScheduleSlot> slots,
            LocalDateTime now,
            int timeCapHours) {
        
        LocalDateTime earliestTime = now.plusHours(timeCapHours);
        
        return slots.stream()
            .filter(slot -> slot.getStatus() == SlotStatus.AVAILABLE)
            .filter(slot -> slot.getAvailableAt().isAfter(earliestTime))
            .map(ScheduleSlot::getAvailableAt)
            .min(LocalDateTime::compareTo);
    }

    /**
     * Mark past available slots as expired.
     * 
     * @param slots slots to check
     * @param now current time
     * @return list of slots that were expired
     */
    public List<ScheduleSlot> expirePastSlots(List<ScheduleSlot> slots, LocalDateTime now) {
        List<ScheduleSlot> expired = new ArrayList<>();
        
        for (ScheduleSlot slot : slots) {
            if (slot.getStatus() == SlotStatus.AVAILABLE && slot.getAvailableAt().isBefore(now)) {
                slot.expire();
                expired.add(slot);
            }
        }
        
        return expired;
    }
}
