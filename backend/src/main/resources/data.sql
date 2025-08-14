-- ===== PODSTAWOWE ROLE (jak już masz) =====
INSERT INTO roles (name) VALUES ('USER') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT (name) DO NOTHING;

-- ===== UŻYTKOWNICY (jak już masz) =====
INSERT INTO users (email, name, password)
VALUES
    ('admin@test.pl', 'Administrator', '$2a$10$zQzBzQ57yTHuC0OGejYcveQsdWziMLnkmpFX.m6F45WlC4Kr6N0Gy'),
    ('anna@test.pl', 'Anna Kowalska', '$2a$10$zQzBzQ57yTHuC0OGejYcveQsdWziMLnkmpFX.m6F45WlC4Kr6N0Gy'),
    ('piotr@test.pl', 'Piotr Nowak', '$2a$10$zQzBzQ57yTHuC0OGejYcveQsdWziMLnkmpFX.m6F45WlC4Kr6N0Gy')
ON CONFLICT (email) DO NOTHING;

-- ===== PRZYPISANIE RÓL =====
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@test.pl' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email IN ('anna@test.pl', 'piotr@test.pl') AND r.name = 'USER'
ON CONFLICT DO NOTHING;

-- ===== 🚀 WORD SETS DLA ADMIN@TEST.PL =====
INSERT INTO word_sets (title, description, language, translation_language, user_id, created_at, updated_at)
SELECT
    'Podstawowy angielski',
    'Najważniejsze słówka angielskie dla początkujących',
    'en', 'pl', u.id,
    '2025-01-10 10:00:00', '2025-01-11 22:15:13'
FROM users u WHERE u.email = 'admin@test.pl'
ON CONFLICT DO NOTHING;

INSERT INTO word_sets (title, description, language, translation_language, user_id, created_at, updated_at)
SELECT
    'Biznesowy angielski',
    'Słówka przydatne w pracy i biznesie',
    'en', 'pl', u.id,
    '2025-01-09 14:00:00', '2025-01-11 22:15:13'
FROM users u WHERE u.email = 'admin@test.pl'
ON CONFLICT DO NOTHING;

INSERT INTO word_sets (title, description, language, translation_language, user_id, created_at, updated_at)
SELECT
    'Podróże i turystyka',
    'Niezbędne słówka podczas podróży',
    'en', 'pl', u.id,
    '2025-01-08 16:00:00', '2025-01-11 22:15:13'
FROM users u WHERE u.email = 'admin@test.pl'
ON CONFLICT DO NOTHING;

INSERT INTO word_sets (title, description, language, translation_language, user_id, created_at, updated_at)
SELECT
    'Emocje i uczucia',
    'Słownictwo związane z emocjami',
    'en', 'pl', u.id,
    '2025-01-07 12:00:00', '2025-01-11 22:15:13'
FROM users u WHERE u.email = 'admin@test.pl'
ON CONFLICT DO NOTHING;

INSERT INTO word_sets (title, description, language, translation_language, user_id, created_at, updated_at)
SELECT
    'Jedzenie i gotowanie',
    'Wszystko o jedzeniu po angielsku',
    'en', 'pl', u.id,
    '2025-01-06 09:00:00', '2025-01-11 22:15:13'
FROM users u WHERE u.email = 'admin@test.pl'
ON CONFLICT DO NOTHING;

-- ===== 🎯 SŁÓWKA - PODSTAWOWY ANGIELSKI =====
INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'hello', 'cześć', ws.id, 10, true, '2025-01-11 20:00:00', '2025-01-10 10:00:00', '2025-01-11 20:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podstawowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'thank you', 'dziękuję', ws.id, 8, true, '2025-01-11 19:30:00', '2025-01-10 10:00:00', '2025-01-11 19:30:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podstawowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'goodbye', 'do widzenia', ws.id, 5, false, '2025-01-10 15:00:00', '2025-01-10 10:00:00', '2025-01-10 15:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podstawowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'please', 'proszę', ws.id, 7, true, '2025-01-11 18:00:00', '2025-01-10 10:00:00', '2025-01-11 18:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podstawowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'sorry', 'przepraszam', ws.id, 6, false, '2025-01-11 16:30:00', '2025-01-10 10:00:00', '2025-01-11 16:30:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podstawowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'yes', 'tak', ws.id, 9, true, '2025-01-11 21:00:00', '2025-01-10 10:00:00', '2025-01-11 21:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podstawowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'no', 'nie', ws.id, 8, true, '2025-01-11 20:45:00', '2025-01-10 10:00:00', '2025-01-11 20:45:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podstawowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'good morning', 'dzień dobry', ws.id, 4, false, NULL, '2025-01-10 10:00:00', '2025-01-10 10:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podstawowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'good night', 'dobranoc', ws.id, 3, false, NULL, '2025-01-10 10:00:00', '2025-01-10 10:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podstawowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'how are you', 'jak się masz', ws.id, 5, true, '2025-01-11 17:15:00', '2025-01-10 10:00:00', '2025-01-11 17:15:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podstawowy angielski';

