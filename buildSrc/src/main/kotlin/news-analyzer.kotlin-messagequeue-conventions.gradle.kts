plugins {
    id("news-analyzer.kotlin-common-conventions")
}

val rabbit_client_version: String by project

dependencies {
    implementation("com.rabbitmq:amqp-client:$rabbit_client_version")
}