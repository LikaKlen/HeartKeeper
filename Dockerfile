FROM openjdk:21-jdk-slim
WORKDIR /app

# Копируем скомпилированный JAR-файл из стадии build
COPY build/libs/HeattKeeper-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт для приложения
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]