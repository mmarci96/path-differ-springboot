FROM docker.io/gradle:8-jdk17 AS build

WORKDIR /app

COPY gradlew .
COPY gradle/wrapper gradle/wrapper

COPY build.gradle .
COPY settings.gradle .
COPY src ./src

RUN ./gradlew clean build javadoc

RUN mkdir -p /javadoc && cp -r ./build/docs/javadoc /javadoc

FROM docker.io/library/eclipse-temurin:17-jdk-alpine

RUN addgroup -S spring && adduser -S -G spring spring

WORKDIR /app

COPY --from=build /app/build/libs/demo-*.jar app.jar
COPY --from=build /javadoc /app/static/doc
RUN chown -R spring:spring /app

USER spring

ENTRYPOINT ["java", "-jar", "app.jar"]

