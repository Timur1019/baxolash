# Backend (Spring Boot) — stateless, порт 8080, конфиг через env
# Для Render: Web Service (Docker). Переменные: SPRING_DATASOURCE_*, S3_*, APP_CORS_ALLOWED_ORIGINS
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

COPY . .
RUN ./gradlew bootJar --no-daemon -x test && \
    cp /app/build/libs/*.jar /app/app.jar

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN adduser -D -u 1000 appuser
COPY --from=builder /app/app.jar app.jar
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
