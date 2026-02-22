package ru.cleardocs.lkweb

import kotlinx.browser.window
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
    /** Базовый URL ClearDocs API. На localhost — CORS-прокси 9082 -> 155.212.162.11:8080, иначе бэкенд напрямую. */
    val baseUrl: String
        get() {
            val h = window.location.hostname
            return when {
                h == "localhost" || h == "127.0.0.1" -> "http://localhost:9082"
                else -> "http://155.212.162.11:8080"
            }
        }

    /** Базовый URL Onyx Chat API. На localhost/127.0.0.1 — CORS-прокси 9081, иначе бэкенд. */
    val onyxBaseUrl: String
        get() {
            val h = window.location.hostname
            return if (h == "localhost" || h == "127.0.0.1") {
                "http://localhost:9081/api/"
            } else {
                "http://155.212.162.11:3000/api/"
            }
        }

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
            header("Accept", "application/json")
        }
    }

    /**
     * HttpClient для Onyx Chat API ([onyxBaseUrl]).
     */
    fun createOnyxHttpClient(): HttpClient = HttpClient(Js) {
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            url(onyxBaseUrl)
        }
    }
}
