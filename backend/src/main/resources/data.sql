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

-- ===== WORD SETS =====
INSERT INTO word_sets (title, description, language, translation_language, user_id, created_at, updated_at)
SELECT
    'Podstawowy angielski',
    'Najważniejsze słówka angielskie',
    'en', 'pl', u.id,
    '2025-01-10 10:00:00', '2025-01-11 22:15:13'
FROM users u WHERE u.email = 'anna@test.pl';

-- ===== WORDS =====
INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'hello', 'cześć', ws.id, 5, true, '2025-01-11 20:00:00', '2025-01-10 10:00:00', '2025-01-11 20:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'anna@test.pl';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'thank you', 'dziękuję', ws.id, 8, true, '2025-01-11 19:30:00', '2025-01-10 10:00:00', '2025-01-11 19:30:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'anna@test.pl';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'goodbye', 'do widzenia', ws.id, 2, false, NULL, '2025-01-10 10:00:00', '2025-01-10 10:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'anna@test.pl';

-- ===== VIDEOS (tylko title, url, user_id - dopasowane do Twojej encji) =====
INSERT INTO videos (title, url, user_id, created_at, updated_at)
VALUES
-- English Learning Videos
('English Grammar Basics - Complete Guide',
 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
 (SELECT id FROM users WHERE email = 'admin@test.pl'),
 '2025-01-08 10:00:00', '2025-01-11 22:15:13'),

('English Conversation Practice - Daily Situations',
 'https://www.youtube.com/watch?v=J---aiyznGQ',
 (SELECT id FROM users WHERE email = 'piotr@test.pl'),
 '2025-01-09 14:30:00', '2025-01-11 22:15:13'),

('English Pronunciation Masterclass',
 'https://www.youtube.com/watch?v=Tt7bzxurJ1I',
 (SELECT id FROM users WHERE email = 'anna@test.pl'),
 '2025-01-10 09:15:00', '2025-01-11 22:15:13'),

('Business English Vocabulary',
 'https://www.youtube.com/watch?v=fJ9rUzIMcZQ',
 (SELECT id FROM users WHERE email = 'admin@test.pl'),
 '2025-01-10 16:00:00', '2025-01-11 22:15:13'),

('IELTS Speaking Test Tips',
 'https://www.youtube.com/watch?v=qrO4YZeyl0I',
 (SELECT id FROM users WHERE email = 'piotr@test.pl'),
 '2025-01-11 11:20:00', '2025-01-11 22:15:13'),

('English Idioms and Phrases',
 'https://www.youtube.com/watch?v=y6120QOlsfU',
 (SELECT id FROM users WHERE email = 'anna@test.pl'),
 '2025-01-11 13:45:00', '2025-01-11 22:15:13'),

('English Grammar: Past Tense vs Present Perfect',
 'https://www.youtube.com/watch?v=kJQP7kiw5Fk',
 (SELECT id FROM users WHERE email = 'admin@test.pl'),
 '2025-01-11 08:00:00', '2025-01-11 22:15:13'),

('English Listening Practice - News Report',
 'https://www.youtube.com/watch?v=lXMskKTw3Bc',
 (SELECT id FROM users WHERE email = 'piotr@test.pl'),
 '2025-01-11 15:30:00', '2025-01-11 22:15:13');