-- ===== 💼 SŁÓWKA - BIZNESOWY ANGIELSKI =====
INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'meeting', 'spotkanie', ws.id, 6, true, '2025-01-11 14:00:00', '2025-01-09 14:00:00', '2025-01-11 14:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Biznesowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'presentation', 'prezentacja', ws.id, 7, true, '2025-01-11 13:30:00', '2025-01-09 14:00:00', '2025-01-11 13:30:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Biznesowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'deadline', 'termin', ws.id, 8, false, '2025-01-10 16:00:00', '2025-01-09 14:00:00', '2025-01-10 16:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Biznesowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'project', 'projekt', ws.id, 9, true, '2025-01-11 15:45:00', '2025-01-09 14:00:00', '2025-01-11 15:45:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Biznesowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'budget', 'budżet', ws.id, 5, false, '2025-01-11 12:00:00', '2025-01-09 14:00:00', '2025-01-11 12:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Biznesowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'client', 'klient', ws.id, 7, true, '2025-01-11 14:30:00', '2025-01-09 14:00:00', '2025-01-11 14:30:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Biznesowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'contract', 'umowa', ws.id, 4, false, NULL, '2025-01-09 14:00:00', '2025-01-09 14:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Biznesowy angielski';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'invoice', 'faktura', ws.id, 3, false, NULL, '2025-01-09 14:00:00', '2025-01-09 14:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Biznesowy angielski';

-- ===== ✈️ SŁÓWKA - PODRÓŻE I TURYSTYKA =====
INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'airport', 'lotnisko', ws.id, 8, true, '2025-01-11 10:00:00', '2025-01-08 16:00:00', '2025-01-11 10:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podróże i turystyka';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'hotel', 'hotel', ws.id, 9, true, '2025-01-11 09:30:00', '2025-01-08 16:00:00', '2025-01-11 09:30:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podróże i turystyka';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'restaurant', 'restauracja', ws.id, 7, false, '2025-01-10 19:00:00', '2025-01-08 16:00:00', '2025-01-10 19:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podróże i turystyka';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'ticket', 'bilet', ws.id, 6, true, '2025-01-11 11:15:00', '2025-01-08 16:00:00', '2025-01-11 11:15:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podróże i turystyka';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'passport', 'paszport', ws.id, 5, false, '2025-01-10 08:00:00', '2025-01-08 16:00:00', '2025-01-10 08:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podróże i turystyka';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'suitcase', 'walizka', ws.id, 4, false, NULL, '2025-01-08 16:00:00', '2025-01-08 16:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Podróże i turystyka';

-- ===== 😊 SŁÓWKA - EMOCJE I UCZUCIA =====
INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'happy', 'szczęśliwy', ws.id, 8, true, '2025-01-11 12:30:00', '2025-01-07 12:00:00', '2025-01-11 12:30:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Emocje i uczucia';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'sad', 'smutny', ws.id, 6, false, '2025-01-10 14:00:00', '2025-01-07 12:00:00', '2025-01-10 14:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Emocje i uczucia';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'angry', 'zły', ws.id, 5, false, '2025-01-09 16:00:00', '2025-01-07 12:00:00', '2025-01-09 16:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Emocje i uczucia';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'excited', 'podekscytowany', ws.id, 7, true, '2025-01-11 13:00:00', '2025-01-07 12:00:00', '2025-01-11 13:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Emocje i uczucia';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'nervous', 'zdenerwowany', ws.id, 4, false, NULL, '2025-01-07 12:00:00', '2025-01-07 12:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Emocje i uczucia';

