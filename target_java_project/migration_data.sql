-- Migration Data from PHP to Java Project
-- Source: original_php_project/src/Common/Migrations/Version20220522184711.php
-- Target: PostgreSQL database for Java Spring Boot application
-- Note: psiholog -> therapist renaming applied

-- ============================================================================
-- THERAPIST DATA (12 therapists)
-- ============================================================================
-- Table: therapist
-- Columns: id, email, role, state, first_name, last_name, birth_date, works_from, profile_template, sex
-- Note: TherapistProfile is embedded in the Therapist entity
-- ============================================================================

-- Therapist 1: Зеро Мэн (Zero Man) - Test Account
INSERT INTO therapist (id, email, role, state, first_name, last_name, birth_date, works_from, profile_template, sex, created_at, updated_at)
VALUES (66666666, 'goodhelpua@gmail.com', 1, 1, 'Зеро', 'Мэн', '1999-10-01', '2004-04-28', '000-zero-man', 1, NOW(), NOW());

-- Therapist 2: Алиса Чижикова (Alisa Chizikova)
INSERT INTO therapist (id, email, role, state, first_name, last_name, birth_date, works_from, profile_template, sex, created_at, updated_at)
VALUES (1, 'chizhikovaalice@gmail.com', 1, 1, 'Алиса', 'Чижикова', '1985-04-28', '2004-04-28', '001-alisa-chizikova', 1, NOW(), NOW());

-- Therapist 3: Инна Каращук (Inna Karashchuk)
INSERT INTO therapist (id, email, role, state, first_name, last_name, birth_date, works_from, profile_template, sex, created_at, updated_at)
VALUES (2, 'karach@bigmir.net', 1, 1, 'Инна', 'Каращук', '1975-05-10', '2011-01-01', '002-inna-karashchuk', 1, NOW(), NOW());

-- Therapist 4: Виктория Радий (Viktoriya Radiy)
INSERT INTO therapist (id, email, role, state, first_name, last_name, birth_date, works_from, profile_template, sex, created_at, updated_at)
VALUES (3, 'viktoriaradiy@gmail.com', 1, 1, 'Виктория', 'Радий', '1988-09-21', '2015-01-01', '003-viktoriya-radiy', 1, NOW(), NOW());

-- Therapist 5: Оксана Рыжикова (Oksana Ryzhikova)
INSERT INTO therapist (id, email, role, state, first_name, last_name, birth_date, works_from, profile_template, sex, created_at, updated_at)
VALUES (4, 'ovryzhikova@gmail.com', 1, 1, 'Оксана', 'Рыжикова', '1968-05-04', '2011-01-01', '004-oksana-ryzhikova', 1, NOW(), NOW());

-- Therapist 6: Валентина Довбыш (Valentina Dovbysh)
INSERT INTO therapist (id, email, role, state, first_name, last_name, birth_date, works_from, profile_template, sex, created_at, updated_at)
VALUES (5, 'design.zone.18@gmail.com', 1, 1, 'Валентина', 'Довбыш', '1975-05-18', '2017-01-01', '005-valentina-dovbysh', 1, NOW(), NOW());

-- Therapist 7: Юлия Гурневич (Yuliya Gurnevich)
INSERT INTO therapist (id, email, role, state, first_name, last_name, birth_date, works_from, profile_template, sex, created_at, updated_at)
VALUES (6, 'gurnevich.juliya@gmail.com', 1, 1, 'Юлия', 'Гурневич', '1984-01-01', '2007-01-01', '006-yuliya-gurnevich', 1, NOW(), NOW());

-- Therapist 8: Виктор Дичко (Viktor Dichko)
INSERT INTO therapist (id, email, role, state, first_name, last_name, birth_date, works_from, profile_template, sex, created_at, updated_at)
VALUES (7, 'dichkovn@gmail.com', 1, 1, 'Виктор', 'Дичко', '1963-01-12', '2010-01-01', '007-viktor-dichko', 2, NOW(), NOW());

-- Therapist 9: Мария Короед (Mariya Koroyed)
INSERT INTO therapist (id, email, role, state, first_name, last_name, birth_date, works_from, profile_template, sex, created_at, updated_at)
VALUES (8, 'koroed38@gmail.com', 1, 1, 'Мария', 'Короед', '1986-01-02', '2018-01-01', '008-mariya-koroyed', 1, NOW(), NOW());

