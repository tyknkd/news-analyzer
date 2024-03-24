package io.newsanalyzer.datasupport.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Topic (
    val topicId: Int,
    val terms: String
)

object Topics: Table() {
    val topicId = integer("topicId")
    val terms = varchar("terms", 256)
    override val primaryKey = PrimaryKey(topicId)
}
