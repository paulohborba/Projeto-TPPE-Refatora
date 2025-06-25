FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

COPY gradlew* ./
COPY gradle/ gradle/

RUN chmod +x ./gradlew

COPY settings.gradle.kts ./
COPY backend/build.gradle ./
COPY config/checkstyle/checkstyle.xml config/checkstyle/
COPY backend/src/ src/

RUN ./gradlew clean build bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]