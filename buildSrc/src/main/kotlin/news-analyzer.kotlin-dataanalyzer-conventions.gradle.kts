plugins {
    id("news-analyzer.kotlin-dataserver-conventions")
}

val spark_version: String by project

dependencies {
    implementation("org.jetbrains.kotlinx.spark:kotlin-spark-api_3.3.2_2.13:1.2.4")
    implementation("org.apache.logging.log4j:log4j-core:2.21.1")
    implementation("org.apache.spark:spark-sql_2.13:$spark_version")
    implementation("org.apache.spark:spark-mllib_2.13:$spark_version")
}

val sparkJava17CompatibilityJvmArgs = listOf(
    "--add-opens=java.base/java.lang=ALL-UNNAMED",
    "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED",
    "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
    "--add-opens=java.base/java.io=ALL-UNNAMED",
    "--add-opens=java.base/java.net=ALL-UNNAMED",
    "--add-opens=java.base/java.nio=ALL-UNNAMED",
    "--add-opens=java.base/java.util=ALL-UNNAMED",
    "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED",
    "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED",
    "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
    "--add-opens=java.base/sun.nio.cs=ALL-UNNAMED",
    "--add-opens=java.base/sun.security.action=ALL-UNNAMED",
    "--add-opens=java.base/sun.util.calendar=ALL-UNNAMED",
    "--add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED"
)

(tasks.test) {
    jvmArgs = sparkJava17CompatibilityJvmArgs
}

(tasks.run) {
    jvmArgs = sparkJava17CompatibilityJvmArgs
}