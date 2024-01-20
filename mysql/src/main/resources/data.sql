TRUNCATE TABLE app.dialog_message;
TRUNCATE TABLE app.user;

INSERT INTO app.user ( id, first_name, second_name, biography, city )
VALUES ( '1fcb454f-2e39-4a23-9a0f-27fcaff0ee66', 'Ivan', 'Ivanov', 'a true human being', 'Los Angeles' );

INSERT INTO app.user ( id, first_name, second_name, biography, city )
VALUES ( '2fcb454f-2e39-4a23-9a0f-27fcaff0ee66', 'Petr', 'Petrov', 'a false human being', 'NY' );

INSERT INTO app.dialog_message ( from_user_id, to_user_id, text_message )
VALUES ( '2fcb454f-2e39-4a23-9a0f-27fcaff0ee66', '1fcb454f-2e39-4a23-9a0f-27fcaff0ee66', 'AAA' );

INSERT INTO app.dialog_message ( from_user_id, to_user_id, text_message )
VALUES ( '1fcb454f-2e39-4a23-9a0f-27fcaff0ee66', '2fcb454f-2e39-4a23-9a0f-27fcaff0ee66', 'BBB' );

-- Создаем таблицу распределения с указанием столбца для хэширования (from_id) -> only for citus
-- SELECT create_reference_table('app.user');
-- SELECT create_distributed_table('app.dialog_message', 'from_user_id');