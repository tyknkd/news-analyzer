plugins {
    id("news-analyzer.kotlin-messagequeue-conventions")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

dependencies {
    implementation(project(":components:test-support"))
}