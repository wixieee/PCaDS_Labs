FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

COPY . .

RUN --mount=type=cache,target=/root/.m2 mvn clean package -pl audit-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/audit-service/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]