# java-pisense

Система мониторинга физических величин с датчиков, подключённых к Raspberry Pi.

Проект состоит из двух сервисов:

- `pisense-bridge` — работает на Raspberry Pi, обнаруживает и опрашивает датчики;
- `pisense-server` — работает на ПК с PostgreSQL, принимает и хранит измерения.

## Стек

`Java 21` · `Spring Boot 3.3.2` · `Spring MVC` · `Spring Data JPA` · `PostgreSQL 16` · `H2` · `Pi4J 2.6.0` · `Lombok` · `Maven` · `Docker Compose` · `Spring Boot Actuator` · `Swagger UI` · `JUnit` / `Mockito`

## Архитектура

```text
Физические датчики
        ↓
I2C / SPI / 1-Wire
        ↓
pisense-bridge на Raspberry Pi  (порт 8080)
        ↓ HTTP JSON
pisense-server                  (порт 9090)
        ↓
PostgreSQL 16                   (порт 5432)
```

`pisense-bridge` отвечает за работу с физическим оборудованием.
`pisense-server` отвечает за смысл данных, бизнес-конфигурацию и долговременное хранение.

Каждый сервис запускается в собственном Docker-контейнере, описание — в `docker-compose.yml`.

## Модули

| Модуль | Maven-артефакт | Содержимое |
|---|---|---|
| `common` | `pisense-common` | Общие ENUM: `BusType`, `SensorType`, `SensorStatus`, `BatchStatus` |
| `bridge` | `pisense-bridge` | Сканирование шин, опрос датчиков, HTTP-клиент к server |
| `server` | `pisense-server` | Модели данных и схема БД `schema.sql` |

### Классы `bridge`

| Класс | Роль |
|---|---|
| `PisenseBridgeApplication` | Точка входа Spring Boot |
| `model.SensorBus` | Интерфейс шины: `discover()` и `open(DetectedSensor)` |
| `model.Sensor` | Интерфейс драйвера датчика: `getType()` и `read()` |
| `model.DetectedSensor` | Найденное устройство: `busType`, `sensorType`, `address`, `model`, `serialNumber` |
| `driver.mock.MockBus`, `driver.mock.MockSensor` | Эмуляция шины и датчика для профиля `mock` |
| `service.DiscoveryService` | Собирает `DetectedSensor` в DTO и отправляет на server |
| `service.mappers.SensorMapper` | `DetectedSensor` → `DetectedSensorDto` |
| `scheduler.DiscoveryScheduler` | Запуск discovery по расписанию |
| `client.Client` | `RestClient` для `POST /api/v1/bridges/{bridgeId}/discovery` |
| `dto.DiscoverRequestDto` | Тело запроса: `sensors` (список `DetectedSensorDto`) |
| `dto.DetectedSensorDto` | `busType`, `sensorType`, `address`, `model`, `serialNumber` |
| `controller.HomeController` | Отдаёт статическую `static/index.html` на `/` |

### Модели `server`

`Sensor`, `MeasurementClass`, `Locations`, `Batch`, `Measurement` — обычные POJO.
JPA-аннотации не стоят, контроллеры, сервисы и репозитории сервера пока отсутствуют:
реализован только приём discovery на стороне bridge.

## Бизнес-логика

### 1. Запуск bridge

Bridge не хранит сведения о датчиках в энергонезависимой памяти.
При каждом запуске он заново сканирует доступные шины через `SensorBus.discover()`.

Для каждого найденного устройства bridge получает:

- тип шины `busType` — `I2C`, `SPI` или `ONEWIRE`;
- тип измеряемой величины `sensorType` — `TEMPERATURE`, `PRESSURE`, `HUMIDITY`, `ILLUMINANCE`;
- адрес на шине `address`, например `0x76`;
- модель `model`, если она известна;
- серийный номер `serialNumber`, если он поддерживается.

Физический источник определяется комбинацией `busType + address`. Именно по этим двум полям
работают `equals` и `hashCode` у `DetectedSensor` (`@EqualsAndHashCode(of = {"busType", "address"})`).
Один и тот же адрес может существовать на разных шинах, поэтому `address` сам по себе не уникален.

