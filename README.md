# News Analyzer App
**Tyler Kinkade**

[https://github.com/tyknkd/news-analyzer](https://github.com/tyknkd/news-analyzer)

_CSCA 5028: Applications of Software Architecture for Big Data, University of Colorado Boulder_

## Tech Stack
- [Kotlin](https://kotlinlang.org/) v.1.9.20: Programming language
- [Ktor](https://ktor.io/) v.2.3.8: Kotlin application framework
- [Netty](https://netty.io/) v.4.1.106: Web server
- [Apache Freemarker](https://freemarker.apache.org/) v.2.3.32: Dynamic webpage templating
- [PostgreSQL](https://www.postgresql.org/) v.16.2: Relational database
- [Exposed](https://github.com/JetBrains/Exposed) v.0.41.1: Object relational mapping framework
- [HikariCP](https://github.com/brettwooldridge/HikariCP) v.5.1.0: Database connection pooling
- [Junit](https://junit.org/junit5/) v.4.13.2: Testing
- [Kover](https://kotlin.github.io/kotlinx-kover/gradle-plugin/) v.0.7.5: Test code coverage measurement
- [Prometheus](https://prometheus.io/) v.1.6.3: Performance metrics and monitoring
- [Docker Engine](https://www.docker.com/) v.25.0.3: Containerization
- [Java Virtual Machine](https://openjdk.org/) v.17.0.10: Compilation and libraries
- [Gradle](https://gradle.org/) v.8.6: Build tool
- [Ubuntu](https://ubuntu.com/) v.22.4.4: Operating system


## Final Project Rubric
(Click links to see relevant code)
- C-level
  - Web application basic form, reporting
  - Data collection
  - Data analyzer
  - Unit tests
  - Data persistence in any data store
  - REST collaboration internal or API endpoint
  - Production environment
- B-level
  - Integration tests
  - Using mock objects or any test doubles
  - Continuous integration
  - Production monitoring instrumenting
      - `/health` endpoint
      - `/metrics` endpoint
- A-level
  - Event collaboration messaging
  - Continuous delivery

## Live Production Deployment

## API

## Local Development Setup
For local development, you can either (A) run the app fully containerized on a local machine or (B) run each server (web, data collector, data analyzer) from separate terminals with only the database and message queue in Docker containers.
Either way, you must perform the preliminary environment setup first.
### Preliminary Environment Setup
1. Install [Docker](https://www.docker.com/).
2. In a bash shell, clone the git repository and change to the project directory.
```shell
git clone https://github.com/tyknkd/news-analyzer.git
cd news-analyzer
```
3. Run the following bash commands to create a secrets file for the PostgreSQL database password (changing the `yourpasswordgoeshere` string as desired).
```shell
mkdir secrets && echo yourpasswordgoeshere > secrets/postgres_password.txt
```
4. Obtain an API key from [https://newsapi.org](https://newsapi.org) and save it to a separate secrets file with the following bash command, replacing the `yournewsapikeygoeshere` string with your newly obtained key.
```shell
echo yournewsapikeygoeshere > secrets/news_api_key.txt
```

### A. Fully Containerized Setup
1. Build and start the Docker containers.
```shell
docker compose up
```
2. In a web browser, open [http://localhost:8080](http://localhost:8080)
3. To stop the app, press `CTRL+C` in the bash shell from which it was started.

### B. Separate Servers Setup
1. Install [Java 17](https://openjdk.org/).
2. Start only the database and RabbitMQ containers.
```shell
docker compose up db rabbitmq
```
3. In a separate bash shell, load the environment variables.
```shell
source .env && source sensitive.env
```
4. Build and test the project.
```shell
./gradlew build
```
5. Optional: Check test code coverage.
```shell
./gradlew koverHtmlReport
```
6. Start the data collector server.
```shell
./gradlew applications:datacollector:run
```
7. In a separate bash shell, start the data analyzer server.
```shell
./gradlew applications:dataanalyzer:run
```
8. In a separate terminal, start the web server.
```shell
./gradlew applications:webserver:run
```
9. In a web browser, open [http://localhost:8080](http://localhost:8080)
10. To stop the servers and Docker containers, press `CTRL+C` in the bash shells from which they were started.

_&copy;2024 Tyler Kinkade, All Rights Reserved_