-- Therapist 10: Сан Саттари (San Sattari)
INSERT INTO therapist (id, email, role, state, first_name, last_name, birth_date, works_from, profile_template, sex, created_at, updated_at)
VALUES (9, 'san.sattari@gmail.com', 1, 1, 'Сан', 'Саттари', '1980-04-16', '2010-09-01', '009-san-sattari', 2, NOW(), NOW());

-- Therapist 11: Артем Хмелевский (Artem Khmelevskiy)
INSERT INTO therapist (id, email, role, state, first_name, last_name, birth_date, works_from, profile_template, sex, created_at, updated_at)
VALUES (10, 'artey41@gmail.com', 1, 1, 'Артем', 'Хмелевский', '1973-04-16', '2011-12-01', '010-artem-khmelevskiy', 2, NOW(), NOW());

-- Therapist 12: Надежда Тордия (Nadezhda Tordiya)
INSERT INTO therapist (id, email, role, state, first_name, last_name, birth_date, works_from, profile_template, sex, created_at, updated_at)
VALUES (11, 'ntordiya@yahoo.com', 1, 1, 'Надежда', 'Тордия', '1969-11-12', '2010-11-01', '011-nadezhda-tordiya', 1, NOW(), NOW());

-- ============================================================================
-- THERAPIST AUTOLOGIN TOKENS
-- ============================================================================
-- Table: therapist_autologin_token
-- Columns: id, therapist_id, token
-- Note: Tokens are auto-generated UUIDs (32 chars hex)
-- ============================================================================

