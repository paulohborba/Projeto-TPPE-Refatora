FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY settings.gradle.kts .
COPY backend/build.gradle .
COPY config/checkstyle/checkstyle.xml config/checkstyle/

RUN chmod +x ./gradlew

ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.unsafe.testoutputs.enabled=true -Dgradle.wrapper.timeout.ms=180000"

RUN echo "systemProp.org.gradle.internal.http.connectionTimeout=180000" >> gradle/wrapper/gradle-wrapper.properties && \
    echo "systemProp.org.gradle.internal.http.socketTimeout=180000" >> gradle/wrapper/gradle-wrapper.properties && \
    echo "systemProp.org.gradle.internal.http.readTimeout=180000" >> gradle/wrapper/gradle-wrapper.properties && \
    echo "systemProp.org.gradle.internal.http.maxRetries=5" >> gradle/wrapper/gradle-wrapper.properties

RUN ./gradlew dependencies --no-daemon

COPY backend/src/ src/ 

RUN ./gradlew clean backend:build backend:bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /app/backend/build/libs/*.jar app.jar 

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]