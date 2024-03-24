plugins {
    id("news-analyzer.kotlin-common-conventions")
}

val postgres_jdbc_version: String by project
val h2_version: String by project
val exposed_version: String by project
val hikaricp_version: String by project

dependencies {
    implementation("org.postgresql:postgresql:$postgres_jdbc_version")
    implementation("com.h2database:h2:$h2_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposed_version")
    implementation("com.zaxxer:HikariCP:$hikaricp_version")
}