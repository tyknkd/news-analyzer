package com.example.datacollector

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun main() {
    val client = HttpClient(Java)
    val response: HttpResponse = client.get("https://ktor.io/")
    println(response.status)
    client.close()
}

