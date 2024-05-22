# Tech Industry News Analyzer App ![workflow status](https://github.com/tyknkd/news-analyzer/actions/workflows/ci-cd.yaml/badge.svg)
**Tyler Kinkade**

_CSCA 5028: Applications of Software Architecture for Big Data, University of Colorado Boulder_

- [www.jeitikei.online](https://www.jeitikei.online/) (app)
- [github.com/tyknkd/news-analyzer](https://github.com/tyknkd/news-analyzer) (repository)

## Overview
This independent project applies big data software architecture principles and machine learning techniques to analyze 
recent tech industry news articles, automatically extract common themes, and sort them into groups by topic. The primary 
aim is to help readers quickly identify current trends in media reporting on the tech industry and focus on the topics of 
most interest to the reader.

## Architecture
![architecture diagram](https://github.com/tyknkd/news-analyzer/blob/main/images/architecture_diagram.png)

The software architecture consists of three microservices which interact via a message queue broker, as illustrated in the above 
diagram. First, (starting on the left side of the diagram) a data collector microservice collects news article data daily from an 
external internet source ([newsapi.org](https://newsapi.org)), stores the data in a database, and publishes it to a message queue. Next, 
upon receiving the data from the message queue, a data analyzer microservice stores it in a database, applies [Latent Dirichlet Allocation](https://en.wikipedia.org/wiki/Latent_Dirichlet_allocation) (LDA)
to discover common topics, and publishes the results to another message queue. Finally, a web microservice receives the
data, stores it in a database, and presents the articles sorted by topic to the end user via web pages and a REST API service.

## Design
The project design was guided by current best practices in software development and big data architecture. Through the use of 
containerized pods with delivery-confirmed message queues and data persistence, service interruptions to the
end user are minimized and the system remains robust to temporary partitions between the services. Because the collected data is 
well-structured, relational databases are used to efficiently store, process, and retrieve the data. In addition, 
test doubles and mock external services were used to implement efficient unit and integration tests in an automated continuous integration
and deployment workflow. Furthermore, online metrics and visualizations permit real-time monitoring of system 
performance. 

## Tech Stack
The following technology tools were used to implement the project.
- [Ubuntu](https://ubuntu.com/) v.22.4.4: Operating system
- [Kotlin](https://kotlinlang.org/) v.1.9.22: Programming language
- [Java Virtual Machine](https://openjdk.org/) v.17.0.10: Compilation and libraries
- [Gradle](https://gradle.org/) v.8.7: Build tool
- [Ktor](https://ktor.io/) v.2.3.8: Kotlin application framework
- [Netty](https://netty.io/) v.4.1.106: Web server
- [Apache Freemarker](https://freemarker.apache.org/) v.2.3.32: Dynamic webpage templating
- [PostgreSQL](https://www.postgresql.org/) v.16.2: Relational database
- [Exposed](https://github.com/JetBrains/Exposed) v.0.48.0: Object relational mapping framework
- [HikariCP](https://github.com/brettwooldridge/HikariCP) v.5.1.0: Database connection pooling
- [Apache Spark](https://spark.apache.org/) v.3.3.2: Data analytics
- [Kotlin for Apache Spark](https://github.com/Kotlin/kotlin-spark-api) v.1.2.4: Kotlin-Spark compatibility API
- [RabbitMQ](https://www.rabbitmq.com) v.5.21.0: Messaging broker
- [Junit](https://junit.org/junit5/) v.4.13.2: Testing
- [Kover](https://kotlin.github.io/kotlinx-kover/gradle-plugin/) v.0.7.5: Test code coverage measurement
- [Micrometer](https://micrometer.io/) v.1.6.8: Application metrics interface
- [Prometheus](https://prometheus.io/) v.2.51.2: Performance metrics and monitoring storage
- [Grafana](https://grafana.com/) v.10.4.2: Performance metrics visualization
- [Docker Engine](https://www.docker.com/) v.25.0.3: Containerization
- [Kubernetes](https://kubernetes.io/) v.1.30.0: Deployment container orchestrator
- [Kompose](https://kompose.io/) v.1.33.0: Docker Compose to Kubernetes conversion tool
- [Helm](https://helm.sh/) v.3.14.4: Kubernetes package manager
- [GitHub](https://github.com/): Version control, continuous integration and deployment
- [Google Kubernetes Engine](https://cloud.google.com/kubernetes-engine): v.1.28.8: Cloud computing service

## Requirements
This table summarizes the required software features and practices implemented in the project and the corresponding code.

| Feature                                                                                                                                                                                                      | Code   |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|
| [Web application](https://www.jeitikei.online/)                                                                                                                                                              | [applications/web-server](https://github.com/tyknkd/news-analyzer/tree/main/applications/web-server)                                                                                                                                                                                                                                                                                                                                                 |
| Data collection                                                                                                                                                                                              | [applications/data-collector](https://github.com/tyknkd/news-analyzer/tree/main/applications/data-collector)                                                                                                                                                                                                                                                                                                                                         |
| Data analyzer                                                                                                                                                                                                | [applications/data-analyzer](https://github.com/tyknkd/news-analyzer/tree/main/applications/data-analyzer)                                                                                                                                                                                                                                                                                                                                           |
| [Unit tests](https://github.com/tyknkd/news-analyzer/actions)                                                                                                                                                | [web-server/src/test](https://github.com/tyknkd/news-analyzer/tree/main/applications/web-server/src/test/), [data-collector/src/test](https://github.com/tyknkd/news-analyzer/tree/main/applications/data-collector/src/test/), [data-analyzer/src/test](https://github.com/tyknkd/news-analyzer/tree/main/applications/data-analyzer/src/test/), [data-support/src/test](https://github.com/tyknkd/news-analyzer/tree/main/components/data-support/src/test/), etc. |
| Data persistence                                                                                                                                                                                             | [components/data-support](https://github.com/tyknkd/news-analyzer/tree/main/components/data-support)                                                                                                                                                                                                                                                                                                                                                 |
| [REST API endpoint](https://www.jeitikei.online/api/)                                                                                                                                                        | [web-server/src/main/kotlin/io/newsanalyzer/webserver/plugins/Routing.kt](https://github.com/tyknkd/news-analyzer/blob/main/applications/web-server/src/main/kotlin/io/newsanalyzer/webserver/plugins/Routing.kt)                                                                                                                                                                                                                                    |
| [Production environment](https://www.jeitikei.online/)                                                                                                                                                       | [deployment](https://github.com/tyknkd/news-analyzer/tree/main/deployment)                                                                                                                                                                                                                                                                                                                                                                           |
| [Integration tests](https://github.com/tyknkd/news-analyzer/actions)                                                                                                                                         | [web-server/src/test](https://github.com/tyknkd/news-analyzer/tree/main/applications/web-server/src/test/), [data-collector/src/test](https://github.com/tyknkd/news-analyzer/tree/main/applications/data-collector/src/test/), [data-analyzer/src/test](https://github.com/tyknkd/news-analyzer/tree/main/applications/data-analyzer/src/test/)                                                                                                     |
| Test doubles                                                                                                                                                                                                 | [components/test-support](https://github.com/tyknkd/news-analyzer/tree/main/components/test-support)                                                                                                                                                                                                                                                                                                                                                 |
| [Continuous integration](https://github.com/tyknkd/news-analyzer/actions)                                                                                                                                    | [github/workflows](https://github.com/tyknkd/news-analyzer/tree/main/.github/workflows)                                                                                                                                                                                                                                                                                                                                                              |
| Monitoring ([screenshot](https://github.com/tyknkd/news-analyzer/blob/main/images/monitoring_workloads.png), [health](https://www.jeitikei.online/health/), [metrics](https://www.jeitikei.online/metrics/)) | [monitoring](https://github.com/tyknkd/news-analyzer/tree/main/monitoring)                                                                                                                                                                                                                                                                                                                                                                           |
| Event collaboration messaging                                                                                                                                                                                | [components/mq-support](https://github.com/tyknkd/news-analyzer/tree/main/components/mq-support)                                                                                                                                                                                                                                                                                                                                                     |
| [Continuous delivery](https://github.com/tyknkd/news-analyzer/actions)                                                                                                                                       | [github/workflows](https://github.com/tyknkd/news-analyzer/tree/main/.github/workflows)                                                                                                                                                                                                                                                                                                                                                              | 

## Testing
Gradle is used to implement unit and integration tests, and these tests are incorporated into the continuous integration/continuous
deployment workflow. Using test doubles and mock external services, the unit tests check each element of the system (e.g., database
operations, message queue, data processing, etc.), and the integration tests check that these elements function together at the app
level as expected: that the data can be reliably (1) collected, (2) stored in the collector database, (3) transferred to the data analyzer,
(4) processed with unsupervised machine learning (LDA), (5) stored in the analyzer database, (6) passed to
the web server, (7) stored in the web-server database, and (8) displayed to the end user in reverse chronological order grouped by topic.
(It is worth noting that because unsupervised machine learning is being applied to unlabeled data in this project, the article topics 
can only be identified by common keywords.)

## Live Production Environment
The project is deployed on [Google Kubernetes Engine](https://cloud.google.com/kubernetes-engine) and can be accessed at [www.jeitikei.online](https://www.jeitikei.online/)
![homepage](https://github.com/tyknkd/news-analyzer/blob/main/images/screenshot_home_page.png)

## REST API
The REST API entry point is [www.jeitikei.online/api](https://www.jeitikei.online/api/). [HATEOAS](https://en.wikipedia.org/wiki/HATEOAS) principles are applied in order to facilitate hypermedia-driven 
discovery of the endpoints within the API.
![homepage](https://github.com/tyknkd/news-analyzer/blob/main/images/screenshot_api_hateoas.png)

## Monitoring
Production monitoring is accomplished by scraping metrics with [Prometheus](https://cloud.google.com/stackdriver/docs/managed-prometheus) 
and visualizing with [Google Cloud Monitoring](https://cloud.google.com/monitoring).  
![monitoring screenshot](https://github.com/tyknkd/news-analyzer/blob/main/images/monitoring_workloads.png)

Local development environment monitoring is implemented with Prometheus and Grafana instances run in Docker containers.
![dev monitoring screenshot](https://github.com/tyknkd/news-analyzer/blob/main/images/monitoring_dev.png)

## Local Setup
To run the app locally, you can either (A) run the app fully containerized on a local machine or (B) run each service 
(web, data collector, data analyzer) from separate terminals with only the database and message queue in Docker containers.
Either way, you must perform the preliminary environment setup first. (The following commands are for a Linux/Unix environment.)

### Preliminary Environment Setup
1. Install [Docker Engine](https://www.docker.com/).
2. In a bash shell, clone the git repository and change to the project directory.
```shell
git clone https://github.com/tyknkd/news-analyzer.git
cd news-analyzer
```
3. Run the following bash commands to create a secrets file for the PostgreSQL database password (changing the `yourPasswordGoesHere` string as desired).
```shell
mkdir secrets
echo yourPasswordGoesHere > secrets/postgres_password.txt
```
4. Create a secrets file for the Grafana admin password (changing the `yourPasswordGoesHere` string as desired).
```shell
echo yourPasswordGoesHere > secrets/grafana_password.txt
```
5. Obtain an API key from [newsapi.org](https://newsapi.org) and save it to a separate secrets file with the following bash command, replacing the `yourNewsApiKeyGoesHere` string with your newly obtained key. (NB: You can run the tests with a fake key, but an exception will be thrown if you attempt to run the app locally without a valid key because the real news data cannot be collected without it.)
```shell
echo yourNewsApiKeyGoesHere > secrets/news_api_key.txt
```

### A. Fully Containerized Setup
1. Perform preliminary environment setup above.
2. Load the sensitive environment variables
```shell
source sensitive.env
```
3. Optional: Run all tests in containers. (This will take several minutes.)
```shell
docker compose up test
```
4. Build and start the Docker containers. (This will take several minutes the first time.)
```shell
docker compose up
```
5. In a web browser, open [http://localhost:8888](http://localhost:8080)
6. Optional: View Grafana monitoring dashboard at [localhost:3000/d/cdk5654bbrvnkf/news-analyzer-dashboard?orgId=1](http://localhost:3000/d/cdk5654bbrvnkf/news-analyzer-dashboard?orgId=1) (Note: The Grafana username is `admin` and the password is as set in the environment setup above.)
7. To stop all containers, press `CTRL+C` in the bash shell from which it was started.

### B. Run Microservices from Separate Terminals Setup
1. Perform preliminary environment setup above.
2. Install [Java 17](https://openjdk.org/)
3. Install [Spark 3.3.2 (Scala 2.13 version)](https://archive.apache.org/dist/spark/spark-3.3.2/spark-3.3.2-bin-hadoop3-scala2.13.tgz)
4. Load the sensitive environment variables.
```shell
source sensitive.env
```
5. Start only the database and message queue containers. (If monitoring is desired, append `prometheus` and `grafana` to the command.)
```shell
docker compose up db mq
```
6. In a separate bash shell, load the environment variables, and build and test the project
```shell
source .env && source sensitive.env
./gradlew build
```
7. In a separate terminal, load the environment variables and start the web server first.
```shell
source .env && source sensitive.env
./gradlew applications:web-server:run
```
8. In a separate bash shell, load the environment variables and start the data analyzer server second.
```shell
source .env && source sensitive.env
./gradlew applications:data-analyzer:run
```
9. In a separate shell, load the environment variables and start the data collector server last.
```shell
source .env && source sensitive.env
./gradlew applications:data-collector:run
```
10. In a web browser, open [http://localhost:8888](http://localhost:8080)
11. Optional: If the Prometheus and Grafana containers are running (see Step 5), view the monitoring dashboard at [localhost:3000/d/cdk5654bbrvnkf/news-analyzer-dashboard?orgId=1](http://localhost:3000/d/cdk5654bbrvnkf/news-analyzer-dashboard?orgId=1) (Note: The Grafana username is `admin` and the password is as set in the environment setup above.)
12. To stop the servers and Docker containers, press `CTRL+C` in the bash shells from which they were started.

_&copy;2024 Tyler Kinkade, All Rights Reserved_