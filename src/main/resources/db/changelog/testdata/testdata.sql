INSERT INTO
    users (user_name,email,password)
VALUES
    ('Janek','Janek123','password'),
    ('Kamil','LocoKamilo','password');

INSERT INTO
    word_set ( user_id,title,description)
VALUES
    ('1','Sprawdzian numer 1', 'Zwierzęta pokoje'),
    ('1','Sprawdzian numer 2', 'Ubrania');

INSERT INTO
    word (word, translation, word_set_id)
VALUES
    ('kot', 'cat', 1),
    ('pies', 'dog', 1);
