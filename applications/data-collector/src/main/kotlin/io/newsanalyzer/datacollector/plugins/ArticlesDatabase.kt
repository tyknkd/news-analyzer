package io.newsanalyzer.datacollector.plugins

import io.newsanalyzer.datacollector.models.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*
import com.zaxxer.hikari.*


object ArticlesDatabase {
    fun init() {
        val jdbcUrl = "jdbc:h2:file:./build/db"
        val driverClassName = "org.h2.Driver"
        val database = Database.connect(createHikariDataSource(url = jdbcUrl, driver = driverClassName))
        transaction(database) {
            SchemaUtils.create(Articles)
        }
    }

    private fun createHikariDataSource(url: String, driver: String) =
        HikariDataSource(HikariConfig().apply {
            jdbcUrl = url
            driverClassName = driver
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })

    suspend fun <T> dbQuery(block: suspend() -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}