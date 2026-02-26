package ru.cleardocs.lkweb

import kotlinx.browser.window
import kotlinx.serialization.json.Json
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json

/**
 * Настройки Ktor: базовый URL сервера API.
 */
object ApiConfig {
    /** Базовый URL ClearDocs API. На localhost — CORS-прокси 9082, иначе api.cleardocs.ru (nginx → backend:8080). */
    val baseUrl: String
        get() {
            val h = window.location.hostname
            return when {
                h == "localhost" || h == "127.0.0.1" -> "http://localhost:9082"
                else -> "https://api.cleardocs.ru"
            }
        }

    /**
     * Базовый URL Onyx API для send-chat-message.
     * localhost → прокси 9081, production → api.cleardocs.ru (HTTPS, через nginx).
     */
    val onyxBaseUrl: String
        get() {
            val h = window.location.hostname
            return when {
                h == "localhost" || h == "127.0.0.1" -> "http://localhost:9081"
                else -> "https://api.cleardocs.ru"
            }
        }

    /**
     * HttpClient с предустановленным [baseUrl] в defaultRequest.
     * Все относительные пути (например, `get("api/users")`) будут идти на этот хост.
     */
    fun createHttpClient(): HttpClient = HttpClient(Js) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        defaultRequest {
            url(baseUrl)
            header("Accept", "application/json")
        }
    }
}
