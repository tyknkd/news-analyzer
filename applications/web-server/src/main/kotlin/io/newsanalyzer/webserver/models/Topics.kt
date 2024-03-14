package io.newsanalyzer.webserver.models

import org.jetbrains.exposed.sql.*

object Topics: Table() {
    val topicId = integer("topicId")
    val terms = varchar("terms", 256)
    override val primaryKey = PrimaryKey(topicId)
}
