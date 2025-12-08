# Stage 5: Staff Module

## Overview

This stage implements the Staff/Admin Cabinet - the administrative area for managing therapists, blog posts, and payouts.

**Estimated Sub-stages:** 11  
**Dependencies:** Stage 1, Stage 2  
**Next Stage:** Stage 6 - Billing Module

---

## PHP Source Reference

| Type | Path |
|------|------|
| Controllers | `original_php_project/src/Staff/Controller/` |
| Entities | `original_php_project/src/Staff/Entity/` |
| Services | `original_php_project/src/Staff/Service/` |
| Templates | `original_php_project/src/Staff/Resources/templates/` |
| Routes | `original_php_project/config/routes/staff.yaml` |

---

## Sub-Stage Summary

| Sub-Stage | Description | Status |
|-----------|-------------|--------|
| 5.1 | Staff entities | NOT_STARTED |
| 5.2 | Repositories | NOT_STARTED |
| 5.3 | Security | NOT_STARTED |
| 5.4 | Login/Logout | NOT_STARTED |
| 5.5 | Dashboard | NOT_STARTED |
| 5.6 | Therapist list | NOT_STARTED |
| 5.7 | Add/Edit therapist | NOT_STARTED |
| 5.8 | Blog management | NOT_STARTED |
| 5.9 | Payouts management | NOT_STARTED |
| 5.10 | Templates | NOT_STARTED |
| 5.11 | Styles | NOT_STARTED |

---

## Controllers to Implement

| PHP Controller | Route |
|----------------|-------|
| `IndexController` | `GET /staff/` |
| `LoginController` | `GET/POST /staff/login` |
| `AutoLoginController` | `GET /staff/auto-login` |
| `LogoutController` | `POST /staff/logout` |
| `PsihologsController` | `GET /staff/therapists` |
| `PsihologsListAjaxController` | `GET /staff/therapists/list` |
| `AddPsihologController` | `GET/POST /staff/add-therapist` |
| `Psiholog/ProfileController` | `GET/POST /staff/therapist/{id}/profile` |
| `Psiholog/SettingsController` | `GET/POST /staff/therapist/{id}/settings` |
| `Blog/PostListController` | `GET /staff/blog` |
| `Blog/PostListDataAjaxController` | `GET /staff/blog/list` |
| `Blog/PostAddController` | `GET/POST /staff/blog/add` |
| `Blog/PostEditController` | `GET/POST /staff/blog/{id}/edit` |
| `PayoutsController` | `GET/POST /staff/payouts` |
| `PayoutsListAjaxController` | `GET /staff/payouts/list` |
| `FroalaImageUploadController` | `POST /staff/image/upload` |
| `FroalaImageDeleteController` | `DELETE /staff/image/{id}` |

---

## Key Features

### Dashboard
- Quick stats overview
- Navigation to sections

### Therapist Management
- List all therapists (paginated table)
- View/Edit therapist profile
- View/Edit therapist settings
- Add new therapist
- Activate/deactivate therapist

### Blog Management
- List all blog posts
- Create new post
- Edit existing post
- Rich text editor (WYSIWYG)
- Image upload for posts
- Publish/unpublish posts

### Payouts
- Record payouts to therapists
- View payout history
- Calculate earnings

---

## Entities

| Entity | Purpose |
|--------|---------|
| `StaffUser` | Admin user accounts |
| `StaffUserAutologinToken` | Auth tokens |

---

## Security

- Role hierarchy:
  - `ROLE_STAFF_SUPERUSER` > `ROLE_STAFF_USER`
- Access control for sensitive operations
- CSRF protection

---

## Verification Checklist

- [ ] Staff can log in
- [ ] Dashboard shows stats
- [ ] Can list therapists
- [ ] Can add new therapist
- [ ] Can edit therapist profile/settings
- [ ] Blog list displays
- [ ] Can create/edit blog posts
- [ ] Rich text editor works
- [ ] Image upload works
- [ ] Payouts can be recorded
- [ ] Payout history displays

