# Stage 4: User Module

## Overview

This stage implements the User Cabinet - the area where registered users manage their consultations, chat with therapists, and adjust settings.

**Estimated Sub-stages:** 10  
**Dependencies:** Stage 1, Stage 2, Stage 3  
**Next Stage:** Stage 5 - Staff Module

---

## PHP Source Reference

| Type | Path |
|------|------|
| Controllers | `original_php_project/src/User/Controller/` |
| Entities | `original_php_project/src/User/Entity/` |
| Services | `original_php_project/src/User/Service/` |
| Templates | `original_php_project/src/User/Resources/templates/` |
| Routes | `original_php_project/config/routes/user.yaml` |

---

## Sub-Stage Summary

| Sub-Stage | Description | Status |
|-----------|-------------|--------|
| 4.1 | User entities | NOT_STARTED |
| 4.2 | Repositories | NOT_STARTED |
| 4.3 | Security | NOT_STARTED |
| 4.4 | Login/Logout | NOT_STARTED |
| 4.5 | Dashboard | NOT_STARTED |
| 4.6 | Chat | NOT_STARTED |
| 4.7 | Consultations | NOT_STARTED |
| 4.8 | Settings | NOT_STARTED |
| 4.9 | Templates | NOT_STARTED |
| 4.10 | Styles | NOT_STARTED |

---

## Controllers to Implement

| PHP Controller | Java Method | Route |
|----------------|-------------|-------|
| `IndexController` | `dashboard()` | `GET /user/` |
| `LoginController` | `login()` | `GET/POST /user/login` |
| `AutoLoginController` | `autoLogin()` | `GET /user/auto-login` |
| `LogoutController` | (Spring Security) | `POST /user/logout` |
| `Chat/IndexController` | `chatList()` | `GET /user/chat` |
| `Chat/GetMessagesAjaxController` | `getMessages()` | `GET /user/chat/{id}/messages` |
| `Chat/SendMessageController` | `sendMessage()` | `POST /user/chat/{id}/send` |
| `Chat/GetUnreadMessagesAmountAjaxController` | `getUnreadCount()` | `GET /user/chat/unread` |
| `CancelConsultationController` | `cancelConsultation()` | `POST /user/consultation/{id}/cancel` |
| `RulesController` | `rules()` | `GET /user/rules` |
| `SettingsController` | `settings()` | `GET/POST /user/settings` |

---

## Entities

| Entity | Purpose |
|--------|---------|
| `User` | Main user entity |
| `UserAutologinToken` | Authentication tokens |
| `UserConsultation` | Booked consultations |
| `UserTherapist` | User-therapist relationship |
| `ChatMessage` | Chat messages (shared with Therapist) |
| `Promocode` | Discount codes |
| `UserPromocode` | User's applied promocodes |
| `UserRequestTherapist` | Therapist match requests |

---

## Key Features

### Dashboard
- Closest upcoming consultation
- List of future consultations
- Book new consultation button
- Wallet balance display
- "Set your name" reminder if not set
- "Read rules" reminder

### Chat
- List of therapists with conversations
- Message history per therapist
- Send new messages
- Unread message count
- Real-time polling for new messages

### Consultations
- View upcoming consultations
- View consultation details (time, therapist)
- Cancel consultation (with time restrictions)

### Settings
- Update full name
- Update timezone
- View email (readonly)

---

## Services

| PHP Service | Java Service |
|-------------|--------------|
| `UserCreator` | `UserService` |
| `UserAutologinTokenCreator` | `UserAuthService` |
| `ConsultationCreator` | `ConsultationService` |
| `ChatMessagesRetriever` | `UserChatService` |
| `CurrentUserRetriever` | (Spring Security) |
| `TimeHelper` | `TimezoneService` |

---

## Security

- AutoLogin token-based authentication
- Role: `ROLE_USER`
- Session management with remember-me
- Secure routes under `/user/**`

---

## Verification Checklist

- [ ] User can log in via email link
- [ ] Dashboard shows consultations
- [ ] Can view consultation details
- [ ] Can cancel consultations
- [ ] Chat works with therapists
- [ ] Settings page saves correctly
- [ ] Rules page displays
- [ ] Wallet balance shows
- [ ] i18n works throughout
- [ ] Mobile responsive

