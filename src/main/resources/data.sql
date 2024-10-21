CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(100) UNIQUE,
                       user_name VARCHAR(100) UNIQUE,
                       password VARCHAR(200)
);

CREATE TABLE  user_role (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(100) UNIQUE,
                           description VARCHAR(100)
);

CREATE TABLE  user_roles (
                            user_id BIGINT,
                            role_id BIGINT,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id),
                            FOREIGN KEY (role_id) REFERENCES user_role(id)
);
CREATE TABLE  word_set (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          user_id BIGINT,
                          title VARCHAR(250),
                          description VARCHAR(250),
                          language VARCHAR(100),
                          translation_language VARCHAR(100),
                          FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE TABLE  word (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      word_set_id BIGINT,
                      word VARCHAR(250),
                      translation VARCHAR(250),
                      points INT,
                      star BOOLEAN,
                      last_practiced DATE,
                      FOREIGN KEY (word_set_id) REFERENCES word_set(id)
);
CREATE TABLE  video (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(250),
                       url VARCHAR(250),
                       date_and_time TIMESTAMP,
                       user_id BIGINT,
                       FOREIGN KEY (user_id) REFERENCES users(id)
);
CREATE TABLE  comment (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         content VARCHAR(500),
                         user_id BIGINT,
                         video_id BIGINT,
                         date_and_time TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id),
                         FOREIGN KEY (video_id) REFERENCES video(id)
);
CREATE TABLE video_rating (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              user_id BIGINT,
                              video_id BIGINT,
                              rating INT NOT NULL,
                              date_and_time TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES users(id),
                              FOREIGN KEY (video_id) REFERENCES video(id),
                              CONSTRAINT unique_user_id_video_id UNIQUE (user_id, video_id)
);