-- ===== 🍕 SŁÓWKA - JEDZENIE I GOTOWANIE =====
INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'pizza', 'pizza', ws.id, 10, true, '2025-01-11 18:30:00', '2025-01-06 09:00:00', '2025-01-11 18:30:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Jedzenie i gotowanie';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'water', 'woda', ws.id, 9, true, '2025-01-11 19:00:00', '2025-01-06 09:00:00', '2025-01-11 19:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Jedzenie i gotowanie';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'bread', 'chleb', ws.id, 7, false, '2025-01-10 12:00:00', '2025-01-06 09:00:00', '2025-01-10 12:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Jedzenie i gotowanie';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'coffee', 'kawa', ws.id, 8, true, '2025-01-11 07:30:00', '2025-01-06 09:00:00', '2025-01-11 07:30:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Jedzenie i gotowanie';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'apple', 'jabłko', ws.id, 6, true, '2025-01-11 16:00:00', '2025-01-06 09:00:00', '2025-01-11 16:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Jedzenie i gotowanie';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'chicken', 'kurczak', ws.id, 5, false, '2025-01-09 18:00:00', '2025-01-06 09:00:00', '2025-01-09 18:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Jedzenie i gotowanie';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'vegetables', 'warzywa', ws.id, 4, false, NULL, '2025-01-06 09:00:00', '2025-01-06 09:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'admin@test.pl' AND ws.title = 'Jedzenie i gotowanie';

-- ===== 🎥 DODATKOWE VIDEOS DLA ADMIN =====
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
-- ===== 📊 DODATKOWE ZESTAWY DLA INNYCH USERÓW (żeby było różnorodnie) =====
INSERT INTO word_sets (title, description, language, translation_language, user_id, created_at, updated_at)
SELECT
    'Zwierzęta po angielsku',
    'Nazwy zwierząt domowych i dzikich',
    'en', 'pl', u.id,
    '2025-01-05 11:00:00', '2025-01-11 22:15:13'
FROM users u WHERE u.email = 'anna@test.pl'
ON CONFLICT DO NOTHING;

INSERT INTO word_sets (title, description, language, translation_language, user_id, created_at, updated_at)
SELECT
    'Kolory i kształty',
    'Podstawowe kolory i kształty geometryczne',
    'en', 'pl', u.id,
    '2025-01-04 15:00:00', '2025-01-11 22:15:13'
FROM users u WHERE u.email = 'piotr@test.pl'
ON CONFLICT DO NOTHING;

-- Parę słówek do tych zestawów
INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'dog', 'pies', ws.id, 6, true, '2025-01-11 14:00:00', '2025-01-05 11:00:00', '2025-01-11 14:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'anna@test.pl' AND ws.title = 'Zwierzęta po angielsku';

INSERT INTO words (word, translation, word_set_id, points, star, last_practiced, created_at, updated_at)
SELECT 'red', 'czerwony', ws.id, 8, true, '2025-01-11 15:00:00', '2025-01-04 15:00:00', '2025-01-11 15:00:00'
FROM word_sets ws JOIN users u ON ws.user_id = u.id
WHERE u.email = 'piotr@test.pl' AND ws.title = 'Kolory i kształty';


-- ===== PRZYJAŹNIE =====
-- Anna i Piotr są przyjaciółmi
INSERT INTO friendships (requester_id, addressee_id, status, created_at)
SELECT
    (SELECT id FROM users WHERE email = 'anna@test.pl'),
    (SELECT id FROM users WHERE email = 'piotr@test.pl'),
    'ACCEPTED',
    CURRENT_TIMESTAMP - INTERVAL '5 days'
WHERE NOT EXISTS (
    SELECT 1 FROM friendships
    WHERE requester_id = (SELECT id FROM users WHERE email = 'anna@test.pl')
      AND addressee_id = (SELECT id FROM users WHERE email = 'piotr@test.pl')
);

-- Admin ma wysłane zaproszenie do Anny (pending)
INSERT INTO friendships (requester_id, addressee_id, status, created_at)
SELECT
    (SELECT id FROM users WHERE email = 'admin@test.pl'),
    (SELECT id FROM users WHERE email = 'anna@test.pl'),
    'PENDING',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
WHERE NOT EXISTS (
    SELECT 1 FROM friendships
    WHERE requester_id = (SELECT id FROM users WHERE email = 'admin@test.pl')
      AND addressee_id = (SELECT id FROM users WHERE email = 'anna@test.pl')
);

