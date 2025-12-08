# Sub-Stage 1.8: Database Schema

## Goal
Create the initial database schema with all tables required for the application.

---

## File to Create

### V1__Initial_Schema.sql

**Path:** `src/main/resources/db/migration/V1__Initial_Schema.sql`

```sql
-- ============================================
-- GoodHelp Database Schema
-- Initial Migration
-- ============================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- COMMON TABLES
-- ============================================

CREATE TABLE image (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    path VARCHAR(500) NOT NULL,
    mime_type VARCHAR(100),
    size BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- PSIHOLOG MODULE TABLES
-- ============================================

CREATE TABLE psiholog (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    role INTEGER NOT NULL DEFAULT 1,
    state INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE psiholog_profile (
    id BIGSERIAL PRIMARY KEY,
    psiholog_id BIGINT NOT NULL UNIQUE REFERENCES psiholog(id),
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    birth_date DATE,
    works_from DATE,
    profile_template VARCHAR(255),
    sex INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE psiholog_schedule (
    id BIGSERIAL PRIMARY KEY,
    psiholog_id BIGINT NOT NULL REFERENCES psiholog(id),
    available_at TIMESTAMP NOT NULL,
    state INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_psiholog_schedule_psiholog_state ON psiholog_schedule(psiholog_id, state);
CREATE INDEX idx_psiholog_schedule_available_at ON psiholog_schedule(available_at);

CREATE TABLE psiholog_price (
    id BIGSERIAL PRIMARY KEY,
    psiholog_id BIGINT NOT NULL REFERENCES psiholog(id),
    price INTEGER NOT NULL,
    slug VARCHAR(100),
    currency VARCHAR(10) NOT NULL DEFAULT 'UAH',
    type INTEGER NOT NULL DEFAULT 1,
    state INTEGER NOT NULL DEFAULT 1,
    pay_rate_percent INTEGER NOT NULL DEFAULT 70,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_psiholog_price_psiholog ON psiholog_price(psiholog_id);
CREATE INDEX idx_psiholog_price_slug ON psiholog_price(slug);

CREATE TABLE psiholog_settings (
    id BIGSERIAL PRIMARY KEY,
    psiholog_id BIGINT NOT NULL UNIQUE REFERENCES psiholog(id),
    timezone VARCHAR(100) NOT NULL DEFAULT 'Europe/Kiev',
    telegram_chat_id VARCHAR(100),
    schedule_time_cap VARCHAR(50) NOT NULL DEFAULT '+3 hour',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE psiholog_autologin_token (
    id BIGSERIAL PRIMARY KEY,
    psiholog_id BIGINT NOT NULL UNIQUE REFERENCES psiholog(id),
    token VARCHAR(32) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_psiholog_autologin_token ON psiholog_autologin_token(token);

-- ============================================
-- USER MODULE TABLES
-- ============================================

CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(500) NOT NULL,
    full_name VARCHAR(500) DEFAULT '',
    is_full_name_set_by_user BOOLEAN NOT NULL DEFAULT FALSE,
    timezone VARCHAR(100) NOT NULL DEFAULT 'Europe/Kiev',
    locale VARCHAR(10) NOT NULL DEFAULT 'ua',
    is_email_real BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_email ON "user"(email);

CREATE TABLE user_autologin_token (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES "user"(id),
    token VARCHAR(32) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_autologin_token ON user_autologin_token(token);

CREATE TABLE user_consultation (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    psiholog_id BIGINT NOT NULL REFERENCES psiholog(id),
    psiholog_price_id BIGINT REFERENCES psiholog_price(id),
    state INTEGER NOT NULL DEFAULT 1,
    type INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_consultation_user ON user_consultation(user_id);
CREATE INDEX idx_user_consultation_psiholog ON user_consultation(psiholog_id);

CREATE TABLE user_consultation_psiholog_schedule (
    id BIGSERIAL PRIMARY KEY,
    user_consultation_id BIGINT NOT NULL REFERENCES user_consultation(id),
    psiholog_schedule_id BIGINT NOT NULL REFERENCES psiholog_schedule(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE chat_message (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    psiholog_id BIGINT NOT NULL REFERENCES psiholog(id),
    type INTEGER NOT NULL,
    state INTEGER NOT NULL DEFAULT 1,
    body TEXT NOT NULL,
    sent_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_chat_message_user_psiholog ON chat_message(user_id, psiholog_id);
CREATE INDEX idx_chat_message_sent_at ON chat_message(sent_at);

CREATE TABLE user_psiholog (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    psiholog_id BIGINT NOT NULL REFERENCES psiholog(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, psiholog_id)
);

CREATE TABLE promocode (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(500) NOT NULL,
    state INTEGER NOT NULL DEFAULT 1,
    discount_percent INTEGER NOT NULL,
    max_use_number INTEGER DEFAULT 1,
    expire_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_promocode (
    id BIGSERIAL PRIMARY KEY,
    promocode_id BIGINT NOT NULL REFERENCES promocode(id),
    user_id BIGINT REFERENCES "user"(id),
    email VARCHAR(500) NOT NULL,
    state INTEGER NOT NULL DEFAULT 1,
    applied_at TIMESTAMP,
    used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_request_psiholog (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(500),
    email VARCHAR(500),
    phone VARCHAR(100),
    psiholog_id BIGINT REFERENCES psiholog(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE psiholog_user_notes (
    id BIGSERIAL PRIMARY KEY,
    psiholog_id BIGINT NOT NULL REFERENCES psiholog(id),
    user_id BIGINT NOT NULL REFERENCES "user"(id),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(psiholog_id, user_id)
);

-- ============================================
-- STAFF MODULE TABLES
-- ============================================

CREATE TABLE staff_user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    role INTEGER NOT NULL DEFAULT 1,
    state INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE staff_user_autologin_token (
    id BIGSERIAL PRIMARY KEY,
    staff_user_id BIGINT NOT NULL UNIQUE REFERENCES staff_user(id),
    token VARCHAR(32) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- BILLING MODULE TABLES
-- ============================================

CREATE TABLE billing_checkout (
    id BIGSERIAL PRIMARY KEY,
    psiholog_price_id BIGINT NOT NULL,
    psiholog_schedule_id BIGINT NOT NULL,
    user_promocode_id BIGINT REFERENCES user_promocode(id),
    slug VARCHAR(32) NOT NULL UNIQUE,
    user_id BIGINT,
    auth_type VARCHAR(50),
    phone VARCHAR(100),
    email VARCHAR(500),
    name VARCHAR(500),
    ga_client_id VARCHAR(50),
    ga_client_id_original VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_billing_checkout_slug ON billing_checkout(slug);

CREATE TABLE billing_order (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES "user"(id),
    billing_checkout_id BIGINT REFERENCES billing_checkout(id),
    state INTEGER NOT NULL DEFAULT 1,
    ga_client_id VARCHAR(50),
    request_cookies TEXT,
    checkout_slug VARCHAR(32) NOT NULL,
    price INTEGER NOT NULL,
    currency VARCHAR(10) NOT NULL,
    billing_product_id INTEGER DEFAULT 0,
    psiholog_price_id BIGINT,
    issuer_bank_country VARCHAR(100),
    issuer_bank_name VARCHAR(255),
    payment_system VARCHAR(100),
    phone VARCHAR(100),
    email VARCHAR(500),
    timezone VARCHAR(100),
    locale VARCHAR(10) DEFAULT 'ua',
    client_name VARCHAR(500),
    card_pan VARCHAR(50),
    card_type VARCHAR(50),
    reason VARCHAR(500),
    reason_code VARCHAR(50),
    fee VARCHAR(50),
    pending_state_at TIMESTAMP,
    approved_state_at TIMESTAMP,
    failed_state_at TIMESTAMP,
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_consultation_id BIGINT REFERENCES user_consultation(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_billing_order_checkout_slug ON billing_order(checkout_slug);
CREATE INDEX idx_billing_order_user ON billing_order(user_id);

CREATE TABLE order_log (
    id BIGSERIAL PRIMARY KEY,
    content JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_psiholog_schedule (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES billing_order(id),
    psiholog_schedule_id BIGINT NOT NULL REFERENCES psiholog_schedule(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_wallet (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES "user"(id),
    balance INTEGER NOT NULL DEFAULT 0,
    currency VARCHAR(10) NOT NULL DEFAULT 'UAH',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_wallet_operation (
    id BIGSERIAL PRIMARY KEY,
    user_wallet_id BIGINT NOT NULL REFERENCES user_wallet(id),
    amount INTEGER NOT NULL,
    currency VARCHAR(10) NOT NULL,
    type INTEGER NOT NULL,
    reason_type INTEGER NOT NULL,
    reason_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- BLOG/LANDING MODULE TABLES
-- ============================================

CREATE TABLE blog_post (
    id BIGSERIAL PRIMARY KEY,
    psiholog_id BIGINT NOT NULL REFERENCES psiholog(id),
    header VARCHAR(1000) NOT NULL,
    preview TEXT,
    body TEXT,
    main_image_id BIGINT REFERENCES image(id),
    posted_at TIMESTAMP NOT NULL,
    state INTEGER NOT NULL DEFAULT 0,
    locale VARCHAR(10) NOT NULL DEFAULT 'uk',
    slug VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_blog_post_state_posted ON blog_post(state, posted_at DESC);
CREATE INDEX idx_blog_post_slug ON blog_post(slug);

-- ============================================
-- COMMENTS
-- ============================================

COMMENT ON TABLE psiholog IS 'Psychologist accounts';
COMMENT ON TABLE psiholog_profile IS 'Psychologist profile information';
COMMENT ON TABLE psiholog_schedule IS 'Available time slots for psychologists';
COMMENT ON TABLE psiholog_price IS 'Pricing information for psychologists';
COMMENT ON TABLE psiholog_settings IS 'Psychologist account settings';
COMMENT ON TABLE "user" IS 'Client/user accounts';
COMMENT ON TABLE user_consultation IS 'Booked consultations between users and psychologists';
COMMENT ON TABLE chat_message IS 'Chat messages between users and psychologists';
COMMENT ON TABLE billing_order IS 'Payment orders';
COMMENT ON TABLE billing_checkout IS 'Checkout sessions';
COMMENT ON TABLE staff_user IS 'Admin/staff user accounts';
COMMENT ON TABLE blog_post IS 'Blog posts for the website';

-- ============================================
-- DONE
-- ============================================
```

