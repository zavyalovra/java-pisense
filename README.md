# PiSense
Система сбора телеметрии с датчиков, подключённых к Raspberry Pi.

## Стек
`Java 21` · `Spring Boot 3.3.2` · `Spring MVC` · `Spring Data JPA` · `PostgreSQL 16.1` · `H2` · `Pi4J 2.6.0` · `Lombok` · `Maven` · `Docker Compose` · `Spring Boot Actuator` · `Swagger UI`

## Предметная область

* **Bridge - приложение на Raspberry Pi, единица приёма данных.** Планировщик живёт на нём, он периодически сканирует шины
  (`i2c1`, `i2c2`, `spi`, `onewire`) и отправляет на сервер топологию (discovery). Отправка batch с измерениями
  не реализована: замысел описан ниже, в разделе «Идемпотентность», и в схеме БД; по нему `uuid` генерирует bridge,
  поэтому `batches.bridge_id` и идемпотентность по `batches.uuid` ложатся естественно.
* **Измерение относится к каналу и описывается парой `sensorCode` + `channelName`.** У датчика бывает несколько каналов, и у каждого свои
  `metric_type` и `unit`.
* **Топология нормализована, а факты хранятся узкими строками:** `batch_id`, `channel_id`, `measurement_value`, `measured_at`.
  Это нужно для таблицы, которая будет самой большой.
* **server** — Spring Boot приложение. Принимает discovery, хранит топологию в PostgreSQL
  и отдаёт REST API. Приём batch не реализован; замысел описан ниже, в разделе «Идемпотентность», и в схеме БД.
  Swagger UI в docker-compose: <http://localhost:8081>.
* **common** — общие DTO и enum'ы.

| Понятие | Описание |
|---|---|
| Bridge | Устройство на базе Raspberry Pi, единица приёма данных |
| Bus (шина) | Цифровая шина bridge: I2C, SPI, 1-Wire |
| Sensor (датчик) | Устройство на шине, имеет один и более измерительных каналов |
| Channel (канал) | Измеряемая величина датчика: имя, тип метрики, единица измерения |
| Batch | Посылка со всеми измерениями одного цикла опроса bridge, имеет `uuid` |
| Measurement | Одно значение канала в момент времени |

## Схема базы данных

Схема строится вокруг **bridge**. Это корень двух ветвей:

* **Топология:** `bridge → bus → sensor → sensor_channel`.
  Она меняется редко и описывает, что физически подключено.
* **Поток данных:** `bridge → batch → measurement`.
  Она растёт постоянно и содержит основной объём данных.

Ветви сходятся в `sensor_channels`. Измерение ссылается на канал (`measurements.channel_id`), а канал через цепочку
датчик → шина → мост приводит обратно к bridge. По сути это схема «звезда»: узкая таблица фактов `measurements`
и небольшие таблицы-измерения вокруг неё.

По структуре корнем является `bridge`, а по нагрузке и смыслу центр схемы — пара `measurements` + `sensor_channels`.
Большинство запросов выглядит как «история значений канала за период».

## ER-диаграмма

