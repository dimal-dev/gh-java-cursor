# Sub-Stage 4.10: User Styles

## Goal
Create CSS/SCSS styles for user cabinet.

---

## File Structure

```
src/main/resources/static/
├── css/user/main.css
└── scss/user/
    ├── main.scss
    ├── _variables.scss    # Inherit from common
    ├── _layout.scss
    ├── _sidebar.scss
    ├── _dashboard.scss
    ├── _chat.scss
    ├── _settings.scss
    └── _components.scss
```

---

## Key Styles

### Layout
```scss
.user-cabinet {
    display: flex;
    min-height: 100vh;
    
    .sidebar {
        width: 260px;
        background: #2D3748;
        position: fixed;
        height: 100vh;
        display: flex;
        flex-direction: column;
    }
    
    .content {
        margin-left: 260px;
        flex: 1;
        background: #F7FAFC;
    }
    
    .page-content {
        padding: 32px;
        max-width: 900px;
    }
}
```

### Dashboard Cards
```scss
.consultation-card {
    background: white;
    border-radius: 12px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 16px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    
    &.highlight {
        border-left: 4px solid $color-primary;
    }
    
    img {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        object-fit: cover;
    }
    
    .info {
        flex: 1;
        h3 { margin: 0 0 4px; }
        p { margin: 0; color: #666; }
    }
    
    .btn-cancel {
        background: #FED7D7;
        color: #C53030;
        border: none;
        padding: 8px 16px;
        border-radius: 6px;
        cursor: pointer;
    }
}
```

### Chat Styles
```scss
.chat-container {
    display: flex;
    height: calc(100vh - 100px);
    background: white;
    border-radius: 12px;
    overflow: hidden;
}

.conversation-list {
    width: 280px;
    border-right: 1px solid #E2E8F0;
    
    .conversation-item {
        padding: 16px;
        border-bottom: 1px solid #EDF2F7;
        cursor: pointer;
        
        &:hover { background: #F7FAFC; }
        &.active { background: #EBF8FF; }
        
        .avatar {
            width: 48px;
            height: 48px;
            border-radius: 50%;
        }
    }
}

.message-area {
    flex: 1;
    display: flex;
    flex-direction: column;
}
```

---

## Mobile Responsive

```scss
@media (max-width: 768px) {
    .user-cabinet {
        .sidebar {
            transform: translateX(-100%);
            transition: transform 0.3s;
            z-index: 1000;
            
            &.open { transform: translateX(0); }
        }
        
        .content {
            margin-left: 0;
        }
        
        .mobile-header {
            display: flex;
        }
    }
}
```

---

## Verification
- [ ] All styles compile
- [ ] Layout looks correct
- [ ] Dashboard cards styled
- [ ] Chat interface works
- [ ] Mobile responsive
- [ ] Consistent with overall design

---

## Stage 4 Complete
Verify all user module features work, then proceed to **Stage 5: Staff Module**.

