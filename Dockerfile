# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/tire_change-V1.0.jar backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/backend.jar"]