### 2. Регистрация обнаруженных устройств

`DiscoveryScheduler` вызывает `DiscoveryService.reportDiscovery()` по расписанию
(`pisense.poll-interval-ms`, первый запуск через 5 секунд).
Если список пуст, запрос не отправляется.

Запрос уходит из `Client.sendDiscovery()`:

```http
POST /api/v1/bridges/{bridgeId}/discovery
Content-Type: application/json

{
  "sensors": [
    {
      "busType": "ONEWIRE",
      "sensorType": "TEMPERATURE",
      "address": "0x76",
      "model": "DS18B20",
      "serialNumber": "80-1234566"
    }
  ]
}
```

`bridgeId` берётся из настройки `pisense.bridge-id`.
Server выполняет идемпотентную синхронизацию:

1. находит существующий датчик по `busType` + `address`;
2. создаёт новый, если такого нет;
3. обновляет `model`, `serial_number`, `last_seen_at`;
4. не удаляет датчики, которые не пришли в текущем сканировании.

Новый датчик получает статус `DISCOVERED`.
После назначения класса величины и места размещения он может перейти в `ACTIVE`.

Ошибка отправки не останавливает сервис: `DiscoveryScheduler` ловит исключение и пишет `log.warn`.

### 3. Конфигурация датчика

Bridge знает технические характеристики устройства, но не определяет его бизнес-смысл.

Server хранит в таблице `sensors`:

- `measurement_class_id` — класс физической величины;
- `location_id` — место расположения;
- `status` — статус датчика;
- единица измерения и описание класса лежат в `measurement_classes`.

Изменение конфигурации выполняется через:

```http
PATCH /api/v1/sensors/{sensorId}

{
  "measurementClassId": 1,
  "locationId": 2,
  "status": "ACTIVE"
}
```

Ответ датчика содержит `measurementClass` и `location` вложенными объектами,
а также поля `address`, `model`, `serialNumber`, `createdAt`, `lastSeenAt`.

### 4. Опрос датчиков

Bridge опрашивает датчики через `Sensor.read()` последовательно.
Ошибка одного датчика не останавливает цикл опроса остальных устройств.

Результат одного опроса состоит из полей:

- `measurementId` — UUID измерения (`measurements.uuid`);
- `sensorId` — идентификатор датчика (`measurements.sensor_id`);
- `measurementValue` — числовое значение (`measurements.measurement_value`);
- `measuredAt` — время измерения (`measurements.measured_at`).

Сейчас профиль `mock` возвращает синусоиду вокруг базового значения плюс шум:
`MockSensor.read()`.

### 5. Формирование batch

Bridge объединяет несколько измерений в один пакет. Пакет получает UUID `batchId`.

```text
один batch
        ├── measurement
        ├── measurement
        └── measurement
```

Пакет отправляется на server:

```http
POST /api/v1/measurement-batches
```

```json
{
  "batchId": "550e8400-e29b-41d4-a716-446655440000",
  "measuredAt": "2026-11-18T12:37:54.530Z",
  "measurements": [
    {
      "measurementId": "8c8f1e20-d5c3-4f5a-9e1d-f45c1b6b1234",
      "sensorId": 42,
      "measurementValue": 23.7,
      "measuredAt": "2026-11-18T12:37:54.530Z"
    }
  ]
}
```

### 6. Обработка batch на server

Server выполняет следующие действия:

1. валидирует JSON и обязательные поля;
2. проверяет уникальность `batchId` (`batches.uuid`);
3. проверяет существование датчиков (`measurements.sensor_id`);
4. проверяет значения и временные метки;
5. создаёт запись в `batches`;
6. создаёт связанные записи в `measurements` (`measurements.batch_id` указывает на `batches.id`);
7. фиксирует изменения одной транзакцией.

Batch и его измерения сохраняются атомарно:

```text
либо сохраняются batch и все measurements,
либо не сохраняется ни одна запись.
```

## Идемпотентность

Идемпотентность нужна из-за возможной потери HTTP-ответа:

1. bridge отправил batch;
2. server сохранил данные;
3. ответ потерялся из-за сетевой ошибки;
4. bridge повторил отправку;
5. server обнаружил существующий `batchId`;
6. дубликаты не создаются.

