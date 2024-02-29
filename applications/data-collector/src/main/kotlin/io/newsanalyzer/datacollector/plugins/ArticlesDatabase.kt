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
        lateinit var host: String
        lateinit var port: String
        if (System.getenv("OS_ENV") == "container") {
            host = "db"
            port = System.getenv("POSTGRES_CONTAINER_PORT")
        } else {
            host = "localhost"
            port = System.getenv("POSTGRES_HOST_PORT")
        }
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