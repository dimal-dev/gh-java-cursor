/**
 * Mobile Menu Component
 * Handles mobile menu toggle functionality
 */
(function() {
    'use strict';

    /**
     * Initialize mobile menu
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

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initMobileMenu);
    } else {
        initMobileMenu();
    }

    // Export for use in main.js if needed
    window.GoodHelp = window.GoodHelp || {};
    window.GoodHelp.MobileMenu = {
        init: initMobileMenu
    };
})();

