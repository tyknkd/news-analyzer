plugins {
    id("news-analyzer.kotlin-common-conventions")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

dependencies {
    implementation(project(":components:data-support"))
}