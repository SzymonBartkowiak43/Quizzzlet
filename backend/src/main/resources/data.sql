-- Podstawowe role
INSERT INTO roles (name) VALUES ('USER') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT (name) DO NOTHING;

-- ===== 3 UŻYTKOWNIKÓW =====
INSERT INTO users (email, name, password)
VALUES
    ('admin@test.pl', 'Administrator', '$2a$10$zQzBzQ57yTHuC0OGejYcveQsdWziMLnkmpFX.m6F45WlC4Kr6N0Gy'),
    ('anna@test.pl', 'Anna Kowalska', '$2a$10$zQzBzQ57yTHuC0OGejYcveQsdWziMLnkmpFX.m6F45WlC4Kr6N0Gy'),
    ('piotr@test.pl', 'Piotr Nowak', '$2a$10$zQzBzQ57yTHuC0OGejYcveQsdWziMLnkmpFX.m6F45WlC4Kr6N0Gy');


-- ===== PRZYPISANIE RÓL =====
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@test.pl' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email IN ('anna@test.pl', 'piotr@test.pl') AND r.name = 'USER'
ON CONFLICT DO NOTHING;

INSERT INTO word_sets (title, description, language, translation_language, user_id, created_at, updated_at)
SELECT
    'Podstawowy angielski',
    'Najważniejsze słówka angielskie',
    'en', 'pl', u.id,
    '2025-08-09 12:00:00', '2025-08-11 21:10:09'
FROM users u WHERE u.email = 'anna@test.pl';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'hello', 'cześć', ws.id, 5, true, '2025-08-11 20:00:00', '2025-08-09 12:00:00', '2025-08-11 20:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'anna@test.pl';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'thank you', 'dziękuję', ws.id, 8, true, '2025-08-11 19:30:00', '2025-08-09 12:00:00', '2025-08-11 19:30:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'anna@test.pl';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'goodbye', 'do widzenia', ws.id, 2, false, NULL, '2025-08-09 12:00:00', '2025-08-09 12:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'anna@test.pl';

INSERT INTO videos (title, url, user_id, created_at, updated_at)
SELECT
    'Angielski dla początkujących',
    'https://www.youtube.com/watch?v=test123',
    u.id, '2025-08-10 15:00:00', '2025-08-11 21:10:09'
FROM users u WHERE u.email = 'piotr@test.pl';
