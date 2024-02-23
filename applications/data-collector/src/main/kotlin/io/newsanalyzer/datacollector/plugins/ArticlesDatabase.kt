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
        val user = "root"
        val password = "root"
//        val database = Database.connect(
//            url = jdbcUrl,
//            user = user,
//            driver = driverClassName,
//            password = password
//        )
        val database = Database.connect(createHikariDataSource(url = jdbcUrl, driver = driverClassName, user = user, pass = password))
        transaction(database) {
            SchemaUtils.create(Articles)
        }
    }

    private fun createHikariDataSource(
        url: String, driver: String, user: String, pass: String) =
        HikariDataSource(HikariConfig().apply {
            jdbcUrl = url
            driverClassName = driver
            username = user
            password = pass
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })

    suspend fun <T> dbQuery(block: suspend() -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}