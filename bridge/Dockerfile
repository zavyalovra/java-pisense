# syntax=docker/dockerfile:1.7

# ---------- Стадия сборки ----------
# $BUILDPLATFORM: Maven всегда работает нативно на машине сборки (x86 или ARM).
# Jar переносим между архитектурами, поэтому эмуляция QEMU для сборки не нужна.
FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Родительский pom и конфиги нужны Maven для разрешения parent (контекст = корень репо)
# pom соседнего модуля нужен, потому что родитель перечисляет его в <modules>
COPY pom.xml lombok.config checkstyle.xml suppressions.xml ./
COPY bridge/pom.xml bridge/pom.xml
COPY server/pom.xml server/pom.xml
COPY bridge/src bridge/src

# Кэш ~/.m2 между сборками: зависимости не скачиваются повторно
# -pl bridge -am: собираем только bridge и его родителя
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -pl bridge -am package -DskipTests

# ---------- Стадия запуска ----------
# Образ мультиархитектурный: amd64 на ноутбуке, arm64 на Pi.
FROM eclipse-temurin:21-jre-jammy

# wget нужен для healthcheck
RUN apt-get update && \
    apt-get install -y --no-install-recommends wget && \
    rm -rf /var/lib/apt/lists/* && \
    mkdir -p /var/log/pi4j

WORKDIR /app
COPY --from=build /build/bridge/target/pisense-bridge-*.jar /app/app.jar

# Лёгкие настройки JVM для слабого ARM; переопределяются через JAVA_OPTS в compose
ENV JAVA_OPTS="-XX:+UseSerialGC -Xms32m -Xmx96m -XX:MaxMetaspaceSize=64m"

EXPOSE 8080

# exec: JVM становится PID 1 и корректно получает SIGTERM
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]