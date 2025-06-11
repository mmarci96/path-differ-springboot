FROM docker.io/gradle:8-jdk17 AS build

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper gradle/wrapper

COPY build.gradle .
COPY settings.gradle .
COPY src ./src

RUN ./gradlew clean build
RUN ./gradlew javadoc
COPY ./build/docs/ ./src/main/resources/static/doc

FROM docker.io/library/eclipse-temurin:17-jdk-alpine

RUN addgroup -S spring && adduser -S -G spring spring

WORKDIR /app

COPY --from=build /app/build/libs/demo-*.jar app.jar

RUN chown -R spring:spring /app

USER spring

ENTRYPOINT ["java", "-jar", "app.jar"]

