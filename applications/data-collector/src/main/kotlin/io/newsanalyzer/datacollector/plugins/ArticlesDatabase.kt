package io.newsanalyzer.datacollector.plugins

import io.newsanalyzer.datacollector.models.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*


object ArticlesDatabase {
    fun init() {
        val jdbcURL = "jdbc:h2:file:./build/db"
        val driverClassName = "org.h2.Driver"
        val user = "root"
        val password = ""
        val database = Database.connect(
            url = jdbcURL,
            user = user,
            driver = driverClassName,
            password = password
        )
        transaction(database) {
            SchemaUtils.create(Articles)
        }
    }

    suspend fun <T> dbQuery(block: suspend() -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}