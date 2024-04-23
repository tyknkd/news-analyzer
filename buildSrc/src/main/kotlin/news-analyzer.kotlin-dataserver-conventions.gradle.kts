plugins {
    id("news-analyzer.kotlin-application-conventions")
    id("news-analyzer.kotlin-database-conventions")
}

val kotlinx_dataframe_version: String by project

dependencies {
    implementation(project(":components:data-support"))
    implementation(project(":components:test-support"))
    implementation("org.jetbrains.kotlinx:dataframe:$kotlinx_dataframe_version")
}