plugins {
    id("news-analyzer.kotlin-client-conventions")
}

dependencies {
    implementation(project(":components:models"))
}

application {
    mainClass.set("com.example.datacollector.CollectorKt")
}