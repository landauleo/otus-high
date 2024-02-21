##Postgres

###Полусинхронная репликация:

####Описание/Пошаговая инструкция выполнения:
1. Настраиваем асинхронную репликацию 
2. Выбираем 2 запроса на чтение (/user/get/{id} и /user/search из спецификации) и переносим их на чтение со слейва 
3. Делаем нагрузочный тест по методам (/user/get/{id} и /user/search из спецификации), которые перевели на слейв до и после репликации. Замеряем нагрузку мастера (CPU, la, disc usage, memory usage)
4. ОПЦИОНАЛЬНО: в качестве конфига, который хранит IP реплики сделать массив для легкого добавления реплики. Это не самый правильный способ балансирования нагрузки. Поэтому опционально
5. Настроить 2 слейва и 1 мастер
6. Включить row-based репликацию
7. Включить GTID
8. Настроить полусинхронную репликацию
9. Создать нагрузку на запись в любую тестовую таблицу. На стороне, которой нагружаем считать, сколько строк мы успешно записали
10. С помощью kill -9 убиваем мастер MySQL
11. Заканчиваем нагрузку на запись
12. Выбираем самый свежий слейв. Промоутим его до мастера. Переключаем на него второй слейв
13. Проверяем, есть ли потери транзакций

####Swagger:
localhost:8080/swagger-ui/index.html#/

#### Пошаговое выполнение с разбором команд для postgres

# Репликация в Postgres

1. Создаем сеть, запоминаем адрес -> создаем сетку, где будет работать наш контейнер

*создание сети Docker с именем "pgnet":*

`docker network create pgnet`

*получение информации о сети Docker с именем "pgnet" и просмотр информации о сетях Docker, включая их настройки, подсети, и другие параметры:*

`docker network inspect pgnet | grep Subnet`

2. Поднимаем мастера -> создаем директорию с данными, где будем хранить и править конфиги

```docker run -dit -v $PWD/pgmaster/:/var/lib/postgresql/data -e POSTGRES_PASSWORD=pass -p 5432:5432 --restart=unless-stopped --network=pgnet --name=pgmaster postgres```

`-dit` - Эти флаги указывают на использование интерактивного терминала и запуск контейнера в фоновом режиме (daemon mode). Флаг `-d` означает, что контейнер будет выполняться в фоновом режиме, `-i` указывает на интерактивный режим, который позволяет взаимодействовать с контейнером.

`-v $PWD/pgmaster/:/var/lib/postgresql/data` - Этот флаг монтирует локальный каталог `$PWD/pgmaster/` внутрь контейнера по пути `/var/lib/postgresql/data`. Это позволяет контейнеру использовать локальное хранилище для данных PostgreSQL.

`-e POSTGRES_PASSWORD=pass` - Этот флаг устанавливает переменную окружения `POSTGRES_PASSWORD` в значение "pass". Это задает пароль для пользователя PostgreSQL.

`-p 5432:5432` - Этот флаг устанавливает проброс портов, позволяя внешним системам подключаться к порту 5432 внутри контейнера, который является стандартным портом PostgreSQL.

`--restart=unless-stopped` - Этот флаг указывает, что контейнер будет автоматически перезапускаться, если он остановится, за исключением случаев, когда он будет явно остановлен пользователем.

`--network=pgnet` - Этот флаг указывает, что контейнер должен быть подключен к сети Docker с именем "pgnet". Это позволит контейнеру взаимодействовать с другими контейнерами, подключенными к той же сети.

`--name=pgmaster` - Этот флаг устанавливает имя контейнера как "pgmaster".

`postgres` - Это имя образа, на основе которого создается контейнер


3. Меняем postgresql.conf на мастере

```
ssl = off #определяем, используется ли шифрование SSL/TLS для защиты соединений с PostgreSQL-сервером -> SSL-шифрование отключено, и соединения с сервером не будут защищены SSL/TLS. Это означает, что данные, передаваемые между клиентами и сервером, могут быть переданы в открытом виде и могут быть подвержены прослушиванию или изменению
wal_level = replica #управляет уровнем журнала записи (Write-Ahead Logging, WAL) -> удет вести журнал записи на уровне, достаточном для репликации данных на другие серверы
max_wal_senders = 4 # expected slave num
```
4. Подключаемся к мастеру и создаем пользователя для репликации
```
docker exec -it pgmaster su - postgres -c psql
create role replicator with login replication password 'pass';
```

