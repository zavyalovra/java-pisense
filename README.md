# java-pisense

Система мониторинга физических величин с датчиков, подключённых к Raspberry Pi.

Проект состоит из двух сервисов:

- `pisense-bridge` — работает на Raspberry Pi, обнаруживает и опрашивает датчики;
- `pisense-server` — работает на ПК с PostgreSQL, принимает и хранит измерения.

## Стек

`Java 8` · `Spring Boot` · `Spring MVC` · `Spring Data JPA` · `Hibernate` · `REST API` · `PostgreSQL` · `SQL` · `Maven` · `Docker` · `JUnit` · `Mockito` · `Postman`

## Архитектура

```text
Физические датчики
        ↓
I2C / SPI / 1-Wire
        ↓
pisense-bridge на Raspberry Pi
        ↓ HTTP JSON
pisense-server
        ↓
PostgreSQL
```

`pisense-bridge` отвечает за работу с физическим оборудованием.
`pisense-server` отвечает за смысл данных, бизнес-конфигурацию и долговременное хранение.

Каждый сервис запускается в собственном Docker-контейнере.

## Бизнес-логика

### 1. Запуск bridge

Bridge не хранит сведения о датчиках в энергонезависимой памяти.
При каждом запуске он заново сканирует доступные шины.

Для каждого найденного устройства bridge получает:

- идентификатор шины, например `i2c1`;
- тип шины: `I2C`, `SPI` или `Onewire`;
- адрес датчика на шине, например `0x76`;
- модель датчика, если она известна;
- серийный номер, если он поддерживается.

Физический источник однозначно определяется комбинацией:

```text
bridge + bus + address
```

Например:

```text
bridge1.i2c1.0x76
```

Один и тот же адрес может существовать на разных шинах, поэтому адрес датчика не является глобальным идентификатором.

### 2. Регистрация обнаруженных устройств

После сканирования bridge передаёт результат на server:

```http
POST /api/v1/bridges/{bridgeId}/discovery
```

Server выполняет идемпотентную синхронизацию:

1. находит bridge;
2. создаёт или обновляет шину;
3. создаёт или обновляет датчик по паре `bus + address`;
4. обновляет модель, серийный номер и `lastSeenAt`;
5. оставляет ранее известные датчики в базе, даже если они временно не обнаружены.

Новый датчик получает статус `DISCOVERED`.
После назначения класса измеряемой величины и места размещения он может перейти в `ACTIVE`.

### 3. Конфигурация датчика

Bridge знает технические характеристики устройства, но не определяет его бизнес-смысл.

Server хранит:

- класс физической величины;
- единицу измерения;
- описание класса;
- место расположения датчика;
- статус датчика.

Например, bridge сообщает, что найден датчик `BME280` на `i2c1` по адресу `0x76`.
Server может назначить ему класс `TEMPERATURE` и место `Серверная`.

Изменение конфигурации выполняется через:

```http
PATCH /api/v1/sensors/{sensorId}
```

Тело запроса содержит ссылки на связанные записи:

```json
{
  "measurementClassId": 1,
  "locationId": 2,
  "status": "ACTIVE"
}
```

В ответе датчика связанные сущности возвращаются вложенными объектами `bus`, `measurementClass` и `location`.

### 4. Опрос датчиков

Bridge по расписанию опрашивает зарегистрированные в памяти датчики.
Опрос выполняется последовательно.

Ошибка одного датчика не должна останавливать цикл опроса остальных устройств.
Для проблемного датчика фиксируется техническая ошибка, а остальные корректные показания продолжают обрабатываться.

Результат одного опроса содержит:

- идентификатор измерения `measurementId`;
- идентификатор датчика `sensorId`;
- числовое значение `measurementValue`;
- время измерения `measuredAt`.

### 5. Формирование batch

Bridge объединяет несколько измерений в один пакет.
Пакет получает UUID `batchId`.

```text
один measurement_batch
        ├── measurement
        ├── measurement
        └── measurement
```

