CREATE TABLE IF NOT EXISTS app.user
(
    id          char(36) NOT NULL PRIMARY KEY,
    first_name  varchar(100),
    second_name varchar(100),
    birthdate   date,
    biography   varchar(250),
    city        varchar(100),
    password    varchar(250),
    gender      varchar(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS app.post
(
    id        char(36) NOT NULL PRIMARY KEY,
    post_text LONGTEXT,
    user_id   char(36) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

