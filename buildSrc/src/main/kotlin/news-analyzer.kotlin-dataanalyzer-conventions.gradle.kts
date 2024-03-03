plugins {
    id("news-analyzer.kotlin-dataserver-conventions")
}

val spark_version: String by project

dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:0.13.0")
    implementation("org.jetbrains.kotlinx.spark:kotlin-spark-api_3.3.2_2.13:1.2.4")
    implementation("org.apache.spark:spark-sql_2.13:$spark_version")
    implementation("org.apache.spark:spark-mllib_2.13:$spark_version")
}

tasks {
    shadowJar {
        configurations.forEach {
            it.exclude("org.apache.spark")
            it.exclude("org.scala-lang")
        }
    }
}