Пакет отправляется на server:

```http
POST /api/v1/measurement-batches
```

Пример запроса:

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
2. проверяет уникальность `batchId`;
3. проверяет существование датчиков;
4. проверяет значения и временные метки;
5. создаёт запись в `measurement_batches`;
6. создаёт связанные записи в `measurements`;
7. фиксирует изменения одной транзакцией.

Batch и его измерения сохраняются атомарно:

```text
либо сохраняются batch и все measurements,
либо не сохраняется ни одна запись.
```

## Идемпотентность

Идемпотентность нужна из-за возможной потери HTTP-ответа.

Сценарий повторной отправки:

1. bridge отправил batch;
2. server сохранил данные;
3. ответ server потерялся из-за сетевой ошибки;
4. bridge повторил отправку того же batch;
5. server обнаружил уже существующий `batchId`;
6. новые дубликаты не создаются.

Для этого используются два уровня идентификаторов:

- `batchId` — уникальный идентификатор пакета;
- `measurementId` — уникальный идентификатор отдельного измерения.

В базе данных действуют ограничения:

```text
measurement_batches.batch_id UNIQUE
measurements.measurement_id UNIQUE
```

Проверка в Java-сервисе не заменяет уникальное ограничение базы данных.
Ограничение в БД защищает от гонки двух одновременных запросов.

## Статусы

### Статус датчика

```text
DISCOVERED                 найден при сканировании, но ещё не настроен
ACTIVE                     разрешён для работы и опроса
TEMPORARILY_UNAVAILABLE    временно не отвечает
DISABLED                   отключён администратором
RETIRED                    выведен из эксплуатации
```

Старые датчики не удаляются, чтобы история измерений не потеряла связь с источником.

### Статус batch

```text
ACCEPTED   пакет успешно принят
REJECTED   пакет отклонён при валидации или обработке
```

При атомарной обработке отклонённый batch не должен содержать частично сохранённые измерения.

## ER-модель

![ER-диаграмма](docs/pisense_schema.svg)

### Основные сущности

```text
bridges
   1
   │
   └── N buses
          1
          │
          └── N sensors
                    │
                    ├── 1 measurement_classes
                    └── 0..1 locations

measurement_batches
          1
          │
          └── N measurements
                    │
                    └── 1 sensors
```

### `bridges`

Физические Raspberry Pi, отправляющие данные.

- `id` — внутренний идентификатор;
- `external_id` — стабильный идентификатор bridge;
- `name` — имя для отображения;
- `active` — признак активности;
- `created_at` — время регистрации.

### `buses`

Физические шины конкретного bridge.

- `bridge_id` — владелец шины;
- `external_id` — например `i2c1` или `spi0`;
- `bus_type` — `I2C`, `SPI` или `Onewire`.

### `measurement_classes`

Справочник физических величин.

- `type_code` — `TEMPERATURE`, `PRESSURE`, `HUMIDITY` или `ILLUMINANCE`;
- `unit_of_measurement` — например `°C`, `%`, `Pa`;
- `description` — описание класса.

### `locations`

Точки пространства, где размещены датчики.

Связь необязательная: датчик может быть обнаружен до назначения места.

### `sensors`

Физические источники измерений.

- `bus_id` и `address` определяют положение датчика на физической шине;
- `(bus_id, address)` — уникальная комбинация;
- `measurement_class_id` задаёт смысл измерения;
- `location_id` задаёт место размещения;
- `last_seen_at` обновляется при обнаружении датчика.

### `measurement_batches`

Заголовки пакетов, отправленных bridge.

- `batch_id` — внешний UUID пакета;
- `status` — результат обработки;
- `measured_at` — время пакета, если оно задано;
- `received_at` — время получения server.

### `measurements`

Исторические факты измерений.

- `measurement_id` — внешний уникальный UUID измерения;
- `measurement_batch_id` — ссылка на пакет;
- `sensor_id` — ссылка на датчик;
- `measurement_value` — значение;
- `measured_at` — время измерения на bridge;
- `received_at` — время сохранения на server.

