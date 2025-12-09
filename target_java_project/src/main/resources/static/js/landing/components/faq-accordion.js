/**
 * FAQ Accordion Component
 * Handles FAQ accordion functionality (only one item open at a time)
 */
(function() {
    'use strict';

    /**
     * Initialize FAQ accordion
     * Supports both .gh-faq__item and .ghl-frequent-questions__question selectors
     */
    function initFaqAccordion() {
        // Standard FAQ items
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

        // Landing page FAQ items (different selector)
        const landingFaqItems = document.querySelectorAll('.ghl-frequent-questions__question');
        landingFaqItems.forEach(function(item) {
            const questionHead = item.querySelector('.ghl-frequent-questions__question-head');
            if (questionHead) {
                questionHead.addEventListener('click', function() {
                    const containerEl = item;
                    if (containerEl.classList.contains('ghl-frequent-questions__question--opened')) {
                        containerEl.classList.remove('ghl-frequent-questions__question--opened');
                    } else {
                        // Close all other items
                        landingFaqItems.forEach(function(otherItem) {
                            otherItem.classList.remove('ghl-frequent-questions__question--opened');
                        });
                        // Open clicked item
                        containerEl.classList.add('ghl-frequent-questions__question--opened');
                    }
                });
            }
        });
    }

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initFaqAccordion);
    } else {
        initFaqAccordion();
    }

    // Export for use in main.js if needed
    window.GoodHelp = window.GoodHelp || {};
    window.GoodHelp.FaqAccordion = {
        init: initFaqAccordion
    };
})();