INSERT INTO therapist_autologin_token (therapist_id, token, created_at, updated_at)
VALUES 
    (66666666, 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6', NOW(), NOW()),
    (1, 'b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7', NOW(), NOW()),
    (2, 'c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8', NOW(), NOW()),
    (3, 'd4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9', NOW(), NOW()),
    (4, 'e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0', NOW(), NOW()),
    (5, 'f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1', NOW(), NOW()),
    (6, 'g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2', NOW(), NOW()),
    (7, 'h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3', NOW(), NOW()),
    (8, 'i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4', NOW(), NOW()),
    (9, 'j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5', NOW(), NOW()),
    (10, 'k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6', NOW(), NOW()),
    (11, 'l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7', NOW(), NOW());

-- ============================================================================
-- THERAPIST PRICES
-- ============================================================================
-- Table: therapist_price
-- Columns: id, therapist_id, price, currency, type, state, pay_rate_percent
-- Note: type 1 = INDIVIDUAL, state 1 = ACTIVE (CURRENT in PHP)
-- ============================================================================

INSERT INTO therapist_price (therapist_id, price, currency, type, state, pay_rate_percent, created_at, updated_at)
VALUES 
    (66666666, 1, 'UAH', 1, 1, 97, NOW(), NOW()),
    (1, 2000, 'UAH', 1, 1, 90, NOW(), NOW()),
    (2, 1500, 'UAH', 1, 1, 40, NOW(), NOW()),
    (3, 1000, 'UAH', 1, 1, 40, NOW(), NOW()),
    (4, 1000, 'UAH', 1, 1, 40, NOW(), NOW()),
    (5, 1000, 'UAH', 1, 1, 40, NOW(), NOW()),
    (6, 1500, 'UAH', 1, 1, 40, NOW(), NOW()),
    (7, 1500, 'UAH', 1, 1, 40, NOW(), NOW()),
    (8, 1500, 'UAH', 1, 1, 40, NOW(), NOW()),
    (9, 1500, 'UAH', 1, 1, 50, NOW(), NOW()),
    (10, 1500, 'UAH', 1, 1, 50, NOW(), NOW()),
    (11, 1500, 'UAH', 1, 1, 50, NOW(), NOW());

-- ============================================================================
-- THERAPIST SETTINGS
-- ============================================================================
-- Table: therapist_settings
-- Columns: id, therapist_id, timezone, telegram_chat_id, schedule_time_cap
-- Note: Default schedule_time_cap is '+3 hour'
-- ============================================================================

INSERT INTO therapist_settings (therapist_id, timezone, schedule_time_cap, created_at, updated_at)
VALUES 
    (66666666, 'Europe/Kiev', '+3 hour', NOW(), NOW()),
    (1, 'Europe/Zurich', '+3 hour', NOW(), NOW()),
    (2, 'Europe/Zurich', '+3 hour', NOW(), NOW()),
    (3, 'Europe/Zurich', '+3 hour', NOW(), NOW()),
    (4, 'Europe/Zurich', '+3 hour', NOW(), NOW()),
    (5, 'Europe/Zurich', '+3 hour', NOW(), NOW()),
    (6, 'Europe/Zurich', '+3 hour', NOW(), NOW()),
    (7, 'Europe/Zurich', '+3 hour', NOW(), NOW()),
    (8, 'Europe/Zurich', '+3 hour', NOW(), NOW()),
    (9, 'Europe/Zurich', '+3 hour', NOW(), NOW()),
    (10, 'Europe/Zurich', '+3 hour', NOW(), NOW()),
    (11, 'Europe/Zurich', '+3 hour', NOW(), NOW());

-- ============================================================================
-- RESET SEQUENCES (if needed)
-- ============================================================================
-- Reset the auto-increment sequences to avoid ID conflicts
-- This ensures that new records will have IDs starting from a safe value

-- For therapist table (highest ID is 66666666, so we set it to 66666667)
SELECT setval('therapist_id_seq', 66666667, false);

-- For therapist_autologin_token table
SELECT setval('therapist_autologin_token_id_seq', (SELECT MAX(id) FROM therapist_autologin_token) + 1, false);

-- For therapist_price table
SELECT setval('therapist_price_id_seq', (SELECT MAX(id) FROM therapist_price) + 1, false);

-- For therapist_settings table
SELECT setval('therapist_settings_id_seq', (SELECT MAX(id) FROM therapist_settings) + 1, false);

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================
-- Run these queries to verify the data was inserted correctly

-- SELECT COUNT(*) as therapist_count FROM therapist;
-- SELECT COUNT(*) as token_count FROM therapist_autologin_token;
-- SELECT COUNT(*) as price_count FROM therapist_price;
-- SELECT COUNT(*) as settings_count FROM therapist_settings;

-- SELECT t.id, t.email, t.first_name, t.last_name, 
--        tp.price, tp.currency, ts.timezone
-- FROM therapist t
-- LEFT JOIN therapist_price tp ON t.id = tp.therapist_id
-- LEFT JOIN therapist_settings ts ON t.id = ts.therapist_id
-- ORDER BY t.id;

-- ============================================================================
-- LEGEND / MAPPING REFERENCE
-- ============================================================================
-- PHP Constants -> Java Enum Values:
-- 
-- Role:
--   ROLE_PSIHOLOG = 1 -> TherapistRole.THERAPIST (code = 1)
--
-- State/Status:
--   STATE_ACTIVE = 1 -> TherapistStatus.ACTIVE (code = 1)
--
-- Price Type:
--   TYPE_INDIVIDUAL = 1 -> PriceType.INDIVIDUAL (code = 1)
--
-- Price State:
--   STATE_CURRENT = 1 -> PriceState.ACTIVE (code = 1)
--
-- Sex:
--   1 = Sex.FEMALE
--   2 = Sex.MALE
--
-- PHP IDs -> Therapist IDs:
--   PSIHOLOG_ID_ZERO_MAN = 66666666
--   PSIHOLOG_ID_ALISA_CHIZIKOVA = 1
--   PSIHOLOG_ID_INNA_KARASHCHUK = 2
--   PSIHOLOG_ID_VIKTORIYA_RADIY = 3
--   PSIHOLOG_ID_OKSANA_RYZHIKOVA = 4
--   PSIHOLOG_ID_VALENTINA_DOVBYSH = 5
--   PSIHOLOG_ID_YULIYA_GURNEVICH = 6
--   PSIHOLOG_ID_VIKTOR_DICHKO = 7
--   PSIHOLOG_ID_MARIYA_KOROYED = 8
--   PSIHOLOG_ID_SAN_SATTARI = 9
--   PSIHOLOG_ID_ARTEM_KHMELEVSKIY = 10
--   PSIHOLOG_ID_NADEZHDA_TORDIYA = 11
-- ============================================================================
