# Stage 3: Landing Module

## Overview

This stage implements the public-facing website including the home page, therapist catalog, booking flow, checkout, and blog.

**Estimated Sub-stages:** 11  
**Dependencies:** Stage 1, Stage 2 (partially)  
**Next Stage:** Stage 4 - User Module

---

## PHP Source Reference

| Type | Path |
|------|------|
| Controllers | `original_php_project/src/Landing/Controller/` |
| Entities | `original_php_project/src/Landing/Entity/` |
| Services | `original_php_project/src/Landing/Service/` |
| Templates | `original_php_project/src/Landing/Resources/templates/` |
| Styles | `original_php_project/assets/landing/styles-new/` |
| Routes | `original_php_project/config/routes/landing.yaml` |

---

## Sub-Stage Summary

| Sub-Stage | Description | Status |
|-----------|-------------|--------|
| 3.1 | Main layout template | COMPLETED |
| 3.2 | Home page | COMPLETED |
| 3.3 | Therapist list page | COMPLETED |
| 3.4 | Therapist profile pages | COMPLETED |
| 3.5 | Book consultation page | COMPLETED |
| 3.6 | Checkout page | COMPLETED |
| 3.7 | Thank you page | NOT_STARTED |
| 3.8 | Static pages | NOT_STARTED |
| 3.9 | Request therapist form | NOT_STARTED |
| 3.10 | Landing styles | NOT_STARTED |
| 3.11 | JavaScript functionality | NOT_STARTED |

---

## Pages to Implement

| PHP Controller | Route (UA) | Route (RU) | Route (EN) |
|----------------|------------|------------|------------|
| `IndexController` | `/` | `/ru/` | `/en/` |
| `PsihologListController` | `/therapist-list` | `/ru/therapist-list` | `/en/therapist-list` |
| `PsihologProfileController` | `/therapist/{id}` | `/ru/therapist/{id}` | `/en/therapist/{id}` |
| `BookConsultationController` | `/book-consultation/{id}` | `/ru/book-consultation/{id}` | `/en/book-consultation/{id}` |
| `CheckoutController` | `/checkout` | `/ru/checkout` | `/en/checkout` |
| `CheckoutThankYouController` | `/checkout/thank-you` | ... | ... |
| `AboutController` | `/about` | `/ru/about` | `/en/about` |
| `PricesController` | `/prices` | ... | ... |
| `ConsultationConditionsController` | `/consultation-conditions` | ... | ... |
| `PrivacyController` | `/privacy` | ... | ... |
| `TermsOfUseController` | `/terms-of-use` | ... | ... |
| `RefundPolicyController` | `/refund-policy` | ... | ... |
| `RequestPsihologController` | `/request-therapist` | ... | ... |
| `PsihologApplyController` | `/therapist-apply` | ... | ... |
| `FamilyPsihologController` | `/family-therapist` | ... | ... |
| `TeenagePsihologController` | `/teenage-therapist` | ... | ... |
| `Blog/PostListController` | `/blog` | ... | ... |
| `Blog/PostViewController` | `/blog/{slug}` | ... | ... |

---

## Key Features

### Home Page
- Hero section with main CTA
- Problem categories
- Therapist highlights
- How it works section
- Reviews section
- FAQ accordion

### Therapist List
- Grid of therapist cards
- Photo, name, specialty
- Experience years
- Price range
- "View profile" button

### Therapist Profile
- Full profile with photo carousel
- Biography, education, methods
- Specializations
- Prices for individual/couple
- Reviews
- Book consultation button

### Book Consultation
- Select time slot from available schedule
- Choose price type (individual/couple)
- Enter contact info (new/existing user)
- Apply promocode
- Timezone-aware time display

### Checkout
- Summary of booking
- Payment form (WayForPay integration)
- Order creation

### Blog
- Post list with pagination
- Post view with full content
- Related posts

---

## Services to Implement

| PHP Service | Java Service | Purpose |
|-------------|--------------|---------|
| `PsihologList` | `TherapistListService` | Get therapists for catalog |
| `PsihologProfileInfo` | `TherapistProfileService` | Profile information |
| `CheckoutCreator` | `CheckoutService` | Create checkout session |
| `OrderCheckoutCreator` | `OrderService` | Create order from checkout |
| `PostSearcher` | `BlogService` | Blog post operations |
| `CurrentTimezoneStorage` | `TimezoneService` | User timezone handling |
| `LocaleManager` | (Spring built-in) | Locale handling |

---

## Styles Reference

**SCSS Files to Migrate:**
```
assets/landing/styles-new/
├── base/
│   ├── base.scss
│   ├── typography.scss
│   └── variables.scss
├── components/
│   ├── avatar.scss
│   ├── breadcrumbs.scss
│   ├── button.scss
│   ├── error.scss
│   ├── footer.scss
│   ├── header.scss
│   ├── input.scss
│   ├── select-therapist-btn.scss
│   ├── social-icon.scss
│   ├── soft-light.scss
│   └── switch.scss
├── pages/
│   ├── main.scss (+ main/*.scss)
│   ├── blog.scss
│   ├── book-consultation.scss
│   ├── checkout.scss
│   ├── therapist-list.scss
│   ├── therapist-profile.scss
│   ├── request-therapist.scss
│   └── ...
├── mixins.scss
└── util.scss
```

**Color Variables:**
```scss
$color-primary-olive-1: #D5D55E;
$color-primary-carrot-1: #EF9665;
$color-black-2: #202020;
$color-white-1: #fff;
```

---

## JavaScript Features

- Mobile menu toggle
- Timezone detection & saving
- FAQ accordion
- Time slot selection
- Form validation
- Promocode application (AJAX)
- Checkout status polling

---

## Verification Checklist

- [ ] Home page renders with all sections
- [ ] Language switcher works (UA/RU/EN)
- [ ] Therapist list displays correctly
- [ ] Profile pages show full information
- [ ] Booking flow works end-to-end
- [ ] Checkout integrates with WayForPay
- [ ] Blog displays posts correctly
- [ ] All static pages render
- [ ] Responsive design on mobile/tablet
- [ ] SEO meta tags present
- [ ] Canonical URLs correct

---

## Transition Notes

After completing Stage 3:
1. Users can browse and book consultations
2. Payment processing ready (needs billing webhook)
3. Update progress tracker
4. Proceed to Stage 4

