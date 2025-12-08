package com.goodhelp.common.service;

import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Service for timezone operations.
 * Provides a curated list of common timezones for user selection.
 */
@Service
public class TimezoneService {

    private static final DateTimeFormatter OFFSET_FORMATTER = DateTimeFormatter.ofPattern("XXX");

    /**
     * Get a map of common timezones.
     * Key: timezone ID (e.g., "Europe/Kiev")
     * Value: display label (e.g., "(UTC+02:00) Kyiv")
     *
     * @return ordered map of timezone ID to display label
     */
    public Map<String, String> getAvailableTimezones() {
        Map<String, String> timezones = new LinkedHashMap<>();
        
        // Common timezones used by our users, ordered by offset then name
        addTimezone(timezones, "Pacific/Honolulu");      // UTC-10
        addTimezone(timezones, "America/Anchorage");     // UTC-09
        addTimezone(timezones, "America/Los_Angeles");   // UTC-08
        addTimezone(timezones, "America/Denver");        // UTC-07
        addTimezone(timezones, "America/Chicago");       // UTC-06
        addTimezone(timezones, "America/New_York");      // UTC-05
        addTimezone(timezones, "America/Halifax");       // UTC-04
        addTimezone(timezones, "America/Sao_Paulo");     // UTC-03
        addTimezone(timezones, "Atlantic/Azores");       // UTC-01
        addTimezone(timezones, "Europe/London");         // UTC+00
        addTimezone(timezones, "Europe/Paris");          // UTC+01
        addTimezone(timezones, "Europe/Berlin");         // UTC+01
        addTimezone(timezones, "Europe/Warsaw");         // UTC+01
        addTimezone(timezones, "Europe/Kiev");           // UTC+02 (Kyiv)
        addTimezone(timezones, "Europe/Bucharest");      // UTC+02
        addTimezone(timezones, "Europe/Athens");         // UTC+02
        addTimezone(timezones, "Europe/Helsinki");       // UTC+02
        addTimezone(timezones, "Europe/Istanbul");       // UTC+03
        addTimezone(timezones, "Europe/Moscow");         // UTC+03
        addTimezone(timezones, "Europe/Minsk");          // UTC+03
        addTimezone(timezones, "Asia/Dubai");            // UTC+04
        addTimezone(timezones, "Asia/Tbilisi");          // UTC+04
        addTimezone(timezones, "Asia/Yerevan");          // UTC+04
        addTimezone(timezones, "Asia/Baku");             // UTC+04
        addTimezone(timezones, "Asia/Tashkent");         // UTC+05
        addTimezone(timezones, "Asia/Almaty");           // UTC+06
        addTimezone(timezones, "Asia/Bangkok");          // UTC+07
        addTimezone(timezones, "Asia/Singapore");        // UTC+08
        addTimezone(timezones, "Asia/Hong_Kong");        // UTC+08
        addTimezone(timezones, "Asia/Tokyo");            // UTC+09
        addTimezone(timezones, "Australia/Sydney");      // UTC+10
        addTimezone(timezones, "Pacific/Auckland");      // UTC+12
        
        return timezones;
    }

    /**
     * Get sorted timezones by offset for proper ordering.
     */
    public Map<String, String> getTimezonesSortedByOffset() {
        Map<Integer, Map<String, String>> byOffset = new TreeMap<>();
        
        for (Map.Entry<String, String> entry : getAvailableTimezones().entrySet()) {
            ZoneId zone = ZoneId.of(entry.getKey());
            int offset = ZonedDateTime.now(zone).getOffset().getTotalSeconds();
            byOffset.computeIfAbsent(offset, k -> new LinkedHashMap<>())
                    .put(entry.getKey(), entry.getValue());
        }

        Map<String, String> result = new LinkedHashMap<>();
        byOffset.values().forEach(result::putAll);
        return result;
    }

    private void addTimezone(Map<String, String> timezones, String zoneId) {
        try {
            ZoneId zone = ZoneId.of(zoneId);
            ZonedDateTime now = ZonedDateTime.now(zone);
            String offset = now.format(OFFSET_FORMATTER);
            String cityName = getCityName(zoneId);
            String label = String.format("(UTC%s) %s", offset, cityName);
            timezones.put(zoneId, label);
        } catch (Exception e) {
            // Skip invalid timezone
        }
    }

    private String getCityName(String zoneId) {
        // Extract city name from zone ID (e.g., "Europe/Kiev" -> "Kiev")
        String city = zoneId.contains("/") 
            ? zoneId.substring(zoneId.lastIndexOf('/') + 1) 
            : zoneId;
        // Replace underscores with spaces
        city = city.replace("_", " ");
        // Special case for Kyiv
        if ("Kiev".equals(city)) {
            city = "Kyiv";
        }
        return city;
    }

    /**
     * Validate if a timezone ID is valid.
     */
    public boolean isValidTimezone(String timezoneId) {
        try {
            ZoneId.of(timezoneId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get display name for a timezone.
     */
    public String getDisplayName(String timezoneId) {
        Map<String, String> timezones = getAvailableTimezones();
        return timezones.getOrDefault(timezoneId, timezoneId);
    }
}

