package com.goodhelp.common.service;

import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Helper service for timezone operations.
 * Migrated from PHP: App\Common\Service\TimezoneHelper
 */
@Service
public class TimezoneHelper {
    
    public static final String DEFAULT_TIMEZONE = "Europe/Kiev";
    
    /**
     * Get a human-readable label for a timezone offset.
     * 
     * @param offsetSeconds Offset in seconds from UTC
     * @return Label like "UTC+2" or "UTC-5"
     */
    public String getLabelForOffset(int offsetSeconds) {
        return getLabelForOffset(offsetSeconds, null);
    }
    
    /**
     * Get a human-readable label for a timezone offset.
     * 
     * @param offsetSeconds Offset in seconds from UTC
     * @param timezoneName Optional timezone name to append
     * @return Label like "UTC+2 (Europe/Kiev)"
     */
    public String getLabelForOffset(int offsetSeconds, String timezoneName) {
        int hours = offsetSeconds / 3600;
        int minutes = Math.abs((offsetSeconds % 3600) / 60);
        
        StringBuilder label = new StringBuilder("UTC");
        if (hours >= 0) {
            label.append("+");
        }
        label.append(hours);
        
        if (minutes > 0) {
            label.append(":").append(String.format("%02d", minutes));
        }
        
        if (timezoneName != null && !timezoneName.isEmpty()) {
            label.append(" (").append(timezoneName).append(")");
        }
        
        return label.toString();
    }
    
    /**
     * Get the current offset for a timezone.
     * 
     * @param timezone Timezone identifier (e.g., "Europe/Kiev")
     * @return Offset in seconds
     */
    public int getOffsetForTimezone(String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        ZoneOffset offset = ZonedDateTime.now(zoneId).getOffset();
        return offset.getTotalSeconds();
    }
    
    /**
     * Check if a timezone identifier is valid.
     * 
     * @param timezone Timezone identifier
     * @return true if valid
     */
    public boolean isValidTimezone(String timezone) {
        try {
            ZoneId.of(timezone);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
