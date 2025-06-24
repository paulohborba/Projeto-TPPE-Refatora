FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle/
COPY build.gradle .
COPY settings.gradle .

COPY config/checkstyle/google_checks.xml config/checkstyle/  # Exemplo: se seu arquivo está em config/checkstyle/

COPY src src/

RUN ./gradlew clean build bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]