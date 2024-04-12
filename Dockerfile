# syntax=docker/dockerfile:1
FROM gradle:8.7-jdk17 AS gradle-build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
ARG APP
RUN gradle applications:${APP}:buildFatJar --no-daemon

FROM eclipse-temurin:17-jre-jammy AS app
ARG APP
ENV APP=${APP}
ENV OS_ENV="container"
RUN mkdir -p /app
COPY --from=gradle-build /home/gradle/src/applications/${APP}/build/libs/*-all.jar /app/${APP}.jar
ADD --chmod=775 ./app-entrypoint.sh /
ENTRYPOINT ["/app-entrypoint.sh"]

FROM tyknkd/spark:3.3.2-scala2.13-java17-jammy AS spark-app
ARG APP
ENV APP=${APP}
ENV OS_ENV="container"
RUN mkdir -p /app
COPY --from=gradle-build /home/gradle/src/applications/${APP}/build/libs/*-all.jar /app/${APP}.jar
ADD --chmod=775 ./app-entrypoint.sh /
ENTRYPOINT ["/app-entrypoint.sh"]