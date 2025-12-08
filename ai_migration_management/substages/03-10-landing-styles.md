# Sub-Stage 3.10: Landing Styles

## Goal
Migrate and implement all CSS/SCSS styles for landing pages.

---

## Source SCSS Location
`original_php_project/assets/landing/styles-new/`

---

## Target Structure

```
src/main/resources/static/
├── css/
│   └── landing/
│       └── main.css        # Compiled
└── scss/
    └── landing/
        ├── main.scss       # Entry point
        ├── base/
        │   ├── _variables.scss
        │   ├── _reset.scss
        │   └── _typography.scss
        ├── components/
        │   ├── _buttons.scss
        │   ├── _forms.scss
        │   ├── _cards.scss
        │   ├── _header.scss
        │   ├── _footer.scss
        │   └── _language-switcher.scss
        ├── pages/
        │   ├── _home.scss
        │   ├── _therapist-list.scss
        │   ├── _therapist-profile.scss
        │   ├── _book-consultation.scss
        │   ├── _checkout.scss
        │   └── _static.scss
        ├── _mixins.scss
        └── _utilities.scss
```

---

## Variables (from original)

```scss
// _variables.scss
// Colors
$color-primary-olive: #D5D55E;
$color-primary-carrot: #EF9665;
$color-black: #202020;
$color-white: #fff;
$color-gray-light: #F5F5F5;
$color-gray: #888;

// Typography
$font-family-primary: 'Inter', sans-serif;
$font-family-heading: 'Montserrat', sans-serif;

$font-size-base: 16px;
$font-size-small: 14px;
$font-size-large: 18px;

$font-size-h1: 48px;
$font-size-h2: 36px;
$font-size-h3: 24px;

// Spacing
$spacing-xs: 4px;
$spacing-sm: 8px;
$spacing-md: 16px;
$spacing-lg: 24px;
$spacing-xl: 48px;

// Layout
$container-max-width: 1200px;
$header-height: 80px;
$border-radius: 8px;

// Breakpoints
$breakpoint-sm: 576px;
$breakpoint-md: 768px;
$breakpoint-lg: 992px;
$breakpoint-xl: 1200px;
```

---

## Key Component Styles

### Header
```scss
.site-header {
    height: $header-height;
    background: $color-white;
    border-bottom: 1px solid $color-gray-light;
    position: fixed;
    width: 100%;
    z-index: 1000;
    
    .container {
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 100%;
    }
    
    .logo img { height: 40px; }
    
    .main-nav a {
        margin: 0 $spacing-md;
        color: $color-black;
        text-decoration: none;
        &:hover { color: $color-primary-carrot; }
    }
}
```

### Buttons
```scss
.btn-primary {
    background: $color-primary-carrot;
    color: $color-white;
    padding: $spacing-md $spacing-lg;
    border-radius: $border-radius;
    border: none;
    cursor: pointer;
    font-weight: 600;
    transition: background 0.2s;
    
    &:hover { background: darken($color-primary-carrot, 10%); }
}

.btn-secondary {
    background: transparent;
    border: 2px solid $color-primary-carrot;
    color: $color-primary-carrot;
    // ...
}
```

### Therapist Card
```scss
.therapist-card {
    background: $color-white;
    border-radius: $border-radius;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    overflow: hidden;
    transition: transform 0.2s, box-shadow 0.2s;
    
    &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 16px rgba(0,0,0,0.15);
    }
    
    .therapist-photo img {
        width: 100%;
        height: 250px;
        object-fit: cover;
    }
    
    .therapist-info {
        padding: $spacing-lg;
    }
}
```

---

## Responsive Design

```scss
// Mobile-first approach
.therapist-grid {
    display: grid;
    gap: $spacing-lg;
    
    // Mobile: single column
    grid-template-columns: 1fr;
    
    @media (min-width: $breakpoint-md) {
        grid-template-columns: repeat(2, 1fr);
    }
    
    @media (min-width: $breakpoint-lg) {
        grid-template-columns: repeat(3, 1fr);
    }
}
```

---

## PHP Style Reference

Copy styles from:
- `assets/landing/styles-new/base/`
- `assets/landing/styles-new/components/`
- `assets/landing/styles-new/pages/`

Adapt Symfony asset paths to Spring static resources.

---

## Verification
- [ ] All SCSS files compile
- [ ] Header displays correctly
- [ ] All buttons styled
- [ ] Cards look correct
- [ ] Pages match original design
- [ ] Mobile responsive
- [ ] No style conflicts

---

## Next
Proceed to **3.11: JavaScript**

