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
     * localhost → прокси 9081.
     * Хост сервера Onyx (например 155.212.162.11) → тот же хост (как в UI Onyx, без обрывов стрима).
     * Остальное (lk.cleardocs.ru и т.д.) → api.cleardocs.ru.
     */
    val onyxBaseUrl: String
        get() {
            val h = window.location.hostname
            val protocol = if (window.location.protocol == "https:") "https" else "http"
            return when {
                h == "localhost" || h == "127.0.0.1" -> "http://localhost:9081"
                h == "155.212.162.11" -> "$protocol://155.212.162.11"
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

    /** HttpClient для Onyx API (файлы, стрим чата). */
    fun createOnyxHttpClient(): HttpClient = HttpClient(Js) {
        defaultRequest {
            url(onyxBaseUrl)
            header("Accept", "*/*")
        }
    }
}
