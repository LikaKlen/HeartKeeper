# Используем официальный образ OpenJDK 17 с Gradle
FROM gradle:8.4-jdk21 AS builder

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем файлы проекта в контейнер
COPY . .

# Собираем приложение
RUN gradle clean build -x test

# Используем минимальный JRE-образ для финального контейнера
FROM eclipse-temurin:21-jre

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR из предыдущего этапа
COPY --from=builder /app/build/libs/*.jar app.jar

# Устанавливаем переменные окружения
ENV SPRING_APPLICATION_NAME=HeartKeeper
ENV SERVER_PORT=8080

# Открываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