Два уровня идентификаторов:

- `batchId` — уникальный идентификатор пакета, колонка `batches.uuid`;
- `measurementId` — уникальный идентификатор измерения, колонка `measurements.uuid`.

UUID генерирует bridge. В `schema.sql` у этих колонок нет `DEFAULT`,
поэтому server сохраняет присланное значение.

Ограничения базы данных:

```text
ux_batches_uuid       UNIQUE (batches.uuid)
ux_measurements_uuid  UNIQUE (measurements.uuid)
```

Проверка в Java-сервисе не заменяет ограничение БД: только БД защищает от гонки двух одновременных запросов.

Статус повторной обработки описан в `BatchStatus`:

```text
ACCEPTED   пакет принят впервые, запись создана, ответ 201
REJECTED   пакет уже был принят ранее, повторная отправка проигнорирована, ответ 200
```

## ENUM-значения

Значения в Java и в JSON должны совпадать посимвольно со значениями в `CHECK`-ограничениях `schema.sql`.

### `SensorStatus` — `sensors.status`

| Значение | Смысл |
|---|---|
| `DISCOVERED` | найден при сканировании, но ещё не настроен |
| `ACTIVE` | разрешён для работы и опроса |
| `TEMPORARILY_UNAVAILABLE` | временно не отвечает |
| `DISABLED` | отключён администратором |
| `RETIRED` | выведен из эксплуатации |

Датчики не удаляются, чтобы история измерений не потеряла связь с источником.

### `BatchStatus` — `batches.status`

| Значение | Смысл |
|---|---|
| `ACCEPTED` | пакет успешно принят |
| `REJECTED` | повторная отправка уже известного пакета |

### `BusType` — тип шины

```text
I2C, SPI, ONEWIRE
```

### `SensorType` — `measurement_classes.type_code`

```text
TEMPERATURE, PRESSURE, HUMIDITY, ILLUMINANCE
```

## Схема базы данных

Источник истины — `server/src/main/resources/schema.sql` (версия v2).
Нативные ENUM-типы не используются: значения хранятся в `varchar(100)` и проверяются `CHECK`.

![ER-диаграмма](docs/pisense_schema.svg)

```text
measurement_classes 1 ──── N sensors
locations           1 ──── N sensors
batches             1 ──── N measurements
sensors             1 ──── N measurements
```

Таблиц `bridges` и `buses` в версии v2 нет.

### `measurement_classes`

| Колонка | Тип | NULL | Ограничения |
|---|---|---|---|
| `id` | `bigint` | нет | PK, `GENERATED BY DEFAULT AS IDENTITY` |
| `type_code` | `varchar(100)` | нет | `CHECK (type_code IN ('TEMPERATURE','PRESSURE','HUMIDITY','ILLUMINANCE'))`, ограничение `ck_measurement_classes_type_code` |
| `unit_of_measurement` | `varchar(50)` | нет | — |
| `description` | `varchar(255)` | нет | — |

### `locations`

| Колонка | Тип | NULL | Ограничения |
|---|---|---|---|
| `id` | `bigint` | нет | PK, `GENERATED BY DEFAULT AS IDENTITY` |
| `name` | `varchar(100)` | нет | — |
| `description` | `varchar(255)` | нет | — |

### `sensors`

| Колонка | Тип | NULL | Ограничения |
|---|---|---|---|
| `id` | `bigint` | нет | PK, `GENERATED BY DEFAULT AS IDENTITY` |
| `address` | `varchar(100)` | нет | адрес на шине, например `0x76` |
| `model` | `varchar(255)` | да | например `BME280` |
| `serial_number` | `varchar(255)` | да | — |
| `measurement_class_id` | `bigint` | нет | FK → `measurement_classes (id)`, `ON DELETE RESTRICT` |
| `location_id` | `bigint` | да | FK → `locations (id)`, `ON DELETE RESTRICT` |
| `status` | `varchar(100)` | нет | `DEFAULT 'DISCOVERED'`, `ck_sensors_status` |
| `created_at` | `timestamptz` | нет | `DEFAULT CURRENT_TIMESTAMP` |
| `last_seen_at` | `timestamptz` | да | обновляется при discovery |

