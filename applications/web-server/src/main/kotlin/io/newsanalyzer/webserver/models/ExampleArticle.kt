package io.newsanalyzer.webserver.models

import java.util.concurrent.atomic.AtomicInteger

class ExampleArticle
private constructor(val id: Int, var title: String, var body: String) {
    companion object {
        private val idCounter = AtomicInteger()
        fun newEntry(title: String, body: String) = ExampleArticle(idCounter.getAndIncrement(), title, body)
    }
}

val exampleArticles = mutableListOf(ExampleArticle.newEntry("Example Article Title", "This is an example of an article body."))