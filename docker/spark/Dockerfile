# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jre-jammy AS spark-jammy
ARG SPARK_VERSION=3.3.2
ARG HADOOP_MAJOR_VERSION=3
ARG SCALA_VERSION=2.13
ENV SPARK_RELEASE="spark-${SPARK_VERSION}-bin-hadoop${HADOOP_MAJOR_VERSION}-scala${SCALA_VERSION}"
ENV SPARK_HOME="/opt/spark"
RUN curl https://archive.apache.org/dist/spark/spark-${SPARK_VERSION}/${SPARK_RELEASE}.tgz -o spark.tgz && \
    tar -xf spark.tgz && \
    mv ${SPARK_RELEASE} ${SPARK_HOME} && \
    rm spark.tgz
ENV PATH="${SPARK_HOME}/bin:${SPARK_HOME}/sbin:${PATH}"
CMD ["bash"]

FROM gradle:8.7-jdk17-jammy AS spark-gradle
ARG SPARK_VERSION=3.3.2
ARG HADOOP_MAJOR_VERSION=3
ARG SCALA_VERSION=2.13
ENV SPARK_RELEASE="spark-${SPARK_VERSION}-bin-hadoop${HADOOP_MAJOR_VERSION}-scala${SCALA_VERSION}"
ENV SPARK_HOME="/opt/spark"
RUN curl https://archive.apache.org/dist/spark/spark-${SPARK_VERSION}/${SPARK_RELEASE}.tgz -o spark.tgz && \
    tar -xf spark.tgz && \
    mv ${SPARK_RELEASE} ${SPARK_HOME} && \
    rm spark.tgz
ENV PATH="${SPARK_HOME}/bin:${SPARK_HOME}/sbin:${PATH}"
CMD ["bash"]