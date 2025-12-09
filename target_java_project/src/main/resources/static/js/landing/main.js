/**
 * GoodHelp Landing - Main JavaScript
 * Entry point for landing page functionality
 */

(function() {
    'use strict';

    /**
     * Initialize on DOM ready
     */
    document.addEventListener('DOMContentLoaded', function() {
        initSmoothScroll();
        initHeaderScroll();
    });

    /**
     * Smooth scroll for anchor links
     */
    function initSmoothScroll() {
        document.querySelectorAll('a[href^="#"]').forEach(function(anchor) {
            anchor.addEventListener('click', function(e) {
                const href = this.getAttribute('href');
                if (href === '#' || href === '#0') return;

                e.preventDefault();
                const target = document.querySelector(href);
                if (target) {
                    const headerHeight = document.querySelector('.gh-header')?.offsetHeight || 0;
                    const targetPosition = target.getBoundingClientRect().top + window.pageYOffset - headerHeight;
                    
                    window.scrollTo({
                        top: targetPosition,
                        behavior: 'smooth'
                    });
                }
            });
        });
    }


    /**
     * Header shadow on scroll
     */
    function initHeaderScroll() {
        const header = document.querySelector('.gh-header');
        if (!header) return;

        let lastScroll = 0;

        window.addEventListener('scroll', function() {
            const currentScroll = window.pageYOffset;

            if (currentScroll > 50) {
                header.classList.add('gh-header--with-shadow');
            } else {
                // Only remove if header doesn't have the class by default
                if (!header.dataset.hasShadow) {
                    header.classList.remove('gh-header--with-shadow');
                }
            }

            lastScroll = currentScroll;
        });
    }


    /**
     * Form validation helper
     */
    window.GoodHelp = window.GoodHelp || {};

    window.GoodHelp.validateEmail = function(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    };

    window.GoodHelp.validatePhone = function(phone) {
        const re = /^\+?[\d\s-]{10,}$/;
        return re.test(phone);
    };

    /**
     * Show loading state on button
     */
    window.GoodHelp.setButtonLoading = function(button, isLoading) {
        if (isLoading) {
            button.disabled = true;
            button.dataset.originalText = button.textContent;
            button.innerHTML = '<span class="gh-spinner"></span>';
        } else {
            button.disabled = false;
            button.textContent = button.dataset.originalText || button.textContent;
        }
    };

    /**
     * Format currency
     */
    window.GoodHelp.formatCurrency = function(amount, currency) {
        currency = currency || 'UAH';
        const formatter = new Intl.NumberFormat('uk-UA', {
            style: 'currency',
            currency: currency,
            minimumFractionDigits: 0
        });
        return formatter.format(amount);
    };

    /**
     * Format date/time (delegates to Timezone component if available)
     */
    window.GoodHelp.formatDateTime = function(dateString, locale) {
        if (window.GoodHelp.Timezone && window.GoodHelp.Timezone.formatDateTime) {
            return window.GoodHelp.Timezone.formatDateTime(dateString, locale);
        }
        // Fallback implementation
        locale = locale || 'uk-UA';
        const date = new Date(dateString);
        return date.toLocaleDateString(locale, {
            day: 'numeric',
            month: 'long',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

})();

