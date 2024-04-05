# syntax=docker/dockerfile:1
# Build stage
FROM gradle:8.6-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
ARG APP
RUN gradle applications:${APP}:buildFatJar --no-daemon
# Run app stage
FROM eclipse-temurin:17-jre-alpine AS app
ARG APP
ENV APP=${APP}
ENV OS_ENV="container"
RUN mkdir -p /app
COPY --from=build /home/gradle/src/applications/${APP}/build/libs/*-all.jar /app/${APP}.jar
ADD --chmod=775 ./docker-entrypoint.sh /
ENTRYPOINT ["/docker-entrypoint.sh"]