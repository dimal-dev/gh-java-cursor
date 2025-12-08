# Sub-Stage 2.12: Therapist Styles

## Goal
Create CSS/SCSS styles for therapist cabinet matching original design.

---

## File Structure

```
src/main/resources/static/
├── css/
│   └── therapist/
│       └── main.css        # Compiled from SCSS
└── scss/
    └── psiholog/
        ├── main.scss       # Entry point
        ├── _variables.scss
        ├── _layout.scss
        ├── _sidebar.scss
        ├── _schedule.scss
        ├── _clients.scss
        ├── _chat.scss
        ├── _payments.scss
        ├── _settings.scss
        └── _components.scss
```

---

## Variables (from original)

```scss
// _variables.scss
$color-primary: #4A90A4;
$color-secondary: #5FB3B3;
$color-success: #7BC87B;
$color-warning: #E6B455;
$color-danger: #D95757;

$color-bg-main: #F5F7FA;
$color-bg-sidebar: #2D3748;
$color-text-primary: #1A202C;
$color-text-secondary: #718096;

$sidebar-width: 250px;
$header-height: 60px;

$border-radius: 8px;
$shadow-card: 0 2px 4px rgba(0, 0, 0, 0.1);
```

---

## Layout Styles

```scss
// _layout.scss
.cabinet-wrapper {
    display: flex;
    min-height: 100vh;
}

.sidebar {
    width: $sidebar-width;
    background: $color-bg-sidebar;
    position: fixed;
    height: 100vh;
}

.content {
    margin-left: $sidebar-width;
    flex: 1;
    background: $color-bg-main;
}

.page-content {
    padding: 24px;
    max-width: 1200px;
}
```

---

## Schedule Styles

```scss
// _schedule.scss
.schedule-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 8px;
}

.schedule-day {
    background: white;
    border-radius: $border-radius;
    padding: 12px;
    box-shadow: $shadow-card;
}

.schedule-slot {
    padding: 8px;
    margin: 4px 0;
    border-radius: 4px;
    cursor: pointer;
    
    &.available { background: $color-success; color: white; }
    &.booked { background: $color-primary; color: white; }
    &.unavailable { background: #E2E8F0; color: $color-text-secondary; }
    &.done { background: #A0AEC0; color: white; }
}
```

---

## Chat Styles

```scss
// _chat.scss
.chat-container {
    display: flex;
    height: calc(100vh - #{$header-height} - 48px);
}

.conversations-list {
    width: 300px;
    border-right: 1px solid #E2E8F0;
    overflow-y: auto;
}

.conversation-item {
    padding: 12px;
    border-bottom: 1px solid #E2E8F0;
    cursor: pointer;
    
    &:hover { background: #F7FAFC; }
    &.active { background: #EBF8FF; }
    
    .unread-badge {
        background: $color-danger;
        color: white;
        border-radius: 10px;
        padding: 2px 8px;
        font-size: 12px;
    }
}

.messages-area {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.message {
    max-width: 70%;
    padding: 10px 14px;
    margin: 8px;
    border-radius: 16px;
    
    &.sent { 
        background: $color-primary;
        color: white;
        align-self: flex-end;
    }
    &.received {
        background: #E2E8F0;
        align-self: flex-start;
    }
}
```

---

## SCSS Compilation

Using Maven frontend plugin or external build:

```xml
<!-- In pom.xml if using dart-sass-maven-plugin -->
<plugin>
    <groupId>com.github.nicklaus4</groupId>
    <artifactId>dart-sass-maven-plugin</artifactId>
    <version>1.0.2</version>
    <executions>
        <execution>
            <goals><goal>compile-sass</goal></goals>
        </execution>
    </executions>
</plugin>
```

Or compile manually: `sass scss/therapist/main.scss css/therapist/main.css`

---

## PHP Style Reference

Examine original PHP templates for:
- Color scheme
- Layout proportions
- Component styles
- Responsive breakpoints

---

## Verification
- [ ] SCSS compiles without errors
- [ ] Styles applied to all pages
- [ ] Colors match original design
- [ ] Responsive design works on mobile
- [ ] No style conflicts

---

## Stage 2 Complete
After this sub-stage, run full verification checklist from `stages/02-psiholog-module.md`, then proceed to **Stage 3: Landing Module**.

