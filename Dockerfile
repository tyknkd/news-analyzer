# syntax=docker/dockerfile:1
# Build stage
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon
# Run app stage
FROM eclipse-temurin:17-jre-alpine
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/newsanalyzer.jar
ENTRYPOINT ["java","-jar","/app/newsanalyzer.jar"]