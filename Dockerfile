# Используем легковесный образ с OpenJDK 17 (подходит для Java 11+)
FROM openjdk:17-jre-slim

# Автор
LABEL maintainer="sergeyzhul"

# Рабочая директория в контейнере
WORKDIR /app

# Копируем JAR-файл из папки target
COPY target/KaraokeMusicBot-1.0-SNAPSHOT.jar app.jar

# Открываем порт (не обязателен для бота, но хорошая практика)
EXPOSE 8080

# Команда запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]