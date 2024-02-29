# syntax=docker/dockerfile:1
# Build stage
FROM gradle:8.6-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle applications:data-collector:buildFatJar --no-daemon
# Run app stage
FROM eclipse-temurin:17-jre-alpine
EXPOSE 8081:8081
ENV OS_ENV=container
RUN mkdir /app
COPY --from=build /home/gradle/src/applications/data-collector/build/libs/data-collector-all.jar /app/datacollector.jar
ENTRYPOINT ["java","-jar","/app/datacollector.jar"]