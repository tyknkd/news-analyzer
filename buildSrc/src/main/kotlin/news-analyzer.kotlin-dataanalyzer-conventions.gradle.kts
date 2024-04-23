plugins {
    id("news-analyzer.kotlin-dataserver-conventions")
}

val kotlinx_spark_version: String by project
val scala_version: String by project
val spark_version: String by project
val log4j_version: String by project

dependencies {
    implementation("org.jetbrains.kotlinx.spark:kotlin-spark-api_${spark_version}_$scala_version:$kotlinx_spark_version")
    implementation("org.apache.logging.log4j:log4j-core:$log4j_version")
    implementation("org.apache.spark:spark-sql_$scala_version:$spark_version")
    implementation("org.apache.spark:spark-mllib_$scala_version:$spark_version")
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