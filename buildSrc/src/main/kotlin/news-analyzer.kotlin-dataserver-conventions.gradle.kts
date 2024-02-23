import gradle.kotlin.dsl.accessors._d03ce625885c886349b7c56a7c284c10.implementation
import org.gradle.kotlin.dsl.provideDelegate

plugins {
    id("news-analyzer.kotlin-application-conventions")
}

val postgres_version: String by project
val h2_version: String by project
val exposed_version: String by project
val hikaricp_version: String by project

dependencies {
    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("com.h2database:h2:$h2_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposed_version")
    implementation("com.zaxxer:HikariCP:$hikaricp_version")
}