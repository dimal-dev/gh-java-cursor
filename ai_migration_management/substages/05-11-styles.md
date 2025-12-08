# Sub-Stage 5.11: Staff Styles

## Goal
Create CSS/SCSS styles for staff cabinet.

---

## File Structure

```
src/main/resources/static/
├── css/staff/main.css
└── scss/staff/
    ├── main.scss
    ├── _variables.scss
    ├── _layout.scss
    ├── _sidebar.scss
    ├── _tables.scss
    ├── _forms.scss
    └── _dashboard.scss
```

---

## Admin-Style Layout

```scss
.staff-cabinet {
    display: flex;
    min-height: 100vh;
    
    .sidebar {
        width: 240px;
        background: #1a202c;
        color: white;
        position: fixed;
        height: 100vh;
        
        .logo {
            padding: 20px;
            font-weight: bold;
            font-size: 18px;
            border-bottom: 1px solid rgba(255,255,255,0.1);
        }
        
        nav a {
            display: block;
            padding: 12px 20px;
            color: rgba(255,255,255,0.7);
            text-decoration: none;
            
            &:hover { background: rgba(255,255,255,0.1); }
            &.active { 
                background: rgba(255,255,255,0.1);
                color: white;
                border-left: 3px solid $color-primary;
            }
        }
    }
    
    .content {
        margin-left: 240px;
        flex: 1;
        background: #f7fafc;
    }
    
    .page-content {
        padding: 24px;
    }
}
```

---

## Data Tables

```scss
.data-table {
    width: 100%;
    background: white;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    
    th, td {
        padding: 12px 16px;
        text-align: left;
        border-bottom: 1px solid #e2e8f0;
    }
    
    th {
        background: #f7fafc;
        font-weight: 600;
        color: #4a5568;
    }
    
    tr:hover td {
        background: #f7fafc;
    }
    
    .actions {
        white-space: nowrap;
        
        a, button {
            margin-right: 8px;
        }
    }
}

.pagination {
    display: flex;
    justify-content: center;
    gap: 8px;
    padding: 20px;
    
    button {
        padding: 8px 12px;
        border: 1px solid #e2e8f0;
        background: white;
        border-radius: 4px;
        cursor: pointer;
        
        &.active {
            background: $color-primary;
            color: white;
            border-color: $color-primary;
        }
    }
}
```

---

## Form Styles

```scss
.form-group {
    margin-bottom: 20px;
    
    label {
        display: block;
        margin-bottom: 6px;
        font-weight: 500;
        color: #374151;
    }
    
    input, select, textarea {
        width: 100%;
        padding: 10px 12px;
        border: 1px solid #d1d5db;
        border-radius: 6px;
        font-size: 14px;
        
        &:focus {
            outline: none;
            border-color: $color-primary;
            box-shadow: 0 0 0 3px rgba($color-primary, 0.1);
        }
    }
    
    textarea {
        min-height: 120px;
        resize: vertical;
    }
    
    .error {
        color: #dc2626;
        font-size: 13px;
        margin-top: 4px;
    }
}
```

---

## Verification
- [ ] All styles compile
- [ ] Admin layout looks professional
- [ ] Tables styled correctly
- [ ] Forms usable
- [ ] Responsive design

---

## Stage 5 Complete
Verify all staff features work, then proceed to **Stage 6: Billing Module**.

