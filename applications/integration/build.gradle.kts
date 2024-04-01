plugins {
    id("news-analyzer.kotlin-dataserver-conventions")
    id("news-analyzer.kotlin-client-conventions")
}
dependencies {
    testImplementation(project(":applications:web-server"))
    testImplementation(project(":applications:data-analyzer"))
    testImplementation(project(":applications:data-collector"))
    testImplementation(project(":components:data-support"))
    testImplementation(project(":components:test-support"))
}