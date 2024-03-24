plugins {
    id("news-analyzer.kotlin-application-conventions")
    id("news-analyzer.kotlin-database-conventions")
}

dependencies {
    implementation(project(":components:data-support"))
    implementation("org.jetbrains.kotlinx:dataframe:0.13.0")
}