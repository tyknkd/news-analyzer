package io.newsanalyzer.datasupport

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import com.zaxxer.hikari.*

open class DatabaseTemplate(dbName: String, tables: List<Table>) {
    var database: Database
    init {
        val driverClassName = "org.postgresql.Driver"
        val port = System.getenv("POSTGRES_PORT")
        val host = if (System.getenv("OS_ENV") == "container") {
            System.getenv("POSTGRES_HOST")
        } else {
            "localhost"
        }
        val password = System.getenv("POSTGRES_PASSWORD")
        if (password == null || password == "") {
            throw RuntimeException("Database password environment variable 'POSTGRES_PASSWORD' is empty or missing")
        }
        val user = System.getenv("POSTGRES_USER")
        val jdbcUrl = "jdbc:postgresql://$host:$port/$dbName?reWriteBatchedInserts=true"
        database = Database.connect(
            createHikariDataSource(
                url = jdbcUrl,
                driver = driverClassName,
                user = user,
                passwd = password)
        )
        transaction(database) {
            for (table in tables) {
                SchemaUtils.create(table)
            }
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
}