---

## Notes

### Flyway vs Manual Migration

You have two options for applying this schema:

**Option 1: Flyway (Recommended for production)**
- Add `spring-boot-starter-flyway` dependency
- Place migration files in `src/main/resources/db/migration/`
- Flyway will auto-apply migrations on startup

**Option 2: Manual / JPA Auto-DDL (Development)**
- Set `spring.jpa.hibernate.ddl-auto=update` in `application-dev.yml`
- Run the SQL manually first, then entities will validate against it

### Table Naming

- Tables use `snake_case`
- `user` is quoted because it's a reserved word in PostgreSQL
- Join tables follow the pattern `{table1}_{table2}`

### Indexes

Key indexes have been added for:
- Foreign keys
- Frequently queried columns
- Lookup columns (email, token, slug)

---

## Verification

1. **Start PostgreSQL:**
   ```bash
   cd docker && docker-compose up -d postgres
   ```

2. **Apply schema manually:**
   ```bash
   docker exec -i goodhelp-postgres psql -U goodhelp -d goodhelp < path/to/V1__Initial_Schema.sql
   ```

3. **Or let JPA validate:**
   - Set `ddl-auto: validate` in application.yml
   - Start application - should not show schema errors

4. **Verify tables:**
   ```bash
   docker exec -it goodhelp-postgres psql -U goodhelp -d goodhelp -c "\dt"
   ```

---

## Checklist

- [ ] Schema SQL file created
- [ ] All tables from entity mapping included
- [ ] Indexes created for performance
- [ ] Foreign keys properly defined
- [ ] Schema applies without errors
- [ ] Application validates schema successfully

---

## Stage 1 Complete!

After completing this sub-stage:

1. Update `99-PROGRESS-TRACKER.md`:
   - Mark all Stage 1 sub-stages as COMPLETED
   - Update "Current Stage" to Stage 2

2. Run verification checklist from `stages/01-project-foundation.md`

3. Proceed to **Stage 2: Therapist Module**

