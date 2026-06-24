# syntax=docker/dockerfile:1.7

FROM gradle:jdk21 AS builder

WORKDIR /home/gradle/project

COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle

RUN --mount=type=cache,target=/home/gradle/.gradle \
	sh ./gradlew --no-daemon dependencies

COPY src ./src

RUN --mount=type=cache,target=/home/gradle/.gradle \
	sh ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
