package io.newsanalyzer.datasupport

import org.jetbrains.exposed.sql.*
import io.newsanalyzer.datasupport.models.*

interface TopicsGatewayTemplate: DataGatewayTemplate {
    fun ResultRow.toTopic() = Topic(
        topicId = this[Topics.topicId],
        terms = this[Topics.terms]
    )

    suspend fun upsertTopics(topics: List<Topic>): Boolean {
        var success = false
        dbQuery {
            val onUpdateExclude = Topics.columns - setOf(Topics.terms)
            val results = Topics.batchUpsert(data = topics, onUpdateExclude = onUpdateExclude) {
                    (topicId, terms) ->
                this[Topics.topicId] = topicId
                this[Topics.terms] = terms
            }
            success = results.isNotEmpty()
        }
        return success
    }

    suspend fun allTopics(): List<Topic> = dbQuery {
        Topics.selectAll().map { row -> row.toTopic() }
    }
}