5. Добавляем запись в pg_hba.conf с ip с первого шага
   `host    replication  replicator  172.18.0.0/16  md5`
6. Перезапустим мастера
   `docker restart pgmaster`
7.  Сделаем бэкап для реплик

*получим доступ к командной оболочке контейнера "pgmaster" в интерактивном режиме:*
```
docker exec -it pgmaster bash
mkdir /pgslave
pg_basebackup -h pgmaster -D /pgslave -U replicator -v -P --wal-method=stream
```
Команда `pg_basebackup` используется в PostgreSQL для создания базовой реплики (standby) базы данных на основе существующей мастер-базы данных. В данном случае, команда выполняет создание базовой реплики с использованием опций и параметров:

`-h pgmaster` - Это опция, которая указывает хост (hostname) или IP-адрес мастер-сервера PostgreSQL, от которого будет выполняться репликация.

`-D /pgslave` - Это опция, которая указывает каталог, в котором будет размещена база данных реплики. В данном случае, реплика будет создана в каталоге "/pgslave".

`-U replicator` - Это опция, которая указывает имя пользователя (роли) PostgreSQL, который будет использоваться для подключения к мастер-серверу. В данном случае, имя пользователя "replicator" будет использоваться для репликации.

`-v` - Этот флаг включает режим подробного вывода (verbose), что означает, что команда будет выводить дополнительную информацию о процессе репликации.

`-P` - Этот флаг используется для включения прогресс-бара, который отображает процент выполнения операции репликации.

`--wal-method=stream` - Это опция, которая указывает метод передачи журналов записи (WAL) для репликации. В данном случае, метод "stream" означает, что журнал записи будет передаваться в режиме потоковой передачи (streaming replication). Это один из способов репликации в PostgreSQL, при котором данные WAL передаются непосредственно от мастер-сервера к реплике для обновления данных в реальном времени.

После выполнения этой команды, PostgreSQL будет создавать базовую реплику в каталоге "/pgslave", используя данные с мастер-сервера "pgmaster". Реплика будет работать в режиме streaming replication, что позволит ей получать обновления данных от мастер-сервера и оставаться актуальной копией мастер-базы данных.

8. Копируем директорию себе

`docker cp pgmaster:/pgslave pgslave`

9. Создадим файл, чтобы реплика узнала, что она реплика (файл у меня был создан автоматически)

`touch pgslave/standby.signal`

10. Меняем postgresql.conf на реплике

`primary_conninfo = 'host=pgmaster port=5432 user=replicator password=pass application_name=pgslave'`
из-за того, что в postgres нет реализации master-master, то он толерантно относится к ситуациям, когда в конфиге slave будут прописаны настройки master (он их просто пропустит)

11. Запускаем реплику

`docker run -dit -v $PWD/pgslave/:/var/lib/postgresql/data -e POSTGRES_PASSWORD=pass -p 15432:5432 --network=pgnet --restart=unless-stopped --name=pgslave postgres`

12. Запустим вторую реплику
```
docker cp pgmaster:/pgslave pgasyncslave

primary_conninfo = 'host=pgmaster port=5432 user=replicator password=pass application_name=pgasyncslave'

touch pgasyncslave/standby.signal

docker run -dit -v $PWD/pgasyncslave/:/var/lib/postgresql/data -e POSTGRES_PASSWORD=pass -p 25432:5432 --network=pgnet --restart=unless-stopped --name=pgasyncslave postgres
```
13. Включаем синхронную репликацию на мастере:
транзакция считается подтвержденной только тогда, когда она подтверждена на master и на всех синхронных репликах:
`synchronous_commit = on`
из этих 2х реплик мы должны синхронизировать данные с первой живой репликой в списке, то есть если pgslave умрет, то синхронизация будет с pgasyncslave

