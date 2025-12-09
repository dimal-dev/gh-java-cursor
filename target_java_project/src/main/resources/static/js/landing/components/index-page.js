/**
 * Index Page Specific Components
 * Handles problem category drag on mobile and other index-specific functionality
 */
(function() {
    'use strict';

    /**
     * Initialize problem category drag on mobile
     */
    function initProblemCategoryDrag() {
        const problemList = document.querySelector('.problem-category-list');
        if (!problemList) {
            return;
        }

        let problemDragEnabled = false;
        let previousPageX = 0;
        const baseOffset = problemList.offsetLeft;

        // Touch events for mobile
        problemList.addEventListener('touchstart', function(e) {
            problemDragEnabled = true;
            previousPageX = e.touches[0].pageX;
        });

        problemList.addEventListener('touchmove', function(e) {
            if (!problemDragEnabled) return;

            const currentPageX = e.touches[0].pageX;
            const diff = currentPageX - previousPageX;
            const currentScroll = problemList.scrollLeft;

            problemList.scrollLeft = currentScroll - diff;
            previousPageX = currentPageX;
        });

        problemList.addEventListener('touchend', function() {
            problemDragEnabled = false;
        });

        // Mouse events for desktop drag
        let isMouseDown = false;
        let startX = 0;
        let scrollLeft = 0;

        problemList.addEventListener('mousedown', function(e) {
            isMouseDown = true;
            startX = e.pageX - problemList.offsetLeft;
            scrollLeft = problemList.scrollLeft;
            problemList.style.cursor = 'grabbing';
        });

        problemList.addEventListener('mouseleave', function() {
            isMouseDown = false;
            problemList.style.cursor = 'grab';
        });

        problemList.addEventListener('mouseup', function() {
            isMouseDown = false;
            problemList.style.cursor = 'grab';
        });

        problemList.addEventListener('mousemove', function(e) {
            if (!isMouseDown) return;
            e.preventDefault();
            const x = e.pageX - problemList.offsetLeft;
            const walk = (x - startX) * 2;
            problemList.scrollLeft = scrollLeft - walk;
        });
    }

    /**
     * Initialize Google Analytics events for index page
     */
    function initAnalyticsEvents() {
        // Track select therapist button clicks
        document.querySelectorAll('.select-psiholog-btn').forEach(function(btn) {
            btn.addEventListener('click', function() {
                if (typeof gtag !== 'undefined') {
                    gtag('event', 'select_psiholog_btn_clicked_from_main', {
                        event_category: 'Conversions'
                    });
                }
            });
        });

        // Track view specific therapist button clicks
        document.querySelectorAll('.view-specific-psiholog-btn').forEach(function(btn) {
            btn.addEventListener('click', function() {
                if (typeof gtag !== 'undefined') {
                    gtag('event', 'view_specific_psiholog_btn_clicked_on_main', {
                        event_category: 'Conversions'
                    });
                }
            });
        });
    }

    /**
     * Initialize all index page components
     */
    function init() {
        initProblemCategoryDrag();
        initAnalyticsEvents();
    }

    // Only initialize on index page
    if (document.querySelector('.problem-category-list')) {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', init);
        } else {
            init();
        }
    }

    // Export for use in main.js if needed
    window.GoodHelp = window.GoodHelp || {};
    window.GoodHelp.IndexPage = {
        init: init
    };
})();

