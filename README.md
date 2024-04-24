# Tech Industry News Analyzer App
**Tyler Kinkade**

[https://github.com/tyknkd/news-analyzer](https://github.com/tyknkd/news-analyzer)

_CSCA 5028: Applications of Software Architecture for Big Data, University of Colorado Boulder_

## Overview
This project applies big data software architecture principles and machine learning techniques to analyze recent tech 
industry news articles, automatically extract common themes, and sort them into groups by topic. The primary aim is to 
help readers quickly identify current trends in media reporting on the tech industry and focus on the topics of most 
interest to the reader.

## Tech Stack
- [Kotlin](https://kotlinlang.org/) v.1.9.22: Programming language
- [Ktor](https://ktor.io/) v.2.3.8: Kotlin application framework
- [Netty](https://netty.io/) v.4.1.106: Web server
- [Apache Freemarker](https://freemarker.apache.org/) v.2.3.32: Dynamic webpage templating
- [PostgreSQL](https://www.postgresql.org/) v.16.2: Relational database
- [Exposed](https://github.com/JetBrains/Exposed) v.0.48.0: Object relational mapping framework
- [HikariCP](https://github.com/brettwooldridge/HikariCP) v.5.1.0: Database connection pooling
- [Junit](https://junit.org/junit5/) v.4.13.2: Testing
- [Kover](https://kotlin.github.io/kotlinx-kover/gradle-plugin/) v.0.7.5: Test code coverage measurement
- [Prometheus](https://prometheus.io/) v.1.6.3: Performance metrics and monitoring
- [Docker Engine](https://www.docker.com/) v.25.0.3: Containerization
- [Apache Spark](https://spark.apache.org/) v.3.3.2: Data analytics
- [Kotlin for Apache Spark](https://github.com/Kotlin/kotlin-spark-api) v.1.2.4: Kotlin-Spark compatibility API
- [RabbitMQ](https://www.rabbitmq.com) v.5.21.0: Messaging broker
- [Java Virtual Machine](https://openjdk.org/) v.17.0.10: Compilation and libraries
- [Gradle](https://gradle.org/) v.8.7: Build tool
- [Ubuntu](https://ubuntu.com/) v.22.4.4: Operating system


## Final Project Rubric
(Click links to see relevant code)
- C-level
  - [x] Web application: [applications/web-server](https://github.com/tyknkd/news-analyzer/tree/main/applications/web-server)
  - [x] Data collection: [applications/data-collector](https://github.com/tyknkd/news-analyzer/tree/main/applications/data-collector)
  - [x] Data analyzer: [applications/data-analyzer](https://github.com/tyknkd/news-analyzer/tree/main/applications/data-analyzer)
  - [x] Unit tests: [web-server/src/test](https://github.com/tyknkd/news-analyzer/tree/main/applications/web-server/src/test/), [data-collector/src/test](https://github.com/tyknkd/news-analyzer/tree/main/applications/data-collector/src/test/), [data-analyzer/src/test](https://github.com/tyknkd/news-analyzer/tree/main/applications/data-analyzer/src/test/) 
  - [x] Data persistence: [components/data-support](https://github.com/tyknkd/news-analyzer/tree/main/components/data-support)
  - [x] REST API endpoint: [web-server/src/main/kotlin/io/newsanalyzer/webserver/plugins/Routing.kt](https://github.com/tyknkd/news-analyzer/blob/main/applications/web-server/src/main/kotlin/io/newsanalyzer/webserver/plugins/Routing.kt)
  - Production environment
- B-level
  - [x] Integration tests: [web-server/src/test](https://github.com/tyknkd/news-analyzer/tree/main/applications/web-server/src/test/), [data-collector/src/test](https://github.com/tyknkd/news-analyzer/tree/main/applications/data-collector/src/test/), [data-analyzer/src/test](https://github.com/tyknkd/news-analyzer/tree/main/applications/data-analyzer/src/test/)
  - [x] Test doubles: [components/test-support](https://github.com/tyknkd/news-analyzer/tree/main/components/test-support)
  - Continuous integration
  - Production monitoring
      - `/health` endpoint
      - `/metrics` endpoint
- A-level
  - [x] Event collaboration messaging
  - Continuous delivery

## Live Production Deployment

## API
The REST API entry point is `/api`. [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS) principles are applied in order to facilitate hypermedia-driven 
discovery of the endpoints within the API.

## Local Setup
To run the app locally, you can either (A) run the app fully containerized on a local machine or (B) run each server 
(web, data collector, data analyzer) from separate terminals with only the database and message queue in Docker containers.
Either way, you must perform the preliminary environment setup first. (The following commands are for a Linux/Unix environment.)

### Preliminary Environment Setup
1. Install [Docker Engine](https://www.docker.com/).
2. In a bash shell, clone the git repository and change to the project directory.
```shell
git clone https://github.com/tyknkd/news-analyzer.git
cd news-analyzer
```
3. Run the following bash commands to create a secrets file for the PostgreSQL database password (changing the `yourpasswordgoeshere` string as desired).
```shell
mkdir secrets && echo yourpasswordgoeshere > secrets/postgres_password.txt
```
4. Obtain an API key from [https://newsapi.org](https://newsapi.org) and save it to a separate secrets file with the following bash command, replacing the `yournewsapikeygoeshere` string with your newly obtained key. (NB: You can run the tests with a fake key, but an exception will be thrown if you attempt to run the app locally without a valid key.)
```shell
echo yournewsapikeygoeshere > secrets/news_api_key.txt
```

### A. Fully Containerized Setup
1. Perform preliminary environment setup above.
2. Optional: Run all tests in containers. (This will take several minutes.)
```shell
docker compose up test
```
3. Build and start the Docker containers. (This will take several minutes the first time.)
```shell
docker compose up
```
4. In a web browser, open [http://localhost:8888](http://localhost:8080)
5. To stop the app, press `CTRL+C` in the bash shell from which it was started.

### B. Local App Servers Setup
1. Perform preliminary environment setup above.
2. Install [Java 17](https://openjdk.org/)
3. Install [Spark 3.3.2 (Scala 2.13 version)](https://archive.apache.org/dist/spark/spark-3.3.2/spark-3.3.2-bin-hadoop3-scala2.13.tgz)
4. Start only the database and message queue containers.
```shell
docker compose up db mq
```
5. In a separate bash shell, load the environment variables.
```shell
source .env && source sensitive.env
```
6. Build and test the project.
```shell
./gradlew build
```
7. In a separate terminal, start the web server first.
```shell
./gradlew applications:web-server:run
```
8. In a separate bash shell, start the data analyzer server second.
```shell
./gradlew applications:data-analyzer:run
```
9. Start the data collector server last.
```shell
./gradlew applications:data-collector:run
```
10. In a web browser, open [http://localhost:8888](http://localhost:8080)
11. To stop the servers and Docker containers, press `CTRL+C` in the bash shells from which they were started.

_&copy;2024 Tyler Kinkade, All Rights Reserved_