plugins {
    id("news-analyzer.kotlin-client-conventions")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}