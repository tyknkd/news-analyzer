package io.newsanalyzer.datacollector.plugins

import io.newsanalyzer.datacollector.models.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*
import com.zaxxer.hikari.*


object ArticlesDatabase {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val host = System.getenv("POSTGRES_HOST")
        val port = System.getenv("POSTGRES_HOSTPORT")
        val dbName = System.getenv("POSTGRES_DB")
        val user = System.getenv("POSTGRES_USER")
        val password = System.getenv("POSTGRES_PASSWORD")
        val jdbcUrl = "jdbc:postgresql://$host:$port/$dbName"
        val database = Database.connect(
            createHikariDataSource(
                url = jdbcUrl,
                driver = driverClassName,
                user = user,
                passwd = password))
        transaction(database) {
            SchemaUtils.create(Articles)
        }
    }

    private fun createHikariDataSource(url: String,
                                       driver: String,
                                       user: String,
                                       passwd: String) =
        HikariDataSource(HikariConfig().apply {
            jdbcUrl = url
            driverClassName = driver
            username = user
            password = passwd
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })

    suspend fun <T> dbQuery(block: suspend() -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}