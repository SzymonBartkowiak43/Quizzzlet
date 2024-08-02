insert into
    users (email, user_name, password)
values
    ('admin@wp.com','admin', '{noop}admin'),
    ('user@wp.com','user', '{noop}user');

insert into
    user_role (name, description)
values
    ('ADMIN', 'pełne uprawnienia'),
    ('USER', 'podstawowe uprawnienia, możliwość oddawania głosów');

insert into
    user_roles (user_id, role_id)
values
    (1, 1),
    (2, 2);