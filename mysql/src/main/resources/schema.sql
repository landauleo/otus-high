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
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS app.post
(
    id        char(36) NOT NULL PRIMARY KEY,
    post_text longtext,
    user_id   char(36) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS app.dialog_message
(
    from_user_id char(36) NOT NULL,
    FOREIGN KEY (from_user_id) REFERENCES app.user (id) ON DELETE CASCADE,
    to_user_id   char(36) NOT NULL,
    FOREIGN KEY (to_user_id) REFERENCES app.user (id) ON DELETE CASCADE,
    text_message char(36) NOT NULL,
    -- Добавляем поле для хранения хэша, используемого для шардирования
    shard_id     int
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Это не будет работать, так как Citus - это distributed postgres -> не mySQL
-- Создаем таблицу распределения с указанием столбца для хэширования (from_id)
-- SELECT create_distributed_table('dialog_message_entity', 'from_id');

