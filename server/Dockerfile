# syntax=docker/dockerfile:1.7

# ---------- Стадия сборки ----------
# Нативная сборка на машине разработчика, jar переносимый
FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Родительский pom и конфиги (контекст сборки = корень репозитория)
COPY pom.xml lombok.config checkstyle.xml suppressions.xml ./
COPY server/pom.xml server/pom.xml
COPY bridge/pom.xml bridge/pom.xml
COPY server/src server/src

# Кэш ~/.m2 между сборками
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -pl server -am package -DskipTests

# ---------- Стадия запуска ----------
FROM eclipse-temurin:21-jre-jammy

# wget нужен для healthcheck в docker-compose
RUN apt-get update && \
    apt-get install -y --no-install-recommends wget && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /build/server/target/pisense-server-*.jar /app/app.jar

# Переопределяется через JAVA_OPTS в compose
ENV JAVA_OPTS="-XX:+UseSerialGC -Xms64m -Xmx256m"

EXPOSE 9090

# exec: корректная остановка контейнера по SIGTERM
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]