Внешние ключи: `fk_sensors_measurement_class`, `fk_sensors_location`, оба `DEFERRABLE INITIALLY IMMEDIATE`.

### `batches`

| Колонка | Тип | NULL | Ограничения |
|---|---|---|---|
| `id` | `bigint` | нет | PK, `GENERATED BY DEFAULT AS IDENTITY` |
| `uuid` | `uuid` | нет | `ux_batches_uuid UNIQUE`, внешний `batchId` |
| `status` | `varchar(100)` | нет | `DEFAULT 'ACCEPTED'`, `ck_batches_status` |
| `measured_at` | `timestamptz` | да | время пакета, если задано |
| `received_at` | `timestamptz` | нет | `DEFAULT CURRENT_TIMESTAMP` |

### `measurements`

| Колонка | Тип | NULL | Ограничения |
|---|---|---|---|
| `id` | `bigint` | нет | PK, `GENERATED BY DEFAULT AS IDENTITY` |
| `uuid` | `uuid` | нет | `ux_measurements_uuid UNIQUE`, внешний `measurementId` |
| `batch_id` | `bigint` | нет | FK → `batches (id)`, `ON DELETE CASCADE` |
| `sensor_id` | `bigint` | нет | FK → `sensors (id)`, `ON DELETE RESTRICT` |
| `measurement_value` | `double precision` | нет | — |
| `measured_at` | `timestamptz` | нет | время измерения на bridge |
| `received_at` | `timestamptz` | нет | `DEFAULT CURRENT_TIMESTAMP`, время сохранения на server |

Разный смысл времени:

- `measured_at` — когда датчик снял значение;
- `received_at` — когда server получил и сохранил данные.

### Индексы

```text
ix_sensors_serial_number             ON sensors (address)
ix_sensors_status                    ON sensors (status)
ix_measurements_sensor_measured_at   ON measurements (sensor_id, measured_at)
ix_measurements_measured_at          ON measurements (measured_at)
ix_measurements_batch                ON measurements (batch_id)
```

`PRIMARY KEY` и `UNIQUE` создают индексы автоматически; перечисленные выше заданы явно.

## Соответствие API, Java-моделей и колонок БД

| Поле API | Поле Java | Колонка БД |
|---|---|---|
| `batchId` | `Batch.uuid` | `batches.uuid` |
| `measurementId` | `Measurement.uuid` | `measurements.uuid` |
| `measurementBatchId` | `Measurement.batchId` | `measurements.batch_id` |
| `sensorId` | `Measurement.sensorId` | `measurements.sensor_id` |
| `measurementValue` | `Measurement.measurementValue` | `measurements.measurement_value` |
| `measuredAt` (batch) | `Batch.measuredAt` | `batches.measured_at` |
| `measuredAt` (measurement) | `Measurement.measurementAt` | `measurements.measured_at` |
| `receivedAt` | `Batch.receivedAt`, `Measurement.receivedAt` | `batches.received_at`, `measurements.received_at` |
| `address` | `Sensor.address` | `sensors.address` |
| `model` | `Sensor.model` | `sensors.model` |
| `serialNumber` | `Sensor.serialNumber` | `sensors.serial_number` |
| `measurementClassId` | `Sensor.MeasurementClassId` | `sensors.measurement_class_id` |
| `locationId` | `Sensor.locationId` | `sensors.location_id` |
| `status` | `Sensor.status` | `sensors.status` |
| `createdAt` | `Sensor.createdAt` | `sensors.created_at` |
| `lastSeenAt` | `Sensor.lastSeenAt` | `sensors.last_seen_at` |
| `typeCode` | `MeasurementClass.typeCode` | `measurement_classes.type_code` |
| `unitOfMeasurement` | `MeasurementClass.unitOfMeasurement` | `measurement_classes.unit_of_measurement` |

Внимание: поле `Measurement.measurementAt` называется иначе, чем колонка и поле API `measuredAt`.
Это нужно привести к одному имени.

## API

Полный контракт: `docs/pisense-api.yaml` (OpenAPI 3.0.3, версия документа 2.0.0).

### Discovery

