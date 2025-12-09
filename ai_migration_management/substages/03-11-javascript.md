# Sub-Stage 3.11: Landing JavaScript

## Goal
Implement JavaScript functionality for landing pages.

---

## File Structure

```
src/main/resources/static/js/landing/
├── main.js           # Entry point, common functions
├── timezone.js       # Timezone detection
├── booking.js        # Booking page logic
├── checkout.js       # Checkout status polling
└── components/
    ├── mobile-menu.js
    ├── faq-accordion.js
    └── promocode.js
```

---

## Main.js

```javascript
// main.js
document.addEventListener('DOMContentLoaded', function() {
    initMobileMenu();
    initFaqAccordion();
    detectAndSaveTimezone();
});

// Mobile menu toggle
function initMobileMenu() {
    const toggle = document.querySelector('.mobile-menu-toggle');
    const nav = document.querySelector('.main-nav');
    
    if (toggle && nav) {
        toggle.addEventListener('click', () => {
            nav.classList.toggle('active');
            toggle.setAttribute('aria-expanded', nav.classList.contains('active'));
        });
    }
}

// FAQ accordion
function initFaqAccordion() {
    document.querySelectorAll('.faq-question').forEach(question => {
        question.addEventListener('click', () => {
            const item = question.parentElement;
            const wasOpen = item.classList.contains('open');
            
            // Close all
            document.querySelectorAll('.faq-item').forEach(i => i.classList.remove('open'));
            
            // Open clicked if wasn't open
            if (!wasOpen) {
                item.classList.add('open');
            }
        });
    });
}
```

---

## Timezone Detection

```javascript
// timezone.js
function detectAndSaveTimezone() {
    const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    const savedTimezone = localStorage.getItem('userTimezone');
    
    if (timezone !== savedTimezone) {
        localStorage.setItem('userTimezone', timezone);
        
        // Send to server
        fetch('/api/timezone', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ timezone })
        }).catch(() => {}); // Ignore errors
    }
}

function getUserTimezone() {
    return localStorage.getItem('userTimezone') || 
           Intl.DateTimeFormat().resolvedOptions().timeZone;
}
```

---

## Booking Page

```javascript
// booking.js
class BookingPage {
    constructor() {
        this.selectedSlotId = null;
        this.selectedPriceId = null;
        this.promocodeApplied = false;
        
        this.init();
    }
    
    init() {
        this.bindPriceSelection();
        this.bindSlotSelection();
        this.bindPromocodeForm();
        this.loadAvailableSlots();
    }
    
    bindPriceSelection() {
        document.querySelectorAll('input[name="priceId"]').forEach(radio => {
            radio.addEventListener('change', (e) => {
                this.selectedPriceId = e.target.value;
                this.updateSummary();
            });
        });
    }
    
    bindSlotSelection() {
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('time-slot')) {
                this.selectSlot(e.target.dataset.slotId, e.target.dataset.time);
            }
        });
    }
    
    selectSlot(slotId, time) {
        // Deselect previous
        document.querySelectorAll('.time-slot.selected').forEach(el => {
            el.classList.remove('selected');
        });
        
        // Select new
        document.querySelector(`[data-slot-id="${slotId}"]`).classList.add('selected');
        document.getElementById('slotId').value = slotId;
        this.selectedSlotId = slotId;
        
        this.updateSummary();
    }
    
    async applyPromocode() {
        const code = document.getElementById('promocode').value.trim();
        if (!code) return;
        
        const response = await fetch('/api/book-consultation/apply-promocode', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                code, 
                priceId: this.selectedPriceId 
            })
        });
        
        const result = await response.json();
        
        if (result.valid) {
            this.promocodeApplied = true;
            this.showDiscount(result.discountPercent, result.newPrice);
        } else {
            this.showPromocodeError(result.message);
        }
    }
    
    updateSummary() {
        // Update booking summary display
    }
}

// Initialize on booking page
if (document.querySelector('.book-consultation-page')) {
    new BookingPage();
}
```

---

## Checkout Status Polling

```javascript
// checkout.js
class CheckoutStatusPoller {
    constructor(slug) {
        this.slug = slug;
        this.pollInterval = 2000;
        this.maxAttempts = 60;
        this.attempts = 0;
        
        this.startPolling();
    }
    
    async startPolling() {
        const status = await this.checkStatus();
        
        if (status === 'pending' && this.attempts < this.maxAttempts) {
            this.attempts++;
            setTimeout(() => this.startPolling(), this.pollInterval);
        } else {
            this.handleFinalStatus(status);
        }
    }
    
    async checkStatus() {
        const response = await fetch(`/api/checkout/status/${this.slug}`);
        const data = await response.json();
        return data.status;
    }
    
    handleFinalStatus(status) {
        document.querySelector('.status-pending').style.display = 'none';
        
        if (status === 'approved') {
            document.querySelector('.status-success').style.display = 'block';
        } else {
            document.querySelector('.status-failed').style.display = 'block';
        }
    }
}

// Initialize on thank you page
const checkoutSlug = document.getElementById('checkout-slug')?.value;
if (checkoutSlug && document.querySelector('.status-pending')) {
    new CheckoutStatusPoller(checkoutSlug);
}
```

---

## PHP JS Reference
Look at JavaScript in original PHP templates and `assets/` folder.

---

## Verification
- [x] Mobile menu works
- [x] FAQ accordion functions
- [x] Timezone saved correctly
- [x] Slot selection on booking
- [x] Promocode application
- [x] Checkout polling works
- [x] No JavaScript errors

---

## Implementation Summary

**Completed:** 2025-12-08

### Files Created:
- `src/main/resources/static/js/landing/components/mobile-menu.js` - Mobile menu toggle functionality
- `src/main/resources/static/js/landing/components/faq-accordion.js` - FAQ accordion for both standard and landing page styles
- `src/main/resources/static/js/landing/components/swiper.js` - Swiper carousel initialization for reviews
- `src/main/resources/static/js/landing/components/index-page.js` - Index page specific functionality (problem category drag, analytics)
- `src/main/resources/static/js/landing/timezone.js` - Timezone detection and management

### Files Updated:
- `src/main/resources/static/js/landing/main.js` - Removed duplicate functionality, now uses components
- `src/main/resources/static/js/landing/checkout.js` - Enhanced with status polling class for thank you page
- `src/main/resources/templates/landing/layout/main.html` - Removed inline scripts, added component references
- `src/main/resources/templates/landing/index.html` - Removed inline scripts, uses external components
- `src/main/resources/templates/landing/checkout-thank-you.html` - Removed inline script, uses checkout.js

### Architecture:
- Component-based architecture for reusability
- All inline JavaScript moved to external files
- Proper separation of concerns
- Components auto-initialize on DOM ready
- Exported APIs for programmatic access via `window.GoodHelp` namespace

---

## Stage 3 Complete ✅
All landing module functionality implemented. Proceed to **Stage 4: User Module**.

