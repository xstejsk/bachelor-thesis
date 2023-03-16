# Build Stage
FROM maven:3.8.4-openjdk-17 AS build
COPY . /build
WORKDIR /build
RUN mvn clean package -DskipTests

# Run Stage
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=production", "app.jar"]