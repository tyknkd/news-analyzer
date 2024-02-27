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
- [Docker](https://www.docker.com/) v.4.26.1: Containerization
- [Java Virtual Machine](https://openjdk.org/) v.17.0.9: Compilation and libraries
- [Gradle](https://gradle.org/) v.8.5: Build tool
- [Ubuntu](https://ubuntu.com/) v.22.0.4: Operating system


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

## Production Deployment

## API

## Local Development Setup
1. Install Java 17.
2. Obtain an API key from [https://newsapi.org](https://newsapi.org) and assign it to `NEWS_API_KEY` in the `sensitive.env` file (i.e., replace the `0123...tuv` string with your key).
3. Run the following command in a bash shell (changing the `pass1234` password string as desired) to create a Docker secrets file for the PostgreSQL database password.
```shell
mkdir secrets && echo pass1234 > secrets/postgres_password.txt
```
4. Edit the database environment variables in `example.env` as desired and rename the file as `.env` (i.e., without "example").
5. load the environment variables in the same Linux/Unix shell from which you will later build and run the data collector application.
```shell
source .env && source sensitive.env
```
6. Build the program
```shell
./gradlew build
```
7. To check test code coverage:
```shell
./gradlew koverHtmlReport
```

_&copy;2024 Tyler Kinkade, All Rights Reserved_