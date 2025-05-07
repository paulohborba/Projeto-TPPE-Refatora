FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/pom.xml .

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

CMD ["sh", "-c", "mvn test -f pom.xml -Dmaven.test.failure.ignore=true && java -jar app.jar"]