FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

COPY . .

RUN --mount=type=cache,target=/root/.m2 mvn clean package -pl notification-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/notification-service/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]