`measured_at` и `received_at` имеют разный смысл:

- `measured_at` показывает, когда датчик снял значение;
- `received_at` показывает, когда server получил или сохранил данные.

## API

Полный контракт находится в файле:

```text
docs/pisense-api.yaml
```

### Bridges

| Метод | Путь | Назначение |
|---|---|---|
| `POST` | `/api/v1/bridges` | Регистрация bridge |
| `GET` | `/api/v1/bridges` | Список bridge |
| `GET` | `/api/v1/bridges/{bridgeId}` | Получение bridge |
| `PATCH` | `/api/v1/bridges/{bridgeId}` | Изменение bridge |
| `POST` | `/api/v1/bridges/{bridgeId}/discovery` | Передача результата обнаружения |

### Справочники

| Метод | Путь | Назначение |
|---|---|---|
| `GET` | `/api/v1/buses` | Список шин |
| `GET` | `/api/v1/measurement-classes` | Список классов величин |
| `POST` | `/api/v1/measurement-classes` | Создание класса величины |
| `GET` | `/api/v1/locations` | Список мест размещения |
| `POST` | `/api/v1/locations` | Создание места размещения |

### Sensors

| Метод | Путь | Назначение |
|---|---|---|
| `GET` | `/api/v1/sensors` | Список датчиков с фильтрами |
| `GET` | `/api/v1/sensors/{sensorId}` | Датчик со вложенными `bus`, `measurementClass`, `location` |
| `PATCH` | `/api/v1/sensors/{sensorId}` | Изменение класса, места и статуса |

Пример ответа датчика:

```json
{
  "id": 42,
  "bus": {
    "id": 3,
    "bridgeId": 1,
    "externalId": "i2c1",
    "busType": "I2C"
  },
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

### Measurements

| Метод | Путь | Назначение |
|---|---|---|
| `POST` | `/api/v1/measurement-batches` | Приём batch измерений |
| `GET` | `/api/v1/measurement-batches` | Список batch |
| `GET` | `/api/v1/measurement-batches/{batchId}` | Получение batch |
| `GET` | `/api/v1/measurements` | История измерений с фильтрами |

Фильтры `GET /api/v1/measurements`:

- `sensorId`;
- `measurementBatchId`;
- `measuredFrom`;
- `measuredTo`.

## Валидация

Server проверяет:

- наличие обязательных полей;
- формат UUID для `batchId` и `measurementId`;
- существование связанных bridge, bus, sensor, location и measurement class;
- допустимые значения enum;
- корректность времени;
- отсутствие дубликатов `batchId` и `measurementId`;
- числовое значение измерения.

Ошибки возвращаются в формате:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Описание ошибки"
}
```

Основные HTTP-коды:

```text
200  успешное чтение, обновление или повторная обработка batch
201  ресурс создан
400  некорректный запрос
404  ресурс не найден
409  конфликт уникальности
422  бизнес-валидация не пройдена
```

## Принципы реализации

- Controller принимает HTTP-запрос и передаёт DTO в service.
- Service содержит бизнес-логику и управляет транзакцией.
- Repository отвечает за доступ к PostgreSQL.
- DTO отделены от JPA-сущностей.
- Bridge работает через абстракции шин и драйверов датчиков.
- Добавление нового драйвера не должно изменять код существующих драйверов.
- Ошибка одного датчика не должна останавливать опрос остальных.
- Уникальные ограничения БД являются последней защитой от дубликатов.
- Исторические измерения не изменяются после успешной записи.

Рекомендуемая зависимость слоёв:

```text
controller → service → repository
```

## Docker

### Разработка на x86

```bash
cp .env.example .env
docker compose up --build
```

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

Swagger-контракт можно импортировать в Swagger Editor, Postman или IntelliJ IDEA:

```text
docs/pisense-api.yaml
```

Проверка состояния сервисов выполняется через Spring Boot Actuator:

```http
GET /actuator/health
```
