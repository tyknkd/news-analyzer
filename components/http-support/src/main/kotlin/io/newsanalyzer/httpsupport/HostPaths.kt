package io.newsanalyzer.httpsupport

class HostPaths {
    fun getAnalyzerPath(): String {
        val port = System.getenv("ANALYZER_PORT")
        val host = if (System.getenv("OS_ENV") == "container") {
            System.getenv("ANALYZER_HOST")
        } else {
            "localhost"
        }
        return "$host:$port"
    }
    fun getCollectorPath(): String {
        val port = System.getenv("COLLECTOR_PORT")
        val host = if (System.getenv("OS_ENV") == "container") {
            System.getenv("COLLECTOR_HOST")
        } else {
            "localhost"
        }
        return "$host:$port"
    }
    fun getWebServerPath(): String {
        val port = System.getenv("WEBSERVER_PORT")
        val host = if (System.getenv("OS_ENV") == "container") {
            System.getenv("COLLECTOR_HOST")
        } else {
            "localhost"
        }
        return "$host:$port"
    }
}