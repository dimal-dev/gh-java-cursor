/**
 * Checkout Page JavaScript
 * Handles checkout page analytics and thank you page status polling
 */
(function () {
    'use strict';

    /**
     * Initialize checkout page analytics
     */
    function initCheckoutAnalytics() {
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
    }

    /**
     * Checkout Status Poller
     * Polls the server for order status updates on thank you page
     */
    class CheckoutStatusPoller {
        constructor(slug, initialStatus, initialPrice, initialTherapistName) {
            this.slug = slug;
            this.initialStatus = initialStatus;
            this.initialPrice = initialPrice;
            this.initialTherapistName = initialTherapistName;
            this.pollInterval = 5000; // 5 seconds
            this.maxAttempts = 60; // 5 minutes max
            this.attempts = 0;
            this.intervalId = null;

            this.failedContainer = document.querySelector('#state-failed');
            this.approvedContainer = document.querySelector('#state-approved');
            this.pendingContainer = document.querySelector('#state-pending');

            this.init();
        }

        init() {
            // Show initial state
            if (this.initialStatus === 'approved') {
                this.show(this.approvedContainer);
            } else if (this.initialStatus === 'failed') {
                this.show(this.failedContainer);
            } else if (this.initialStatus === 'pending') {
                this.show(this.pendingContainer);
                this.startPolling();
            }
        }

        show(element) {
            [this.failedContainer, this.approvedContainer, this.pendingContainer].forEach(container => {
                if (container) container.classList.add('gh-d-none');
            });
            if (element) element.classList.remove('gh-d-none');
        }

        async checkStatus() {
            try {
                const response = await fetch('/api/checkout/status/' + this.slug, {
                    method: 'GET',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });

                if (!response.ok) {
                    throw new Error('Failed to check status');
                }

                const data = await response.json();
                const state = data.state;

                if (state === 3) { // Failed
                    this.show(this.failedContainer);
                    this.stopPolling();
                    this.trackAnalytics('thank_you_page_payment_failed');
                } else if (state === 4) { // Approved
                    this.show(this.approvedContainer);
                    this.stopPolling();

                    // Update login button href if provided
                    const loginButton = document.querySelector('#go-to-personal-cabinet-button');
                    if (loginButton && data.l) {
                        loginButton.setAttribute('href', data.l);
                    }

                    this.trackPurchase();
                }
            } catch (error) {
                console.error('Error checking status:', error);
            }
        }

        startPolling() {
            this.checkStatus();
            this.intervalId = setInterval(() => {
                this.attempts++;
                if (this.attempts >= this.maxAttempts) {
                    this.stopPolling();
                    return;
                }
                this.checkStatus();
            }, this.pollInterval);
        }

        stopPolling() {
            if (this.intervalId) {
                clearInterval(this.intervalId);
                this.intervalId = null;
            }
        }

        trackAnalytics(eventName) {
            if (typeof gtag !== 'undefined') {
                gtag('event', eventName, {
                    event_category: 'Thank you page'
                });
            }
        }

        trackPurchase() {
            if (typeof gtag !== 'undefined') {
                gtag('event', 'purchase', {
                    transaction_id: this.slug,
                    value: this.initialPrice / 100, // Convert cents to currency units
                    currency: 'UAH',
                    items: [{
                        item_name: this.initialTherapistName || 'Consultation'
                    }]
                });
            }
        }
    }

    /**
     * Initialize thank you page status polling
     */
    function initThankYouPage() {
        const slugInput = document.getElementById('checkout-slug');
        const initialStatusInput = document.getElementById('initial-status');
        const initialPriceInput = document.getElementById('initial-price');
        const initialTherapistNameInput = document.getElementById('initial-therapist-name');

        if (slugInput && initialStatusInput) {
            const slug = slugInput.value;
            const initialStatus = initialStatusInput.value || 'pending';
            const initialPrice = initialPriceInput ? parseInt(initialPriceInput.value, 10) : 0;
            const initialTherapistName = initialTherapistNameInput ? initialTherapistNameInput.value : '';

            new CheckoutStatusPoller(slug, initialStatus, initialPrice, initialTherapistName);
        }
    }

    // Initialize on DOM ready
    document.addEventListener('DOMContentLoaded', () => {
        // Initialize checkout analytics (only on checkout page)
        if (document.querySelector('#payment-form')) {
            initCheckoutAnalytics();
        }

        // Initialize thank you page polling (only on thank you page)
        if (document.getElementById('checkout-slug')) {
            initThankYouPage();
        }
    });

    // Export for use in other scripts
    window.GoodHelp = window.GoodHelp || {};
    window.GoodHelp.Checkout = {
        CheckoutStatusPoller: CheckoutStatusPoller
    };
})();