| Метод | Путь | Назначение |
|---|---|---|
| `POST` | `/api/v1/bridges/{bridgeId}/discovery` | Результат сканирования шин |

### Sensors

| Метод | Путь | Назначение |
|---|---|---|
| `GET` | `/api/v1/sensors` | Список датчиков, фильтры `status`, `measurementClassId`, `locationId`, `address` |
| `GET` | `/api/v1/sensors/{sensorId}` | Датчик с вложенными `measurementClass` и `location` |
| `PATCH` | `/api/v1/sensors/{sensorId}` | Изменение `measurementClassId`, `locationId`, `status` |

Пример ответа:

```json
{
  "id": 42,
  "address": "0x76",
  "model": "BME280",
  "serialNumber": "ABC123",
  "measurementClass": {
    "id": 1,
    "typeCode": "TEMPERATURE",
    "unitOfMeasurement": "°C",
    "description": "Температура"
  },
  "location": {
    "id": 2,
    "name": "Серверная",
    "description": "Стойка 3"
  },
  "status": "ACTIVE",
  "createdAt": "2026-11-18T12:37:54.530Z",
  "lastSeenAt": "2026-11-18T12:37:54.530Z"
}
```

### Справочники

| Метод | Путь | Назначение |
|---|---|---|
| `GET` | `/api/v1/measurement-classes` | Список классов величин |
| `POST` | `/api/v1/measurement-classes` | Создание класса |
| `GET` | `/api/v1/locations` | Список мест размещения |
| `POST` | `/api/v1/locations` | Создание места размещения |

### Measurements

| Метод | Путь | Назначение |
|---|---|---|
| `POST` | `/api/v1/measurement-batches` | Приём batch, `201` при первом приёме, `200` при повторе |
| `GET` | `/api/v1/measurement-batches` | Список пакетов, фильтр `status` |
| `GET` | `/api/v1/measurement-batches/{batchId}` | Пакет по UUID |
| `GET` | `/api/v1/measurements` | История измерений |

Фильтры `GET /api/v1/measurements`:

- `sensorId`;
- `measurementBatchId` (`bigint`, это `measurements.batch_id`, не путать с `batchId`);
- `measuredFrom`;
- `measuredTo`.

## Валидация и ошибки

Server проверяет:

- наличие обязательных полей;
- формат UUID для `batchId` и `measurementId`;
- существование связанных `measurement_classes`, `locations`, `sensors`;
- допустимые значения ENUM;
- корректность времени;
- отсутствие дубликатов `batches.uuid` и `measurements.uuid`;
- числовое значение `measurement_value`.

Формат ошибки:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Описание ошибки"
}
```

Коды ответов:

```text
200  успешное чтение, изменение или повторная обработка batch
201  ресурс создан
400  некорректный запрос
404  ресурс не найден
409  конфликт уникальности
422  бизнес-валидация не пройдена
503  сервис не готов (actuator/health)
```

## Конфигурация

### `pisense-server`

| Свойство | Значение по умолчанию | Смысл |
|---|---|---|
| `server.port` | `9090` | HTTP-порт |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/pisense` | Подключение к PostgreSQL |
| `spring.datasource.username` / `password` | `pisense` / `pisense` | Учётные данные |
| `spring.sql.init.mode` | `always` | Выполнять `schema.sql` при старте |
| `spring.jpa.hibernate.ddl-auto` | `none` | Схему создаёт только SQL-скрипт |
| `management.endpoints.web.exposure.include` | `health` | Наружу открыт только `actuator/health` |

### `pisense-bridge`

| Свойство | Значение по умолчанию | Смысл |
|---|---|---|
| `server.port` | `8080` | HTTP-порт bridge |
| `pisense-server.url` | `http://localhost:9090` | Адрес server |
| `pisense-server.connect-timeout-ms` | `3000` | Таймаут соединения |
| `pisense-server.read-timeout-ms` | `5000` | Таймаут чтения ответа |
| `pisense.poll-interval-ms` | `60000` (в профиле `mock` — `5000`) | Период discovery |
| `pisense.last-measurements-size` | `50` | Размер буфера последних измерений, пока не используется в коде |
| `pisense.bridge-id` | `1` | `bridgeId` в пути discovery |

