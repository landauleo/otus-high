##gRPC-dialogue, gRPC-client
###Разделение монолита на сервисы:

####Описание/Пошаговая инструкция выполнения:
1. Вынести систему диалогов в отдельный сервис (реализовать взаимодействие на Rest API или gRPC)

####Для самопроверки:
- отправка сообщений:
```
grpcurl --plaintext -d '{"fromUserId": "1fcb454f-2e39-4a23-9a0f-27fcaff0ee66", "toUserId": "2fcb454f-2e39-4a23-9a0f-27fcaff0ee66", "text": "Hello, World!"}' localhost:9090 landau.leo.high.generated.DialogueService/SendUserMessage
```
- получение диалогов юзера:
```
grpcurl --plaintext -d '{"userId": "1fcb454f-2e39-4a23-9a0f-27fcaff0ee66"}' localhost:9090 landau.leo.high.generated.DialogueService/GetUsersDialogs
```

####Swagger:
localhost:8080/swagger-ui/index.html#/
