# Этап 1: Сборка JAR
FROM maven:3.9-openjdk-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Собираем JAR
RUN mvn clean package -DskipTests

# Этап 2: Запуск
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Копируем JAR из первого этапа
COPY --from=builder /app/target/KaraokeMusicBot-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]