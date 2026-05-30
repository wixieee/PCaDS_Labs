FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

COPY . .

RUN --mount=type=cache,target=/root/.m2 mvn clean package -pl billing-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/billing-service/target/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]