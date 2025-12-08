/**
 * GoodHelp Landing - Main JavaScript
 */

(function() {
    'use strict';

    /**
     * Initialize on DOM ready
     */
    document.addEventListener('DOMContentLoaded', function() {
        initMobileMenu();
        initSmoothScroll();
        initFaqAccordion();
        initHeaderScroll();
    });

    /**
     * Mobile menu toggle
     */
    function initMobileMenu() {
        const menuToggle = document.getElementById('mobile-menu-toggle');
        const menuClose = document.getElementById('mobile-menu-close');
        const mobileHeader = document.getElementById('mobile-header');

        if (menuToggle && mobileHeader) {
            menuToggle.addEventListener('click', function() {
                mobileHeader.classList.add('gh-mobile-header--opened');
                document.body.style.overflow = 'hidden';
            });
        }

        if (menuClose && mobileHeader) {
            menuClose.addEventListener('click', function() {
                mobileHeader.classList.remove('gh-mobile-header--opened');
                document.body.style.overflow = '';
            });
        }

        // Close menu when clicking a link
        if (mobileHeader) {
            const menuLinks = mobileHeader.querySelectorAll('a');
            menuLinks.forEach(function(link) {
                link.addEventListener('click', function() {
                    mobileHeader.classList.remove('gh-mobile-header--opened');
                    document.body.style.overflow = '';
                });
            });
        }
    }

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
     * FAQ Accordion
     */
    function initFaqAccordion() {
        const faqItems = document.querySelectorAll('.gh-faq__item');

        faqItems.forEach(function(item) {
            const question = item.querySelector('.gh-faq__question');
            if (question) {
                question.addEventListener('click', function() {
                    const isOpen = item.classList.contains('gh-faq__item--open');

                    // Close all items
                    faqItems.forEach(function(otherItem) {
                        otherItem.classList.remove('gh-faq__item--open');
                    });

                    // Toggle clicked item
                    if (!isOpen) {
                        item.classList.add('gh-faq__item--open');
                    }
                });
            }
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
     * Timezone detection and saving
     */
    function detectAndSaveTimezone() {
        const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
        
        // Check if timezone is already saved in cookie
        if (document.cookie.includes('user_timezone')) {
            return;
        }

        // Save timezone
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
     * Format date/time
     */
    window.GoodHelp.formatDateTime = function(dateString, locale) {
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

