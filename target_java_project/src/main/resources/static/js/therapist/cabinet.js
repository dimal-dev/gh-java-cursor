/**
 * GoodHelp - Therapist Cabinet Common JavaScript
 */

(function() {
    'use strict';

    /**
     * Mobile sidebar toggle
     */
    function initSidebarToggle() {
        // Create mobile menu button if it doesn't exist
        const sidebar = document.querySelector('.cabinet-sidebar');
        if (!sidebar) return;

        // Toggle sidebar on mobile
        const mobileToggle = document.createElement('button');
        mobileToggle.className = 'mobile-menu-toggle';
        mobileToggle.innerHTML = '☰';
        mobileToggle.style.cssText = `
            display: none;
            position: fixed;
            top: 16px;
            left: 16px;
            z-index: 101;
            background: #1a1d2e;
            color: #fff;
            border: none;
            border-radius: 8px;
            padding: 12px 16px;
            font-size: 1.2rem;
            cursor: pointer;
        `;

        document.body.appendChild(mobileToggle);

        mobileToggle.addEventListener('click', () => {
            sidebar.classList.toggle('open');
        });

        // Show toggle on mobile
        const checkMobile = () => {
            if (window.innerWidth <= 768) {
                mobileToggle.style.display = 'block';
            } else {
                mobileToggle.style.display = 'none';
                sidebar.classList.remove('open');
            }
        };

        checkMobile();
        window.addEventListener('resize', checkMobile);

        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', (e) => {
            if (window.innerWidth <= 768 && 
                sidebar.classList.contains('open') && 
                !sidebar.contains(e.target) && 
                e.target !== mobileToggle) {
                sidebar.classList.remove('open');
            }
        });
    }

    /**
     * Initialize tooltips
     */
    function initTooltips() {
        document.querySelectorAll('[data-tooltip]').forEach(el => {
            el.addEventListener('mouseenter', function() {
                const tooltip = document.createElement('div');
                tooltip.className = 'tooltip';
                tooltip.textContent = this.dataset.tooltip;
                tooltip.style.cssText = `
                    position: absolute;
                    background: #1f2937;
                    color: #fff;
                    padding: 6px 12px;
                    border-radius: 6px;
                    font-size: 0.85rem;
                    z-index: 1000;
                    pointer-events: none;
                `;
                document.body.appendChild(tooltip);

                const rect = this.getBoundingClientRect();
                tooltip.style.top = (rect.top - tooltip.offsetHeight - 8) + 'px';
                tooltip.style.left = (rect.left + rect.width / 2 - tooltip.offsetWidth / 2) + 'px';

                this._tooltip = tooltip;
            });

            el.addEventListener('mouseleave', function() {
                if (this._tooltip) {
                    this._tooltip.remove();
                    this._tooltip = null;
                }
            });
        });
    }

    /**
     * Handle AJAX form submissions
     */
    function initAjaxForms() {
        document.querySelectorAll('form[data-ajax]').forEach(form => {
            form.addEventListener('submit', async function(e) {
                e.preventDefault();

                const submitBtn = this.querySelector('[type="submit"]');
                const originalText = submitBtn ? submitBtn.textContent : '';
                
                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.innerHTML = '<span class="spinner-small"></span>';
                }

                try {
                    const formData = new FormData(this);
                    const response = await fetch(this.action, {
                        method: this.method,
                        body: formData
                    });

                    if (response.ok) {
                        const event = new CustomEvent('ajax-success', { detail: { form: this, response } });
                        this.dispatchEvent(event);
                    } else {
                        throw new Error('Request failed');
                    }
                } catch (error) {
                    console.error('Form submission error:', error);
                    alert('An error occurred. Please try again.');
                } finally {
                    if (submitBtn) {
                        submitBtn.disabled = false;
                        submitBtn.textContent = originalText;
                    }
                }
            });
        });
    }

    /**
     * Initialize on DOM ready
     */
    document.addEventListener('DOMContentLoaded', function() {
        initSidebarToggle();
        initTooltips();
        initAjaxForms();
    });
})();

