(function() {
    'use strict';

    document.addEventListener('DOMContentLoaded', () => {
        initPriceSelection();
        initSlotSelection();
        initAuthSwitch();
        initPromo();
        initFormValidation();
        preselectDefaults();
    });

    function qs(selector) {
        return document.querySelector(selector);
    }

    function qsa(selector) {
        return Array.from(document.querySelectorAll(selector));
    }

    function getSelectedPriceInput() {
        return qs('.gh-book-consultation__choose-type-switch:checked');
    }

    function getSelectedSlotInput() {
        return qs('.gh-book-consultation__schedule-day-time-switch:checked');
    }

    function initPriceSelection() {
        qsa('.gh-book-consultation__choose-type-switch').forEach(input => {
            input.addEventListener('change', () => {
                toggleScheduleLists(input.dataset.targetSelector);
                resetSlotSelection();
                updateSummaryFromPrice(input);
                updateBookButtonState();
                resetPromocodeUi();
            });
        });
    }

    function initSlotSelection() {
        qsa('.gh-book-consultation__schedule-day-time-switch').forEach(input => {
            input.addEventListener('change', () => {
                const slotIdInput = qs('#slotId');
                if (slotIdInput) {
                    slotIdInput.value = input.value;
                }
                updateSummaryTime(input.dataset.summaryLabel || input.dataset.time || '');
                updateBookButtonState();
            });
        });
    }

    function initAuthSwitch() {
        qsa('.gh-book-consultation__choose-auth-type-switch').forEach(input => {
            input.addEventListener('change', () => {
                const targetSelector = input.dataset.targetSelector;
                qsa('.gh-book-consultation__auth-form').forEach(block => block.classList.add('gh-d-none'));
                if (targetSelector) {
                    qsa(targetSelector).forEach(block => block.classList.remove('gh-d-none'));
                }
                updateBookButtonState();
            });
        });
    }

    function initPromo() {
        const toggle = qs('#promo-toggle');
        const promoForm = qs('#promo-form');
        const applyBtn = qs('#apply-promocode');
        if (toggle && promoForm) {
            toggle.addEventListener('click', () => promoForm.classList.toggle('gh-d-none'));
        }
        if (applyBtn) {
            applyBtn.addEventListener('click', applyPromocode);
        }
    }

    function initFormValidation() {
        const form = qs('#booking-form');
        if (!form) return;
        form.addEventListener('submit', evt => {
            if (!validateForm()) {
                evt.preventDefault();
            }
        });
    }

    function preselectDefaults() {
        // Ensure a price is selected
        const priceInputs = qsa('.gh-book-consultation__choose-type-switch');
        if (priceInputs.length > 0 && !getSelectedPriceInput()) {
            priceInputs[0].checked = true;
            priceInputs[0].dispatchEvent(new Event('change'));
        }

        // Select first available slot of the visible list
        const visibleList = qs('.gh-book-consultation__schedule-time-list:not(.gh-d-none)');
        if (visibleList) {
            const firstSlot = visibleList.querySelector('.gh-book-consultation__schedule-day-time-switch');
            if (firstSlot) {
                firstSlot.checked = true;
                firstSlot.dispatchEvent(new Event('change'));
            }
        }
        updateBookButtonState();
    }

    function toggleScheduleLists(selector) {
        qsa('.gh-book-consultation__schedule-time-list').forEach(list => list.classList.add('gh-d-none'));
        if (selector) {
            qsa(selector).forEach(list => list.classList.remove('gh-d-none'));
        }
    }

    function resetSlotSelection() {
        qsa('.gh-book-consultation__schedule-day-time-switch').forEach(input => input.checked = false);
        const slotIdInput = qs('#slotId');
        if (slotIdInput) {
            slotIdInput.value = '';
        }
        updateSummaryTime(qs('#summary-session-time')?.dataset.placeholder || '');
    }

    function updateSummaryFromPrice(priceInput) {
        if (!priceInput) return;
        const summaryType = qs('#summary-session-type');
        const summaryDuration = qs('#summary-session-duration');
        const priceSummary = qs('#price-summary');

        if (summaryType) {
            summaryType.textContent = priceInput.dataset.label || '';
        }
        if (summaryDuration) {
            const duration = priceInput.dataset.duration || '';
            summaryDuration.textContent = duration ? `${duration} ${getTranslation('minutes')}` : '';
        }
        if (priceSummary) {
            priceSummary.classList.remove('gh-d-none');
            const newPriceEl = qs('#new-price-value');
            if (newPriceEl) {
                newPriceEl.textContent = priceInput.dataset.priceValue || '0';
            }
            hideOldPrice();
        }
    }

    function updateSummaryTime(label) {
        const summary = qs('#summary-session-time');
        if (summary) {
            summary.textContent = label || getTranslation('Needs_to_be_chosen');
        }
    }

    function applyPromocode() {
        const codeInput = qs('#promocode-name');
        if (!codeInput) return;

        hidePromocodeErrors();

        const code = codeInput.value.trim();
        if (!code) {
            showElement('#error-promocode-empty');
            return;
        }

        const priceInput = getSelectedPriceInput();
        if (!priceInput) {
            showElement('#error-promocode-empty');
            return;
        }

        const payload = {
            code: code,
            priceId: Number(priceInput.value),
            email: getActiveEmail()
        };

        fetch('/api/book-consultation/apply-promocode', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(payload)
        })
            .then(res => res.ok ? res.json() : Promise.reject())
            .then(data => {
                if (!data || !data.valid) {
                    showElement('#error-promocode-doesnt-exist');
                    return;
                }
                const newPrice = data.newPrice ?? priceInput.dataset.priceValue;
                const basePrice = Number(priceInput.dataset.priceValue || newPrice);
                updatePriceWithDiscount(basePrice, newPrice, code);
            })
            .catch(() => showElement('#error-promocode-doesnt-exist'));
    }

    function updatePriceWithDiscount(basePrice, discountedPrice, code) {
        const oldPrice = qs('#old-price');
        const oldPriceValue = qs('#old-price-value');
        const newPriceValue = qs('#new-price-value');
        const promoLabel = qs('#promo-applied-label');
        const promoHidden = qs('#appliedPromocode');

        if (oldPrice && oldPriceValue) {
            oldPrice.classList.remove('gh-d-none');
            oldPriceValue.textContent = basePrice;
        }
        if (newPriceValue) {
            newPriceValue.textContent = discountedPrice;
        }
        if (promoLabel) {
            promoLabel.classList.remove('gh-d-none');
            promoLabel.textContent = getTranslation('Promocode_applied') + (code ? ` ${code}` : '');
        }
        if (promoHidden) {
            promoHidden.value = code;
        }
        showElement('#promocode-applied-message');
    }

    function hideOldPrice() {
        const oldPrice = qs('#old-price');
        if (oldPrice) {
            oldPrice.classList.add('gh-d-none');
        }
        const promoLabel = qs('#promo-applied-label');
        if (promoLabel) {
            promoLabel.classList.add('gh-d-none');
            promoLabel.textContent = '';
        }
        const promoHidden = qs('#appliedPromocode');
        if (promoHidden) {
            promoHidden.value = '';
        }
    }

    function resetPromocodeUi() {
        hidePromocodeErrors();
        hideOldPrice();
        const message = qs('#promocode-applied-message');
        if (message) {
            message.classList.add('gh-d-none');
        }
    }

    function hidePromocodeErrors() {
        qsa('.gh-promocode-error').forEach(el => el.classList.add('gh-d-none'));
    }

    function getActiveEmail() {
        const authExisting = qs('#auth-existing--email');
        const authNew = qs('#auth-new--email');
        const authType = qs('.gh-book-consultation__choose-auth-type-switch:checked');
        if (authType && authType.value === 'existing') {
            return authExisting?.value || '';
        }
        return authNew?.value || '';
    }

    function validateForm() {
        const priceInput = getSelectedPriceInput();
        const slotInput = getSelectedSlotInput();
        const authType = qs('.gh-book-consultation__choose-auth-type-switch:checked');
        const email = getActiveEmail();
        const name = qs('#auth-new--name')?.value.trim();
        const phone = qs('#auth-new--phone')?.value.trim();
        let valid = true;

        hideElement('#summary-errors');
        hideElement('#error-name');
        hideElement('#error-phone');
        hideElement('#error-email');
        hideElement('#error-email-existing');
        hideElement('#slot-error');

        if (!priceInput) {
            valid = false;
            showElement('#summary-errors');
        }
        if (!slotInput) {
            valid = false;
            showElement('#summary-errors');
            showElement('#slot-error');
        }
        if (!email) {
            valid = false;
            if (authType && authType.value === 'existing') {
                showElement('#error-email-existing');
            } else {
                showElement('#error-email');
            }
        }
        if (!authType || authType.value === 'new') {
            if (!name) {
                valid = false;
                showElement('#error-name');
            }
            if (!phone) {
                valid = false;
                showElement('#error-phone');
            }
        }

        if (!valid) {
            showElement('#summary-errors');
        }

        const slotIdInput = qs('#slotId');
        if (slotInput && slotIdInput) {
            slotIdInput.value = slotInput.value;
        }

        return valid;
    }

    function updateBookButtonState() {
        const button = qs('#book-button');
        if (!button) return;
        const priceSelected = !!getSelectedPriceInput();
        const slotSelected = !!getSelectedSlotInput();
        button.disabled = !(priceSelected && slotSelected);
    }

    function showElement(selector) {
        const el = typeof selector === 'string' ? qs(selector) : selector;
        if (el) {
            el.classList.remove('gh-d-none');
        }
    }

    function hideElement(selector) {
        const el = typeof selector === 'string' ? qs(selector) : selector;
        if (el) {
            el.classList.add('gh-d-none');
        }
    }

    function getTranslation(fallbackKey) {
        // Fallback: use existing text from DOM if available
        const map = {
            minutes: qs('#summary-session-duration')?.dataset?.minutesLabel || 'minutes',
            Needs_to_be_chosen: qs('#summary-session-time')?.dataset?.placeholder || 'Needs to be chosen',
            Promocode_applied: qs('#promo-applied-label')?.dataset?.label || 'Promocode applied'
        };
        return map[fallbackKey] || fallbackKey;
    }
})();