Схема БД v3. Описание в DBML — [`docs/pisense-er.dbml`](docs/pisense-er.dbml) (можно открыть на [dbdiagram.io](https://dbdiagram.io)),
исполняемый скрипт — [`server/src/main/resources/schema.sql`](server/src/main/resources/schema.sql).
<p align="center">
  <img src="docs/pisense_schema.svg" alt="ER-диаграмма PiSense" width="900">
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

## Идемпотентность

Приём batch на сервере ещё не реализован: сейчас bridge отправляет только discovery. Ниже — замысел схемы.

1. Bridge формирует batch и генерирует `batchId` (UUID) **до** отправки. При повторе используется тот же `batchId`.
2. Сервер в одной транзакции вставляет строку в `batches` и все строки в `measurements`.
3. Если `batches.uuid` уже есть (проверка по уникальному индексу, а не `if` в Java, чтобы не было гонки),
   сервер ничего не пишет и сообщает, что batch уже был принят.

По замыслу, если ответ потерялся, bridge безопасно повторяет отправку.

## API

Контракт: [`docs/openapi.yaml`](docs/openapi.yaml) (OpenAPI 3.0). Swagger UI в docker-compose: <http://localhost:8081>.

Ошибки возвращаются в едином формате `ErrorResponse` (`error`, `description`): клиентские — 400, 404, 405 и 415,
внутренние — 500.

Реализовано в `BridgeController`:

* `POST /api/v1/bridges/{bridgeName}/discovery` — 200, `BridgeResponseDto`; создаёт мост, если его ещё нет, 400 при
  нарушении валидации.
* `GET /api/v1/bridges?from=0&size=20` — 200, `PageResponseDto` (`content`, `from`, `size`, `totalElements`,
  `totalPages`); `size` больше 100 усекается до 100, `from` должен быть кратен фактическому размеру страницы, иначе 400.
* `GET /api/v1/bridges/{bridgeName}` — 200, `BridgeResponseDto` с топологией, 404, если моста нет.
* `PATCH /api/v1/bridges/{bridgeName}` — 200, `BridgeShortResponseDto`, 400 при невалидном теле, 404, если моста нет.
* `GET /actuator/health` — 200 при `status: UP`, 503 если приложение не готово; server 9090, bridge 8080.

Приём batch, история измерений, список датчиков и каналов не реализованы и в контракт не входят.

## Запуск

Maven внешний, `mvnw` в проекте нет. Тестов в проекте нет ни в одном модуле.

```bash
mvn -B clean verify          # сборка всех модулей
mvn -B -Pcheck verify        # сборка + checkstyle и SpotBugs
mvn -B -Pcoverage verify     # формально проходит, но тестов нет: jacoco:check пропускается
```

```bash
cp .env.example .env
docker compose up -d --build                      # bridge в режиме mock, server, PostgreSQL, Swagger UI
```

Переменные окружения в `.env` (см. `.env.example`): `COMPOSE_FILE` — набор compose-файлов (`docker-compose.yml` или
`docker-compose.yml:docker-compose.pi.yml`), `BRIDGE_PROFILE` — профиль bridge (по умолчанию `mock`),
`POSTGRES_PASSWORD` — пароль пользователя PostgreSQL `pisense`, `PISENSE_DATA_DIR` — каталог данных PostgreSQL
на хосте (по умолчанию `./data`).

Оверрайд для Raspberry Pi — заготовка на будущее, рабочим запуском не является:

```bash
docker compose -f docker-compose.yml -f docker-compose.pi.yml up -d
```

| Сервис | Порт |
|---|---|
| pisense-bridge | 8080 |
| pisense-server | 9090 |
| PostgreSQL | 127.0.0.1:5432 |
| Swagger UI | 8081 |

Настройки bridge (`bridge/src/main/resources/application.properties`): имя моста `pisense.bridge-name` (идёт в URL запросов),
`pisense.poll-interval-ms`, `pisense-server.url`, `pisense-server.connect-timeout-ms`, `pisense-server.read-timeout-ms`.

Профили bridge: `mock` (по умолчанию, `MockBus`/`MockSensor`) и `pi`. Профиль `pi` только объявлен: реализован единственный
`SensorBus` — `MockBus` под `@Profile("mock")`, контекст Pi4J не создаётся, а `BusPresenceGuard` завершает старт
с `IllegalStateException`.

Таблицы создаются скриптом `server/src/main/resources/schema.sql` при каждом старте (`spring.sql.init.mode=always`,
`spring.jpa.hibernate.ddl-auto=none`); скрипт идемпотентен. Миграций Flyway/Liquibase нет.

## Структура репозитория

```
common/   общие DTO и enum'ы
bridge/   приложение на Raspberry Pi: mock-шины, планировщик discovery, клиент
server/   приём discovery, JPA-сущности, REST API
docs/     openapi.yaml (OpenAPI), pisense-er.dbml (схема БД)
```
