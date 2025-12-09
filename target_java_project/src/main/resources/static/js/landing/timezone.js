/**
 * Timezone Detection and Management
 * Detects user timezone and saves it to server
 */
(function() {
    'use strict';

    /**
     * Detect and save timezone to server
     */
    function detectAndSaveTimezone() {
        const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
        
        // Check if timezone is already saved in cookie
        if (document.cookie.includes('user_timezone')) {
            return;
        }

        // Save timezone to localStorage for client-side use
        localStorage.setItem('userTimezone', timezone);

        // Send timezone to server
        fetch('/api/save-timezone', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ tz: timezone })
        }).catch(function(error) {
            console.error('Failed to save timezone:', error);
        });
    }

    /**
     * Get user timezone from localStorage or detect it
     * @returns {string} Timezone string (e.g., "Europe/Kyiv")
     */
    function getUserTimezone() {
        return localStorage.getItem('userTimezone') || 
               Intl.DateTimeFormat().resolvedOptions().timeZone;
    }

    /**
     * Format date/time in user's timezone
     * @param {string|Date} dateString - Date string or Date object
     * @param {string} locale - Locale string (e.g., 'uk-UA')
     * @returns {string} Formatted date string
     */
    function formatDateTime(dateString, locale) {
        locale = locale || 'uk-UA';
        const date = new Date(dateString);
        const timezone = getUserTimezone();
        
        return date.toLocaleDateString(locale, {
            day: 'numeric',
            month: 'long',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            timeZone: timezone
        });
    }

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', detectAndSaveTimezone);
    } else {
        detectAndSaveTimezone();
    }

    // Export for use in other scripts
    window.GoodHelp = window.GoodHelp || {};
    window.GoodHelp.Timezone = {
        detectAndSave: detectAndSaveTimezone,
        getUserTimezone: getUserTimezone,
        formatDateTime: formatDateTime
    };
})();

