CREATE TABLE app.user
(
    id          CHAR(36) NOT NULL PRIMARY KEY,
    first_name  VARCHAR(100),
    second_name VARCHAR(100),
    birthdate   DATE,
    biography   VARCHAR(250),
    city        VARCHAR(100),
    password    VARCHAR(250),
    gender      VARCHAR(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;