-- ===== GRUPY NAUKI =====
-- Grupa publiczna utworzona przez Annę
INSERT INTO study_groups (name, description, creator_id, is_private, max_members, invite_code, created_at)
SELECT
    'Angielski dla początkujących',
    'Grupa dla osób rozpoczynających naukę języka angielskiego. Wspólnie uczymy się podstaw gramatyki i słownictwa.',
    (SELECT id FROM users WHERE email = 'anna@test.pl'),
    false,
    25,
    'ENGL2024',
    CURRENT_TIMESTAMP - INTERVAL '10 days'
WHERE NOT EXISTS (
    SELECT 1 FROM study_groups WHERE name = 'Angielski dla początkujących'
);

-- Grupa prywatna utworzona przez Piotra
INSERT INTO study_groups (name, description, creator_id, is_private, max_members, invite_code, created_at)
SELECT
    'Advanced English Club',
    'Zaawansowany klub języka angielskiego. Dyskusje, literatura, biznesowy angielski.',
    (SELECT id FROM users WHERE email = 'piotr@test.pl'),
    true,
    10,
    'ADVENG01',
    CURRENT_TIMESTAMP - INTERVAL '7 days'
WHERE NOT EXISTS (
    SELECT 1 FROM study_groups WHERE name = 'Advanced English Club'
);

-- Grupa publiczna utworzona przez Admina
INSERT INTO study_groups (name, description, creator_id, is_private, max_members, invite_code, created_at)
SELECT
    'Konwersacje po angielsku',
    'Codzienne konwersacje i praktyka mówienia. Mile widziani wszyscy poziomy!',
    (SELECT id FROM users WHERE email = 'admin@test.pl'),
    false,
    50,
    'CONV2024',
    CURRENT_TIMESTAMP - INTERVAL '3 days'
WHERE NOT EXISTS (
    SELECT 1 FROM study_groups WHERE name = 'Konwersacje po angielsku'
);

-- ===== CZŁONKOSTWO W GRUPACH =====
-- Anna jest twórcą pierwszej grupy (automatycznie admin)
INSERT INTO group_members (group_id, user_id, role, joined_at)
SELECT
    sg.id,
    (SELECT id FROM users WHERE email = 'anna@test.pl'),
    'ADMIN',
    sg.created_at
FROM study_groups sg
WHERE sg.name = 'Angielski dla początkujących'
  AND NOT EXISTS (
    SELECT 1 FROM group_members
    WHERE group_id = sg.id
      AND user_id = (SELECT id FROM users WHERE email = 'anna@test.pl')
);

-- Piotr dołączył do grupy Anny jako zwykły członek
INSERT INTO group_members (group_id, user_id, role, joined_at)
SELECT
    sg.id,
    (SELECT id FROM users WHERE email = 'piotr@test.pl'),
    'MEMBER',
    CURRENT_TIMESTAMP - INTERVAL '8 days'
FROM study_groups sg
WHERE sg.name = 'Angielski dla początkujących'
  AND NOT EXISTS (
    SELECT 1 FROM group_members
    WHERE group_id = sg.id
      AND user_id = (SELECT id FROM users WHERE email = 'piotr@test.pl')
);

-- Piotr jest twórcą drugiej grupy (automatycznie admin)
INSERT INTO group_members (group_id, user_id, role, joined_at)
SELECT
    sg.id,
    (SELECT id FROM users WHERE email = 'piotr@test.pl'),
    'ADMIN',
    sg.created_at
FROM study_groups sg
WHERE sg.name = 'Advanced English Club'
  AND NOT EXISTS (
    SELECT 1 FROM group_members
    WHERE group_id = sg.id
      AND user_id = (SELECT id FROM users WHERE email = 'piotr@test.pl')
);

-- Anna została zaproszona do grupy Piotra jako moderator
INSERT INTO group_members (group_id, user_id, role, joined_at)
SELECT
    sg.id,
    (SELECT id FROM users WHERE email = 'anna@test.pl'),
    'MODERATOR',
    CURRENT_TIMESTAMP - INTERVAL '5 days'
FROM study_groups sg
WHERE sg.name = 'Advanced English Club'
  AND NOT EXISTS (
    SELECT 1 FROM group_members
    WHERE group_id = sg.id
      AND user_id = (SELECT id FROM users WHERE email = 'anna@test.pl')
);

-- Admin jest twórcą trzeciej grupy (automatycznie admin)
INSERT INTO group_members (group_id, user_id, role, joined_at)
SELECT
    sg.id,
    (SELECT id FROM users WHERE email = 'admin@test.pl'),
    'ADMIN',
    sg.created_at
