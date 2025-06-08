
FROM gradle:8-jdk17 AS build

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper gradle/wrapper

COPY build.gradle .
COPY settings.gradle .
COPY src ./src

RUN ./gradlew clean build

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/build/libs/demo-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