Профили: `mock` (по умолчанию, `MockBus` и `MockSensor`) и `pi`
(`application-pi.properties`: `pisense.i2c.bus`, `pisense.i2c.devices`, `pisense.spi.bus`,
`pisense.spi.chip-select`, `pisense.w1.path`).

## Docker

### Разработка на x86

```bash
cp .env.example .env
docker compose up --build
```

Поднимаются `pisense-bridge` (8080), `pisense-server` (9090), `pisense-db` (5432, только localhost)
и `swagger-ui` (8081).

### Развёртывание на Raspberry Pi

В `.env` укажите:

```text
COMPOSE_FILE=docker-compose.yml:docker-compose.pi.yml
PISENSE_DATA_DIR=/path/to/data
```

Затем выполните:

```bash
docker compose up -d --build
```

### Сборка ARM64-образа на машине разработчика

```bash
docker run --privileged --rm tonistiigi/binfmt --install arm64
docker buildx build --platform linux/arm64 -f bridge/Dockerfile -t pisense-bridge --load .
docker save pisense-bridge | ssh pi@raspberrypi docker load
```

## Проверка API

Swagger UI: `http://localhost:8081`.
Контракт: `docs/pisense-api.yaml`, монтируется в контейнер `swagger-ui` как `/spec/pisense-api.yaml`.

Состояние сервисов:

```http
GET http://localhost:9090/actuator/health
GET http://localhost:8080/actuator/health
```

## Принципы реализации

- Controller принимает HTTP-запрос и передаёт DTO в service.
- Service содержит бизнес-логику и управляет транзакцией.
- Repository отвечает за доступ к PostgreSQL.
- DTO отделены от JPA-сущностей.
- Bridge работает через абстракции `SensorBus` и `Sensor`.
- Добавление нового драйвера не изменяет код существующих драйверов.
- Ошибка одного датчика не останавливает опрос остальных.
- Уникальные ограничения БД — последняя защита от дубликатов.
- Исторические измерения не изменяются после успешной записи.

Рекомендуемая зависимость слоёв:

```text
controller → service → repository
```

## Известные расхождения

Состояние на момент описания: проект в середине рефакторинга, схема БД уже v2, серверный API ещё не восстановлен.

1. **Серверный API не реализован.** В `server` только модели, контроллеров и репозиториев нет. Работает только отправка discovery из bridge; эндпоинты `sensors`, `measurement-classes`, `locations`, `measurement-batches`, `measurements` описаны в контракте как целевые.
2. **`bridges` и `buses` удалены из схемы.** Поэтому `bridgeId` из пути discovery не проверяется по БД, а `busType` не хранится в `sensors`: определение источника `bridge + bus + address` ослаблено до `address`.
3. **Нет уникальности `sensors`.** Ограничения `UNIQUE (address)` или `UNIQUE (bus_type, address)` в `schema.sql` нет — повторный discovery может создать дубликат. Это главное место, где нужна правка схемы.
4. **Индекс `ix_sensors_serial_number` построен по `sensors(address)`**, а не по `serial_number`. Имя не соответствует содержимому.
5. **`Measurement.measurementAt`** расходится с колонкой `measured_at` и полем API `measuredAt`.
6. **`LocalDateTime` в моделях против `timestamptz` в БД.** Модели не хранят зону, при этом `hibernate.jdbc.time_zone=UTC`; для корректности лучше `OffsetDateTime`/`Instant`.
7. **Реальных драйверов нет.** Каталоги `driver/i2c` и `driver/onewire` удалены, остался только `driver/mock`. `application-pi.properties` при этом ссылается на драйверы и на тип `LIGHT`, которого нет в `SensorType` (есть `ILLUMINANCE`).
8. **`BatchStatus.REJECTED`** по javadoc означает «уже было принято ранее», а не «ошибка валидации». Старое описание «пакет отклонён при валидации» этому не соответствует.
9. **Нет springdoc и Spring Security.** Swagger — статический YAML, авторизация не настроена, `Authorization` не требуется.
10. **Bridge не отправляет измерения.** `Client` умеет только discovery; формирования и отправки batch в коде нет.
