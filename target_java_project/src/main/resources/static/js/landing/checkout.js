(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', () => {
        if (typeof window.gtag !== 'function') {
            return;
        }

        window.gtag('event', 'checkout_page_viewed', {
            event_category: 'Conversions'
        });

        const form = document.querySelector('#payment-form');
        if (form) {
            form.addEventListener('submit', () => {
                window.gtag('event', 'checkout_order_consultation_btn_clicked', {
                    event_category: 'Conversions'
                });
            });
        }
    });
})();