FROM study_groups sg
WHERE sg.name = 'Konwersacje po angielsku'
  AND NOT EXISTS (
    SELECT 1 FROM group_members
    WHERE group_id = sg.id
      AND user_id = (SELECT id FROM users WHERE email = 'admin@test.pl')
);

-- Anna i Piotr dołączyli do grupy Admina
INSERT INTO group_members (group_id, user_id, role, joined_at)
SELECT
    sg.id,
    u.id,
    'MEMBER',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM study_groups sg, users u
WHERE sg.name = 'Konwersacje po angielsku'
  AND u.email IN ('anna@test.pl', 'piotr@test.pl')
  AND NOT EXISTS (
    SELECT 1 FROM group_members
    WHERE group_id = sg.id AND user_id = u.id
);

-- ===== PRYWATNE WIADOMOŚCI =====
-- Konwersacja między Anną a Piotrem
INSERT INTO private_messages (sender_id, recipient_id, content, created_at, is_read)
SELECT
    (SELECT id FROM users WHERE email = 'anna@test.pl'),
    (SELECT id FROM users WHERE email = 'piotr@test.pl'),
    'Cześć Piotr! Jak ci idzie nauka angielskiego?',
    CURRENT_TIMESTAMP - INTERVAL '3 days',
    true
WHERE NOT EXISTS (
    SELECT 1 FROM private_messages
    WHERE sender_id = (SELECT id FROM users WHERE email = 'anna@test.pl')
      AND content = 'Cześć Piotr! Jak ci idzie nauka angielskiego?'
);

INSERT INTO private_messages (sender_id, recipient_id, content, created_at, is_read)
SELECT
    (SELECT id FROM users WHERE email = 'piotr@test.pl'),
    (SELECT id FROM users WHERE email = 'anna@test.pl'),
    'Cześć Anna! Świetnie, dziękuję za pytanie. A tobie jak idzie grupa dla początkujących?',
    CURRENT_TIMESTAMP - INTERVAL '3 days' + INTERVAL '30 minutes',
    true
WHERE NOT EXISTS (
    SELECT 1 FROM private_messages
    WHERE sender_id = (SELECT id FROM users WHERE email = 'piotr@test.pl')
      AND content LIKE 'Cześć Anna! Świetnie, dziękuję%'
);

INSERT INTO private_messages (sender_id, recipient_id, content, created_at, is_read)
SELECT
    (SELECT id FROM users WHERE email = 'anna@test.pl'),
    (SELECT id FROM users WHERE email = 'piotr@test.pl'),
    'Super! Mam już kilku członków. Może byś mógł pomóc mi z organizacją zajęć?',
    CURRENT_TIMESTAMP - INTERVAL '3 days' + INTERVAL '1 hour',
    false
WHERE NOT EXISTS (
    SELECT 1 FROM private_messages
    WHERE sender_id = (SELECT id FROM users WHERE email = 'anna@test.pl')
      AND content LIKE 'Super! Mam już kilku członków%'
);

-- Wiadomość od Admina do Anny
INSERT INTO private_messages (sender_id, recipient_id, content, created_at, is_read)
SELECT
    (SELECT id FROM users WHERE email = 'admin@test.pl'),
    (SELECT id FROM users WHERE email = 'anna@test.pl'),
    'Witaj Anna! Widziałem, że prowadziś grupę dla początkujących. Może chciałabyś współpracować?',
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    false
WHERE NOT EXISTS (
    SELECT 1 FROM private_messages
    WHERE sender_id = (SELECT id FROM users WHERE email = 'admin@test.pl')
      AND content LIKE 'Witaj Anna! Widziałem%'
);

-- ===== WIADOMOŚCI GRUPOWE =====
-- Wiadomości w grupie "Angielski dla początkujących"
INSERT INTO group_messages (group_id, sender_id, content, created_at)
SELECT
    sg.id,
    (SELECT id FROM users WHERE email = 'anna@test.pl'),
    'Witajcie wszystkich w naszej grupie! Tutaj będziemy dzielić się materiałami i pomagać sobie nawzajem.',
    CURRENT_TIMESTAMP - INTERVAL '9 days'
FROM study_groups sg
WHERE sg.name = 'Angielski dla początkujących'
  AND NOT EXISTS (
    SELECT 1 FROM group_messages
    WHERE group_id = sg.id
      AND content LIKE 'Witajcie wszystkich%'
);

