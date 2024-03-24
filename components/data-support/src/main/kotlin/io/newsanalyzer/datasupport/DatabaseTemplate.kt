package io.newsanalyzer.datasupport

import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.jetbrains.exposed.sql.transactions.experimental.*
import com.zaxxer.hikari.*

interface DatabaseTemplate {
    val envDbName: String
    val tables: List<Table>
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
        val password = System.getenv("POSTGRES_PASSWORD")
        if (password == null || password == "") {
            throw RuntimeException("Database password file (secrets/postgres_password.txt) is empty or missing")
        }
        val dbName = System.getenv(envDbName)
        val user = System.getenv("POSTGRES_USER")
        val jdbcUrl = "jdbc:postgresql://$host:$port/$dbName?reWriteBatchedInserts=true"
        val database = Database.connect(
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

    suspend fun <T> dbQuery(block: suspend() -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}