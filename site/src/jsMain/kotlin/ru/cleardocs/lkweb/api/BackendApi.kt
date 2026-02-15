package ru.cleardocs.lkweb.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import ru.cleardocs.lkweb.ApiConfig
import ru.cleardocs.lkweb.api.dto.GetPlansDto
import ru.cleardocs.lkweb.api.dto.MeDto

/**
 * API-клиент для бэкенда ClearDocs.
 * Использует [ApiConfig.createHttpClient] для базового URL и JSON.
 */
object BackendApi {

    private val client = ApiConfig.createHttpClient()

    /**
     * Возвращает список тарифов (plans).
     * GET /api/v1/plans
     */
    suspend fun plans(): GetPlansDto =
        client.get("api/v1/plans").body()

    /**
     * Возвращает данные текущего пользователя.
     * GET /api/v1/me с заголовком Authorization: Bearer &lt;token&gt;.
     * Токен следует получать из Firebase Auth (idToken текущего пользователя).
     * Пока бэкенд возвращает 401 без проверки токена.
     */
    suspend fun me(token: String): MeDto =
        client.get("api/v1/me") {
            header("Authorization", "Bearer $token")
        }.body()
}