INSERT INTO group_messages (group_id, sender_id, content, created_at)
SELECT
    sg.id,
    (SELECT id FROM users WHERE email = 'piotr@test.pl'),
    'Dzięki za zaproszenie Anna! Cieszę się, że mogę pomóc nowym uczniom.',
    CURRENT_TIMESTAMP - INTERVAL '8 days' + INTERVAL '2 hours'
FROM study_groups sg
WHERE sg.name = 'Angielski dla początkujących'
  AND NOT EXISTS (
    SELECT 1 FROM group_messages
    WHERE group_id = sg.id
      AND sender_id = (SELECT id FROM users WHERE email = 'piotr@test.pl')
);

INSERT INTO group_messages (group_id, sender_id, content, created_at)
SELECT
    sg.id,
    (SELECT id FROM users WHERE email = 'anna@test.pl'),
    'Dziś proponuję skupić się na czasach przeszłych. Kto ma pytania?',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
FROM study_groups sg
WHERE sg.name = 'Angielski dla początkujących'
  AND NOT EXISTS (
    SELECT 1 FROM group_messages
    WHERE group_id = sg.id
      AND content LIKE 'Dziś proponuję skupić%'
);

-- Wiadomości w grupie "Advanced English Club"
INSERT INTO group_messages (group_id, sender_id, content, created_at)
SELECT
    sg.id,
    (SELECT id FROM users WHERE email = 'piotr@test.pl'),
    'Welcome to our advanced English club! Here we focus on business English and complex grammar structures.',
    CURRENT_TIMESTAMP - INTERVAL '6 days'
FROM study_groups sg
WHERE sg.name = 'Advanced English Club'
  AND NOT EXISTS (
    SELECT 1 FROM group_messages
    WHERE group_id = sg.id
      AND content LIKE 'Welcome to our advanced%'
);

INSERT INTO group_messages (group_id, sender_id, content, created_at)
SELECT
    sg.id,
    (SELECT id FROM users WHERE email = 'anna@test.pl'),
    'Thanks for inviting me, Piotr! I''m excited to improve my business English skills.',
    CURRENT_TIMESTAMP - INTERVAL '5 days' + INTERVAL '3 hours'
FROM study_groups sg
WHERE sg.name = 'Advanced English Club'
  AND NOT EXISTS (
    SELECT 1 FROM group_messages
    WHERE group_id = sg.id
      AND sender_id = (SELECT id FROM users WHERE email = 'anna@test.pl')
);

-- Wiadomości w grupie "Konwersacje po angielsku"
INSERT INTO group_messages (group_id, sender_id, content, created_at)
SELECT
    sg.id,
    (SELECT id FROM users WHERE email = 'admin@test.pl'),
    'Witam wszystkich! Ta grupa jest otwarta dla każdego, kto chce praktykować mówienie po angielsku.',
    CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '6 hours'
FROM study_groups sg
WHERE sg.name = 'Konwersacje po angielsku'
  AND NOT EXISTS (
    SELECT 1 FROM group_messages
    WHERE group_id = sg.id
      AND content LIKE 'Witam wszystkich! Ta grupa%'
);

INSERT INTO group_messages (group_id, sender_id, content, created_at)
SELECT
    sg.id,
    (SELECT id FROM users WHERE email = 'anna@test.pl'),
    'Świetna inicjatywa! Może moglibyśmy organizować codzienne sesje rozmowy?',
    CURRENT_TIMESTAMP - INTERVAL '2 days' + INTERVAL '7 hours'
FROM study_groups sg
WHERE sg.name = 'Konwersacje po angielsku'
  AND NOT EXISTS (
    SELECT 1 FROM group_messages
    WHERE group_id = sg.id
      AND sender_id = (SELECT id FROM users WHERE email = 'anna@test.pl')
      AND content LIKE 'Świetna inicjatywa%'
);

INSERT INTO group_messages (group_id, sender_id, content, created_at)
SELECT
    sg.id,
    (SELECT id FROM users WHERE email = 'piotr@test.pl'),
    'Great idea Anna! I can help with pronunciation sessions on weekends.',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
FROM study_groups sg
WHERE sg.name = 'Konwersacje po angielsku'
  AND NOT EXISTS (
    SELECT 1 FROM group_messages
    WHERE group_id = sg.id
      AND sender_id = (SELECT id FROM users WHERE email = 'piotr@test.pl')
      AND content LIKE 'Great idea Anna%'
);