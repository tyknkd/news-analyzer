plugins {
    id("news-analyzer.kotlin-common-conventions")
}

val quartz_version: String by project

dependencies {
    implementation("org.quartz-scheduler:quartz:$quartz_version")
}