package ru.cleardocs.lkweb

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json

/**
 * Настройки Ktor: базовый URL сервера API.
 */
object ApiConfig {
    /** Базовый URL API (без завершающего слеша). */
    const val baseUrl: String = "https://155.212.162.11"

    /**
     * HttpClient с предустановленным [baseUrl] в defaultRequest.
     * Все относительные пути (например, `get("api/users")`) будут идти на этот хост.
     */
    fun createHttpClient(): HttpClient = HttpClient(Js) {
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            url(baseUrl)
        }
    }
}
