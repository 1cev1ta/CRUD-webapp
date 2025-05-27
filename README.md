# 🧩 CRUD WebApp - Task Management System
[![Java](https://img.shields.io/badge/Java-17-blue.svg?logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.5-brightgreen.svg?logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Kafka-Event%20Driven%20Architecture-black?logo=apache-kafka)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql)](https://www.postgresql.org/)
[![Testcontainers](https://img.shields.io/badge/Testcontainers-Integration%20Testing-9cf?logo=docker)](https://www.testcontainers.org/)
## 📚 Описание проекта

CRUD WebApp — это RESTful веб-приложение для управления задачами с использованием Spring Boot, Kafka, PostgreSQL, Testcontainers и других современных технологий. Сервис поддерживает создание, получение, обновление, удаление задач, а также публикует события об изменении статуса задач в Kafka для асинхронной обработки.

---

## 🚀 Возможности

- 📄 CRUD-операции над задачами через REST API
- 📦 События об изменении статуса отправляются в Kafka
- 📩 Сообщение об измененом статусе задачи отправляется пользователю
  на почту при помощи NotificationService
- ✅ Валидация и кастомные исключения
- 🧪 Покрытие unit и integration тестами (с Testcontainers)
- 🐳 Контейнеризация с Docker Compose
- 📜 Настроенный кастомный логгер (log-starter)

---

## 🏗 Стек технологий

| Категория           | Технологии |
|---------------------|------------|
| Язык                | Java 17    |
| Backend Framework   | Spring Boot 2.7.5 |
| REST API            | Spring Web |
| БД                  | PostgreSQL |
| ORM                 | Spring Data JPA |
| Kafka               | Spring for Apache Kafka |
| Тестирование        | JUnit 5, Mockito, Testcontainers |
| Логгирование        | custom log-starter |
| Докеризация         | Docker Compose |
| Интеграционные тесты| Testcontainers для Kafka и PostgreSQL |

---

## 📦 Описание структуры проекта

| **Путь**                             | **Описание**                                                                                            |
|--------------------------------------|--------------------------------------------------------------------------------------------------------|
| **`src/main/java/com/bsdev/crud_webapp/`**                                                                                               |
| `config/KafkaConfig.java`            | Конфигурация Kafka producer и consumer, фабрики сериализации и прослушивания.  |
| `controller/TaskController.java`     | REST-контроллер для задач. Реализует CRUD-операции через HTTP методы (POST, GET, PUT, DELETE).        |
| `dto/TaskRequest.java`               | DTO для создания и обновления задач (входные данные)                                                                 |
| `dto/TaskResponse.java`              | DTO для возврата задач клиенту, отображения данных задачи в ответах API. (выходные данные)                                                     |
| `dto/TaskStatusChangedDto.java`      | DTO для сообщений Kafka при изменении статуса задачи.                                                 |
| `entity/Task.java`                   | Основная JPA-сущность "Задача" для таблицы задач. Содержит атрибуты: id, title, description, userId, status.           |
| `entity/TaskStatus.java`             | Перечисление (Enum) возможных статусов задачи: `NEW`, `IN_PROGRESS`, `DONE`                                                    |
| `exception/TaskNotFoundException.java` | Кастомное исключение, выбрасываемое при отсутствии задачи с указанным ID.                             |
| `kafka/TaskStatusConsumer.java`      | Kafka Consumer, обрабатывающий события смены статуса задач и отправляющий email.              |
| `repository/TaskRepository.java`     | Репозиторий Spring Data JPA для работы с сущностью Task.                                              |
| `service/NotificationService.java`   | Сервис для отправки email-уведомлений при изменении статуса задачи через Spring Mail API.                                   |
| `service/TaskService.java`           | Сервис бизнес-логики создания, обновления, удаления и поиска задач + Kafka-публикация       |
| `CrudWebappApplication.java`         | Главный класс для запуска Spring Boot приложения (точка входа в приложение)                                                                            |
| **`src/main/resources/`**                                                                                                                |
| `application.yml`                    | Основной конфигурационный файл: настройки Kafka, PostgreSQL, email и другие.                         |
| **`src/test/java/com/bsdev/crud_webapp/`**                                                                                              |
| `AbstractContainerBaseTest.java`     | Базовый класс для интеграционных тестов с Testcontainers. Поднимает контейнеры Kafka и PostgreSQL.   |
| `controller/TaskControllerTest.java` | Интеграционные тесты контроллера задач (REST API) с использованием MockMvc.                                      |
| `service/TaskServiceIntegrationTest.java` | Интеграционные тесты `TaskService` с проверкой базы данных и Kafka сообщений.                            |
| `service/TaskServiceUnitTest.java`   | Unit-тесты бизнес-логики работы методов `TaskService` с мокированием зависимостей через Mockito.                                    |
| **`src/test/resources/`**                                                                                                               |
| `application-test.yml`               | Конфигурация для тестов. Использует Testcontainers для подключения к тестовым Kafka и PostgreSQL.     |
| **Корневые файлы**                                                                                                                     |
| `docker-compose.yml`                 | Docker Compose конфигурация для PostgreSQL , Kafka , ZooKeeper , Kafka UI.                                         |
| `pom.xml`                            | Maven зависимости и плагины и настройки сборки для проекта.                                                             |
| `README.md`                          | Документация проекта. 



---

## 🔌 REST API

Все эндпоинты по базовому пути `/tasks`.

| Метод | Путь              | Описание                             | Тело запроса                | Код ответа             |
|-------|-------------------|--------------------------------------|-----------------------------|------------------------|
| POST  | `/tasks`          | Создать новую задачу                 | `TaskRequest` JSON          | **201 Created** + `TaskResponse` |
| GET   | `/tasks/{id}`     | Получить задачу по ID                | —                           | **200 OK** + `TaskResponse`  |
| PUT   | `/tasks/{id}`     | Обновить существующую задачу         | `TaskRequest` JSON          | **204 No Content**      |
| DELETE| `/tasks/{id}`     | Удалить задачу                       | —                           | **204 No Content**      |
| GET   | `/tasks`          | Список всех задач                    | —                           | **200 OK** + List<`TaskResponse`> |

---

### 📄 DTO

```jsonc
// TaskRequest
{
  "title": "string",
  "description": "string",
  "userId": 123,
  "status": "NEW" // NEW | IN_PROGRESS | DONE
}

// TaskResponse
{
  "id": 1,
  "title": "string",
  "description": "string",
  "userId": 123,
  "status": "NEW"
}
```

---

### 📣 Kafka Integration

- Producer (KafkaTemplate<String, TaskStatusChangedDto>)

  - Конфигурируется в KafkaConfig.java

  - Сериализует ключи как String, значения как JSON

  - Топик: ${spring.kafka.listener.topic.task-status-changed} (tasks_status_changed)

- Consumer (TaskStatusConsumer)

  - Batch-листенер, ручной AckMode.MANUAL_IMMEDIATE
    
  - При получении пакета List<TaskStatusChangedDto> отправляет email через NotificationService
    
  - Ошибки обрабатывает с DefaultErrorHandler и FixedBackOff(1s, 3 retries)

---

### ✉️ Email Notifications
NotificationService использует JavaMailSender и application.yml-настройки:

```yaml
notification:
  from: bs_dev@bk.ru
  to: spirin323@gmail.com

spring:
  mail:
    host: smtp.bk.ru
    port: 465
    username: bs_dev@bk.ru
    password: secret
    protocol: smtp
    properties.mail.smtp:
      auth: true
      ssl.enable: true
      starttls.enable: true
```
Отправляет текстовое письмо о новом статусе задачи.

---

### 🧪 Тестирование

1. **Unit-тесты** (TaskServiceUnitTest)

    - Mockito для мокирования TaskRepository и KafkaTemplate
    - Проверка логики create/find/update/delete и публикации событий


2. **Integration-tests** with Testcontainers

    - AbstractContainerBaseTest поднимает контейнеры PostgreSQL и Kafka
    - TaskServiceIntegrationTest, TaskControllerTest проверяют работу всей связки


3. **Profiles**


```yaml
application-test.yml для тестов:

spring:
  datasource:
    url: jdbc:tc:postgresql:15:///testdb
    username: postgres
    password: postgres
    driver-class-name: org.testcontainers.jdbc.ContainerDatabaseDriver

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

  kafka:
    bootstrap-servers: ${spring.kafka.bootstrap-servers}
    consumer:
      group-id: task-status-group
      enable-auto-commit: false
      auto-offset-reset: earliest
      max-poll-records: 10
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    listener:
      topic:
        task-status-changed: tasks_status_changed
      ack-mode: manual_immediate
      concurrency: 1

server:
  port: 0
```
---

### 🐳 Docker Compose

Поднимает локальную среду:
```bash
docker-compose up --build
```

- PostgreSQL — localhost:5432

- Kafka Broker — localhost:9092

- ZooKeeper — localhost:2182

- Kafka UI (Provectus) — http://localhost:8080

```yaml
version: '3.8'
services:

  postgres:
    image: postgres:15
    container_name: tasks-postgres-db
    ports:
      - "5432:5432"
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: tasksdb
    volumes:
      - postgres-data:/var/lib/postgresql/data

  zookeeper_hw:
    image: confluentinc/cp-zookeeper:6.2.1
    hostname: zookeeper_hw
    container_name: zookeeper_hw
    ports:
      - "2182:2182"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2182
      ZOOKEEPER_TICK_TIME: 2000

  broker_hw:
    image: confluentinc/cp-kafka:6.2.1
    hostname: broker_hw
    container_name: broker_hw
    depends_on:
      - zookeeper_hw
    ports:
      - "29092:29092"
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper_hw:2182'
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://broker_hw:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0

  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: kafka-uitool-provectus
    ports:
      - "8080:8080"
    environment:
      KAFKA_CLUSTERS_0_NAME: local-cluster
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: broker_hw:29092
      KAFKA_CLUSTERS_0_ZOOKEEPER: zookeeper_hw:2182
      KAFKA_CLUSTERS_0_JMXPORT: 9997
    depends_on:
      - broker_hw
      - zookeeper_hw

volumes:
  postgres-data:
```

---

### Архитектура работы приложения

![Архитектура работы приложения ](https://www.plantuml.com/plantuml/svg/RLB1RXCn4BtxAto4GnKEd3YWelI0WABGfEVAzKuQAxkUbJsBr1jjXJWKYLy94HLHHBE_CFuZPdTLrOGU4gyzxxrvVfx3I2PGKvKgBfslJJ2LjbZLwC7Jb2vBq04i6NzHGWQfqADT1An0KsZoYiS28jJwvGkbZ2KC-ZH2KAhxrmUlzSp4vHWz1InBENcbtyvc4ys_USLj-iAxTAMVZKzEZvwh7Fd8dKBux2m8aTVfLdWxNciRm12CchhebhdpnQY04WXKHdbGE84QenDdboAIhgJbLscyudk-az-6xqRSYkf6Ijl1s2zUfM_xKk_DV6ac0Ol6zlUec_FInSLWaWmrSRoGsA0u8bJ_WWyUw48Zh9sLHLZnBFQyCpiGrK1xX4iQiQdwRdRuxFtpptIRhiNm3vN1XiW-8hcviuOS-YowdU3R_jPhtl9AGsLSgPu2YyQnrBfjttXwBCyrm9MaRSvD13t1I1S1ffy-g3ocpdbMsuzxawxJzr7dNpRoZCD15dcwKNiFzf_WkyaRBM0P6-7USAjvA_jU_bx6uuzIX-2BRhx_0W00 "Архитектура работы приложения ")


---

### Последовательность работы метода PUT /tasks/{id}

![Диаграмма последовательности (Sequence Diagram) работы метода PUT /tasks/{id}](https://www.plantuml.com/plantuml/svg/RP4nQyCm48Nt-nL7fcGesJkKGDgfK099vd2HbLf4bepkrANql_SiML3JRATxtpk_XtSib2GF7iX8J7Xccs3KbJDkf22uEX7Vzp58YjxRj49Yt4lo_w6ZJL_Es4BCyn9wfzkT3YbssSo7skC1L_GoFH0r3-TXFj8UI-uZYhivG-9Y-9kixfM6gIe-lPGs-8o7ymcteZru--sw7oXhbMUp4ddiIEnKTRs1jbQYjL24RUZM4t4KaipxdiAdxLubRg1HwgYz51_prJlkKIAMBr-us2wc-W0gg8vMKascZdWbCJqqGQ7MhGLPP7WRo7dD3JjLfl_z2m00 "Диаграмма последовательности (Sequence Diagram) работы метода PUT /tasks/{id}")

---

### ⚙️ Сборка и запуск

**Локально без Docker**

```bash
./mvnw clean package
./mvnw spring-boot:run
```
Сервис будет доступен на http://localhost:8081/tasks

**Docker Compose**

```bash
docker-compose up --build
```

### 👨‍💻 Автор

**Boris Spirin - Java Developer**

📧 email: bs_dev@bk.ru

🌐 github: https://github.com/1cev1ta

✍🏻 telegram (contact me): https://t.me/spi_rin

👋 linkedin (follow me): https://www.linkedin.com/in/boris-spirin/
