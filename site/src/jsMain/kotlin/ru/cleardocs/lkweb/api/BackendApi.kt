package ru.cleardocs.lkweb.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import ru.cleardocs.lkweb.ApiConfig
import ru.cleardocs.lkweb.api.dto.GetPlansDto
import ru.cleardocs.lkweb.api.dto.MeDto
import ru.cleardocs.lkweb.api.dto.MeResponseDto

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
     * GET /api/v1/users/me с заголовком Authorization: Bearer &lt;token&gt;.
     * Токен следует получать из Firebase Auth (idToken текущего пользователя).
     */
    suspend fun me(token: String): MeDto {
        val response = client.get("api/v1/users/me") {
            header("Authorization", "Bearer $token")
        }
        if (!response.status.isSuccess()) {
            val body = try { response.bodyAsText() } catch (_: Throwable) { "" }
            throw BackendError(response.status.value, body)
        }
        val resp = response.body<MeResponseDto>()
        return MeDto(
            id = resp.user.email ?: "",
            email = resp.user.email,
            name = resp.user.name,
        )
    }
}

/** Ошибка бэкенда с кодом и телом ответа. */
class BackendError(val code: Int, val body: String) : Exception("$code: ${body.ifBlank { "no body" }}")
