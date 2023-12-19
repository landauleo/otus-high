###Масштабируемая подсистема диалогов:

####Описание/Пошаговая инструкция выполнения:
1. Отправка сообщения пользователю (метод /dialog/{user_id}/send из спецификации)
2. Получение диалога между двумя пользователями (метод /dialog/{user_id}/list из спецификации)

####Swagger:
localhost:8080/swagger-ui/index.html#/

#### Пошаговое выполнение с разбором команд для citus
- https://docs.citusdata.com/en/v11.1/installation/single_node_docker.html


    docker run -d --name citus -p 5432:5432 -e POSTGRES_PASSWORD=mypass \
    citusdata/citus:11.1
- verify it's running, and that Citus is installed


    psql -U postgres -h localhost -d postgres -c "SELECT * FROM citus_version();"
    psql -U postgres -h localhost -d postgres -c "CREATE SCHEMA app;"

- для самопроверки:

  
    EXPLAIN INSERT INTO app.dialog_message (from_user_id, to_user_id, text_message) VALUES ('1fcb454f-2e39-4a23-9a0f-27fcaff0ee66', '2fcb454f-2e39-4a23-9a0f-27fcaff0ee66', 'AAA');
    EXPLAIN INSERT INTO app.dialog_message (from_user_id, to_user_id, text_message) VALUES ('2fcb454f-2e39-4a23-9a0f-27fcaff0ee66', '1fcb454f-2e39-4a23-9a0f-27fcaff0ee66', 'BBB');