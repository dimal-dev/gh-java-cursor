/**
 * Swiper Component
 * Initializes Swiper carousel for reviews section
 */
(function() {
    'use strict';

    /**
     * Initialize Swiper carousel
     */
    function initSwiper() {
        const swiperContainer = document.querySelector('.swiper');
        if (!swiperContainer) {
            return;
        }

        // Check if Swiper library is loaded
        if (typeof Swiper === 'undefined') {
            console.warn('Swiper library not loaded');
            return;
        }

        new Swiper('.swiper', {
            pagination: {
                el: '.ghl-reviews__pagination',
                clickable: true,
            },
            slidesPerView: 1,
            spaceBetween: 30,
            breakpoints: {
                768: {
                    slidesPerView: 2,
                    spaceBetween: 30,
                },
                1200: {
                    slidesPerView: 3,
                    spaceBetween: 40,
                },
            }
        });
    }

    /**
     * Load Swiper library and initialize
     */
    function loadSwiperAndInit() {
        // Check if Swiper is already loaded
        if (typeof Swiper !== 'undefined') {
            initSwiper();
            return;
        }

        // Load Swiper CSS if not already loaded
        if (!document.querySelector('link[href*="swiper"]')) {
            const link = document.createElement('link');
            link.rel = 'stylesheet';
            link.href = 'https://cdn.jsdelivr.net/npm/swiper@8/swiper-bundle.min.css';
            document.head.appendChild(link);
        }

        // Load Swiper JS
        const script = document.createElement('script');
        script.src = 'https://cdn.jsdelivr.net/npm/swiper@8/swiper-bundle.min.js';
        script.onload = initSwiper;
        script.onerror = function() {
            console.error('Failed to load Swiper library');
        };
        document.head.appendChild(script);
    }

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', loadSwiperAndInit);
    } else {
        loadSwiperAndInit();
    }

    // Export for use in main.js if needed
    window.GoodHelp = window.GoodHelp || {};
    window.GoodHelp.Swiper = {
        init: initSwiper
    };
})();

