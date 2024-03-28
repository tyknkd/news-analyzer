package io.newsanalyzer.httpsupport

class HostPaths {
    fun getAnalyzerPath(): String {
        val port = System.getenv("ANALYZER_PORT")
        return if (System.getenv("OS_ENV") == "container") {
            "data-analyzer:$port"
        } else {
            "localhost:$port"
        }
    }
    fun getCollectorPath(): String {
        val port = System.getenv("COLLECTOR_PORT")
        return if (System.getenv("OS_ENV") == "container") {
            "data-collector:$port"
        } else {
            "localhost:$port"
        }
    }
    fun getWebServerPath(): String {
        val port = System.getenv("WEBSERVER_PORT")
        return if (System.getenv("OS_ENV") == "container") {
            "web-server:$port"
        } else {
            "localhost:$port"
        }
    }
}