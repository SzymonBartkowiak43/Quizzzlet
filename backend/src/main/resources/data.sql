INSERT INTO roles (name) VALUES ('USER') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT (name) DO NOTHING;

INSERT INTO users (email, name, password)
VALUES ('admin@example.com', 'admin', '$2a$10$7sDFmTgU/2SYm0V1X2Fqre4hJY8m5PzLkU5NmVX6s9ahD2uG9ogdS')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@example.com' AND r.name = 'ADMIN'
    ON CONFLICT DO NOTHING;