`synchronous_standby_names = 'FIRST 1 (pgslave, pgasyncslave)'`

запускаем мастер и вызываем ф-ю перезагрузки конфигурации
```
docker exec -it pgmaster su - postgres -c psql
select pg_reload_conf();
```
14. Создадим тестовую таблицу и проверим репликацию
```
select application_name, sync_state from pg_stat_replication;
create table test(id bigint primary key not null);
insert into test(id) values(1);
select * from test;
```
15. Запромоутим (повысим до мастера) реплику pgslave
```
docker stop pgmaster

docker exec -it pgslave su - postgres -c psql

select * from pg_promote();
```
переключим реплики на pgslave

`synchronous_commit = on`

из этих 2х реплик мы должны синхронизировать данные с любой живой репликой в списке >> хотя бы одна реплика должна быть синхронной и мастер будет ожидать подтверждения от хотя бы одной реплики перед завершением транзакции 
`synchronous_standby_names = 'ANY 1 (pgmaster, pgasyncslave)'`

16. Подключим вторую реплику к новому мастеру

`primary_conninfo = 'host=pgslave port=5432 user=replicator password=pass application_name=pgasyncslave'`

17. Восстановим мастер в качестве реплики (файл у меня был создан автоматически)

`touch pgmaster/standby.signal`

`primary_conninfo = 'host=pgslave port=5432 user=replicator password=pass application_name=pgmaster'`


18. Настроим логическую репликацию с текущего мастера (pgslave) на новый сервер (фактически у нас появится 2 мастера, отсюда могут быть и конфликты ниже)

Логическая репликация отличается от физической репликации, так как она работает на уровне логических изменений данных, а не на уровне бинарных журналов, как в физической репликации. Это позволяет более гибко реплицировать данные и даже трансформировать данные перед их репликацией.

`wal_level = logical`

`docker restart pgslave`

19. Создадим публикацию
```
GRANT CONNECT ON DATABASE postgres TO replicator;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO replicator;
create publication pg_pub for table test;
```
20. Создадим новый сервер для логической репликации

`docker run -dit -v $PWD/pgstandalone/:/var/lib/postgresql/data -e POSTGRES_PASSWORD=pass -p 35432:5432 --restart=unless-stopped --network=pgnet --name=pgstandalone postgres`

21. Копируем файлы
```docker exec -it pgslave su - postgres

pg_dumpall -U postgres -r -h pgslave -f /var/lib/postgresql/roles.dmp
pg_dump -U postgres -Fc -h pgslave -f /var/lib/postgresql/schema.dmp -s postgres
```
```
docker cp pgslave:/var/lib/postgresql/roles.dmp .
docker cp roles.dmp pgstandalone:/var/lib/postgresql/roles.dmp
docker cp pgslave:/var/lib/postgresql/schema.dmp .
docker cp schema.dmp pgstandalone:/var/lib/postgresql/schema.dmp
```
```
docker exec -it pgstandalone su - postgres
psql -f roles.dmp
pg_restore -d postgres -C schema.dmp
```
22. Создаем подписку

`CREATE SUBSCRIPTION pg_sub CONNECTION 'host=pgslave port=5432 user=replicator password=pass dbname=postgres' PUBLICATION pg_pub;`

23. Сделаем конфликт в данных

На sub:
`insert into test values(9);`

На pub:
`insert into test values(9);`

В логах видим:
2023-03-27 16:15:02.753 UTC [258] ERROR:  duplicate key value violates unique constraint "test_pkey"
2023-03-27 16:15:02.753 UTC [258] DETAIL:  Key (id)=(9) already exists.
2023-03-28 18:30:42.893 UTC [108] CONTEXT:  processing remote data for replication origin "pg_16395" during message type "INSERT" for replication target relation "public.test" in transaction 739, finished at 0/3026450

24. Исправляем конфликт
```
select * from pg_subscription;
SELECT pg_replication_origin_advance('pg_16395', '0/3026C28'::pg_lsn); <- message from log + 1
ALTER SUBSCRIPTION pg_sub ENABLE;
```