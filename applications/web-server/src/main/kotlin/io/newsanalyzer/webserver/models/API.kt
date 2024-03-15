package io.newsanalyzer.webserver.models

import kotlinx.serialization.Serializable

@Serializable
data class Endpoint (
    val title: String,
    val rel: String,
    val href: String,
    val links: List<Endpoint>?
)

val entrypoint = Endpoint(
    title = "Tech Industry News Analyzer API",
    rel = "self",
    href = "/api",
    links = listOf(
        Endpoint(
            title = "Extracted Topics",
            rel = "collection",
            href = "/api/topics",
            links = null
        ),
        Endpoint(
            title = "Articles",
            rel = "collection",
            href = "/api/articles",
            links = null
        ),
        Endpoint(
            title = "Articles by Topic",
            rel = "collection",
            href = "/api/topics/articles",
            links = null
        ),
        Endpoint(
            title = "Articles on a Topic",
            rel = "collection",
            href = "/api/topics/{topicId}/articles",
            links = null
        )
    )
)