# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/tire_change-1.0.jar backend.jar
COPY src/resources/application.yml /config/application.yml

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/backend.jar"]
