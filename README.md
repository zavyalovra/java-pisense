# PiSense

Система сбора телеметрии с датчиков, подключённых к Raspberry Pi.

* **bridge** — приложение на Raspberry Pi. Планировщик через заданные промежутки времени опрашивает все шины
  (`i2c1`, `i2c2`, `spi`, `onewire` и другие) и датчики на них, собирает все измерения в один **batch** и отправляет на сервер.
* **server** — Spring Boot приложение. Принимает batch от каждого bridge и сохраняет в PostgreSQL.
  Каждый batch имеет `uuid`, поэтому повторная отправка идемпотентна.
* **common** — общие DTO и enum'ы (`BusType`, `MetricType`, `DeviceStatus`, `BatchStatus`).

## Предметная область

| Понятие | Описание |
|---|---|
| Bridge | Устройство на базе Raspberry Pi, единица приёма данных |
| Bus (шина) | Цифровая шина bridge: I2C, SPI, 1-Wire |
| Sensor (датчик) | Устройство на шине, имеет один и более измерительных каналов |
| Channel (канал) | Измеряемая величина датчика: имя, тип метрики, единица измерения |
| Batch | Посылка со всеми измерениями одного цикла опроса bridge, имеет `uuid` |
| Measurement | Одно значение канала в момент времени |

Измерение, как оно выглядит на bridge (`TelemetryDto`):

```java
public record TelemetryDto(
    String sensorCode,        // i2c1-0x76
    String channelName,       // temperature
    MetricType metricType,    // TEMPERATURE
    double measurementValue,  // 23.7
    String unit               // Cel
) {}
```

## Вокруг чего построена схема

Схема строится вокруг **bridge**. Это корень двух ветвей:

* **Топология (справочная часть):** `bridge → bus → sensor → sensor_channel`.
  Она меняется редко и описывает, что физически подключено.
* **Поток данных (фактовая часть):** `bridge → batch → measurement`.
  Она растёт постоянно и содержит основной объём данных.

Ветви сходятся в `sensor_channels`. Измерение ссылается на канал (`measurements.channel_id`), а канал через цепочку
датчик → шина → мост приводит обратно к bridge. По сути это схема «звезда»: узкая таблица фактов `measurements`
и небольшие таблицы-измерения вокруг неё.

Так что по структуре корнем является `bridge`, а по нагрузке и смыслу центр схемы — пара `measurements` + `sensor_channels`.
Большинство запросов выглядит как «история значений канала за период».

### Предметная область

* **Bridge остаётся единицей приёма данных.** Планировщик живёт на нём, он отправляет batch и генерирует `uuid`.
  Поэтому `batches.bridge_id` и идемпотентность по `batches.uuid` ложатся естественно.
* **Канал, а не датчик, служит точкой привязки измерений.** У датчика бывает несколько каналов, и у каждого свои
  `metric_type` и `unit`. Привязка к датчику потребовала бы дополнительного поля с именем канала в каждой строке.
* **Топология нормализована, а факты хранятся узкими строками:** `batch_id`, `channel_id`, `measurement_value`, `measured_at`.
  Это нужно для таблицы, которая будет самой большой.

## ER-диаграмма

