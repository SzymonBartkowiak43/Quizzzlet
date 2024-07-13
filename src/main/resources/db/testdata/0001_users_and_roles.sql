insert into
    users (email, password)
values
    ('admin@wp.com', '{noop}admin'),
    ('user@wp.com', '{noop}user');

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