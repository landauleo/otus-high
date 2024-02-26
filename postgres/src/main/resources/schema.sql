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
);

CREATE TABLE IF NOT EXISTS app.post
(
    id        char(36) NOT NULL PRIMARY KEY,
    post_text text,
    user_id   char(36) NOT NULL
);

CREATE TABLE IF NOT EXISTS app.dialog_message
(
    from_user_id char(36) NOT NULL,
    to_user_id   text,
    text_message char(36) NOT NULL,
    -- Добавляем поле для хранения хэша, используемого для шардирования
    shard_id     int
);

CREATE TABLE IF NOT EXISTS app.post_audit
(
    id           char(36)                    NOT NULL PRIMARY KEY,
    user_id      char(36)                    NOT NULL,
    created_date timestamp(6) WITH TIME ZONE NOT NULL
);
