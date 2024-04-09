# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jre-jammy AS jammy-jre-spark
ARG SPARK_VERSION=3.3.2
ARG HADOOP_MAJOR_VERSION=3
ARG SCALA_VERSION=2.13
ENV SPARK_RELEASE="spark-${SPARK_VERSION}-bin-hadoop${HADOOP_MAJOR_VERSION}-scala${SCALA_VERSION}"
ENV SPARK_HOME="/opt/spark"
RUN curl https://archive.apache.org/dist/spark/spark-${SPARK_VERSION}/${SPARK_RELEASE}.tgz -o spark.tgz && \
    tar -xf spark.tgz && \
    mv ${SPARK_RELEASE} ${SPARK_HOME} && \
    rm spark.tgz
ENV PATH="${PATH}:${SPARK_HOME}/bin:${SPARK_HOME}/sbin"

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
ENTRYPOINT ["app-entrypoint.sh"]

FROM jammy-jre-spark AS spark-app
ARG APP
ENV APP=${APP}
ENV OS_ENV="container"
RUN mkdir -p /app
COPY --from=gradle-build /home/gradle/src/applications/${APP}/build/libs/*-all.jar /app/${APP}.jar
ADD --chmod=775 ./app-entrypoint.sh /
ENTRYPOINT ["app-entrypoint.sh"]