Схема БД v3. Исполняемый скрипт — [`server/src/main/resources/schema.sql`](server/src/main/resources/schema.sql), описание в DBML — [`docs/pisense-er.dbml`](docs/pisense-er.dbml) (можно открыть на [dbdiagram.io](https://dbdiagram.io)).
<p align="center">
  <img src="docs/pisense_schema.svg" alt="ER-диаграмма Filmorate" width="900">
</p>

## Таблицы

| Таблица | Назначение | Уникальность |
|---|---|---|
| `bridges` | Мосты. `name` приходит в URL запросов | `name` |
| `buses` | Шины моста (`i2c1`, `i2c2`, `spi0`, `onewire`) | `(bridge_id, name)` |
| `sensors` | Датчики на шине. `sensor_code` формирует bridge: `<шина>-<адрес>`, например `i2c1-0x76` | `(bus_id, sensor_code)` |
| `sensor_channels` | Каналы датчика: `name`, `metric_type`, `unit` | `(sensor_id, name)` |
| `batches` | Посылки. `uuid` генерирует bridge | `uuid` |
| `measurements` | Измерения: `batch_id`, `channel_id`, `measurement_value`, `measured_at` | `(batch_id, channel_id)` |

Замечания по v3:

* Источник истины для колонок — `schema.sql` (его выполняет сервер при старте). В нём у `buses` нет колонки `type`,
  а `metric_type` и `unit` допускают `NULL`. В `pisense-er.dbml` всё наоборот: есть `buses.type`, а `metric_type` и `unit` — `not null`;
  значение там названо `value`, а не `measurement_value`. Файлы стоит синхронизировать. API построен по `schema.sql`:
  у шины только `name`, а `metricType` и `unit` в контракте обязательны.
* Колонка значения называется `measurement_value`, в API это поле `measurementValue` (как в `TelemetryDto`).
* `batches.status` в БД не хранится: `ACCEPTED`/`REJECTED` — это результат приёма в HTTP-ответе (дубликат в БД не пишется).
* `metric_type` и `unit` канала после создания не меняются, чтобы история не меняла смысл.

### Индексы под основные запросы

* `ux_batches_uuid` (из `unique`) — идемпотентность приёма.
* `ix_measurements_channel_measured_at` — «история канала за период», главный запрос на чтение.
* `ux_measurements_batch_channel` — один канал не может встретиться в batch дважды; индекс также покрывает выборку измерений batch.
* `ix_batches_bridge_collected_at` — список batch моста по времени.

## Идемпотентность

1. Bridge формирует batch и генерирует `batchId` (UUID) **до** отправки. При повторе используется тот же `batchId`.
2. Сервер в одной транзакции вставляет строку в `batches` и все строки в `measurements`.
3. Если `batches.uuid` уже есть (проверка по уникальному индексу, а не `if` в Java, чтобы не было гонки), сервер ничего не пишет и возвращает `200` и `status: REJECTED`.
   Первая успешная обработка возвращает `201` и `status: ACCEPTED`.

Если ответ потерялся, bridge безопасно повторяет отправку.

## Работа из Spring Boot / Hibernate

* Ключи — `bigint`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
* Enum'ы — строки: `@Enumerated(EnumType.STRING)`; нативные enum PostgreSQL не используются.
* Время — `timestamptz` ↔ `java.time.Instant`, `hibernate.jdbc.time_zone=UTC`.
* `uuid` ↔ `java.util.UUID`.
* Связи — `@ManyToOne(fetch = FetchType.LAZY)` со стороны «многие». `@OneToMany` у `Bridge → Batch` и `SensorChannel → Measurement` не нужен:
  коллекции измерений огромны, читайте их репозиторными запросами.

```java
@Entity
@Table(name = "measurements")
public class Measurement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id")
    private SensorChannel channel;

    @Column(name = "measurement_value", nullable = false)
    private double measurementValue;

    @Column(name = "measured_at", nullable = false)
    private Instant measuredAt;
}
```

```java
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    Page<Measurement> findByChannelIdAndMeasuredAtBetweenOrderByMeasuredAtAsc(
            Long channelId, Instant from, Instant to, Pageable pageable);
}
```

Для массовой вставки включите батчинг JDBC (`spring.jpa.properties.hibernate.jdbc.batch_size=100`).
Учтите, что с `IDENTITY` Hibernate не батчит вставки: при росте нагрузки перейдите на `SEQUENCE` с `allocationSize`.

## API

Контракт: [`docs/pisense-api.yaml`](docs/pisense-api.yaml) (OpenAPI 3.0). Swagger UI в docker-compose: <http://localhost:8081>.

| Метод | Путь | Назначение |
|---|---|---|
| `GET` | `/api/v1/bridges` | Список мостов |
| `GET` | `/api/v1/bridges/{bridgeName}` | Мост с топологией (шины, датчики, каналы) |
| `PATCH` | `/api/v1/bridges/{bridgeName}` | Изменить статус моста |
| `POST` | `/api/v1/bridges/{bridgeName}/discovery` | bridge отправляет топологию |
| `POST` | `/api/v1/bridges/{bridgeName}/batches` | Принять batch (идемпотентно): `201` / `200` |
| `GET` | `/api/v1/bridges/{bridgeName}/batches` | Список batch моста |
| `GET` | `/api/v1/batches/{batchId}` | Batch по UUID с измерениями |
| `GET` | `/api/v1/sensors` | Список датчиков (фильтры `bridgeName`, `busName`) |
| `GET`, `PATCH` | `/api/v1/sensors/{sensorId}` | Датчик, смена `location` |
| `GET` | `/api/v1/channels/{channelId}` | Канал |
| `GET` | `/api/v1/channels/{channelId}/measurements` | История значений канала за период (`from`, `to`, `page`, `size`) |
| `GET` | `/actuator/health` | Проверка состояния |

Коды ошибок: `400` — неверный запрос, `403` — мост `DISABLED`, `404` — не найдено, `409` — конфликт `metricType`/`unit` канала при discovery,
`422` — неизвестный канал, несовпадение `metricType`/`unit`, дубль канала в batch.

### Порядок работы bridge

1. `POST /api/v1/bridges/pi-kitchen/discovery` — при старте и периодически; мост и топология создаются автоматически.
2. `POST /api/v1/bridges/pi-kitchen/batches` — каждый цикл опроса. Неизвестный канал даёт `422`, поэтому discovery всегда идёт первым.

Пример batch:

```bash
curl -X POST http://localhost:9090/api/v1/bridges/pi-kitchen/batches \
  -H 'Content-Type: application/json' \
  -d '{
    "batchId": "550e8400-e29b-41d4-a716-446655440000",
    "collectedAt": "2026-11-22T12:37:54.530Z",
    "measurements": [
      {"sensorCode": "i2c1-0x76", "channelName": "temperature", "metricType": "TEMPERATURE",
       "unit": "Cel", "measurementValue": 23.7, "measuredAt": "2026-11-22T12:37:54.100Z"}
    ]
  }'
```

История канала за сутки:

```bash
curl 'http://localhost:9090/api/v1/channels/1/measurements?from=2026-11-21T00:00:00Z&to=2026-11-22T00:00:00Z&size=500'
```

## Запуск

```bash
cp .env.example .env
docker compose up -d --build                      # bridge в режиме mock, server, PostgreSQL, Swagger UI
docker compose -f docker-compose.yml -f docker-compose.pi.yml up -d   # на Raspberry Pi с реальными шинами
```

| Сервис | Порт |
|---|---|
| pisense-bridge | 8080 |
| pisense-server | 9090 |
| PostgreSQL | 127.0.0.1:5432 |
| Swagger UI | 8081 |

Настройки bridge (`bridge/src/main/resources/application.properties`): имя моста (`bridges.name`, идёт в URL запросов),
`pisense.poll-interval-ms`, `pisense-server.url`.

Таблицы создаются скриптом `server/src/main/resources/schema.sql` (`spring.sql.init.mode=always`); скрипт идемпотентен.

## Структура репозитория

```
common/   общие DTO и enum'ы
bridge/   приложение на Raspberry Pi: драйверы шин, планировщик, клиент
server/   приём batch, JPA-сущности, REST API
docs/     pisense-api.yaml (OpenAPI), pisense-er.dbml (схема БД)
```
