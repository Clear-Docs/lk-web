package ru.cleardocs.lkweb.api

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.request.header
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.Headers
import io.ktor.http.appendPathSegments
import io.ktor.http.isSuccess
import ru.cleardocs.lkweb.ApiConfig
import ru.cleardocs.lkweb.api.dto.ChatResponseDto
import ru.cleardocs.lkweb.api.dto.CreateConnectorResponseDto
import ru.cleardocs.lkweb.api.dto.CreateUrlConnectorRequestDto
import ru.cleardocs.lkweb.api.dto.UpdateConnectorRequestDto
import ru.cleardocs.lkweb.api.dto.GetConnectorsDto
import ru.cleardocs.lkweb.api.dto.LimitDto
import ru.cleardocs.lkweb.api.dto.GetPlansDto
import ru.cleardocs.lkweb.api.dto.MeDto
import ru.cleardocs.lkweb.api.dto.PlanDto
import ru.cleardocs.lkweb.api.dto.MeResponseDto
import ru.cleardocs.lkweb.api.dto.TochkaPaymentRequestDto
import ru.cleardocs.lkweb.api.dto.TochkaPaymentResponseDto
import ru.cleardocs.lkweb.firebase.FirebaseProvider
import ru.cleardocs.lkweb.firebase.getIdToken

/**
 * API-клиент для бэкенда ClearDocs.
 * Использует [ApiConfig.createHttpClient] для базового URL и JSON.
 * Токен авторизации получает из Firebase Auth (текущий пользователь).
 */
object BackendApi {

    private val client = ApiConfig.createHttpClient()

    private suspend fun requireToken(): String {
        val user = FirebaseProvider.repository.auth.currentUser
            ?: throw IllegalStateException("User not authenticated")
        return getIdToken(user)
    }

    /**
     * Возвращает список тарифов (plans).
     * GET /api/v1/plans
     */
    suspend fun plans(): GetPlansDto =
        client.get("api/v1/plans").body()

    /**
     * Регистрирует пользователя в бэкенде (создаёт запись по Firebase-токену).
     * POST /api/v1/users/register. Вызывать после createUserWithEmailAndPassword или signInWithGoogle.
     */
    suspend fun register() {
        console.log("[BackendApi] register() - start")
        val token = requireToken()
        val response = client.post("api/v1/users/register") {
            header("Authorization", "Bearer $token")
        }
        if (!response.status.isSuccess()) {
            val body = try { response.bodyAsText() } catch (_: Throwable) { "" }
            throw BackendError(response.status.value, body)
        }
        console.log("[BackendApi] register() - done")
    }

    /**
     * Возвращает данные текущего пользователя.
     * GET /api/v1/users/me с заголовком Authorization: Bearer &lt;token&gt;.
     * Токен получается в сервисе из Firebase Auth.
     */
    suspend fun me(): MeDto {
        console.log("[BackendApi] me() - start")
        val token = requireToken()
        val response = client.get("api/v1/users/me") {
            header("Authorization", "Bearer $token")
        }
        if (!response.status.isSuccess()) {
            val body = try { response.bodyAsText() } catch (_: Throwable) { "" }
            throw BackendError(response.status.value, body)
        }
        val resp = response.body<MeResponseDto>()
        val u = resp.user
        val result = MeDto(
            id = u.id ?: u.email ?: "",
            email = u.email ?: "",
            name = u.name ?: "",
            plan = u.plan ?: PlanDto(
                code = "free",
                title = "Бесплатный",
                priceRub = 0,
                periodDays = 0,
                limit = LimitDto(maxConnectors = 0),
            ),
            isCanceled = resp.isCanceled,
        )
        console.log("[BackendApi] me() - done")
        return result
    }

    /**
     * Возвращает credentials для чата (apiKey, personaId).
     * GET /api/v1/chat с заголовком Authorization: Bearer &lt;token&gt;.
     */
    suspend fun chat(): ChatResponseDto {
        val token = requireToken()
        val response = client.get("api/v1/chat") {
            header("Authorization", "Bearer $token")
        }
        if (!response.status.isSuccess()) {
            val body = try { response.bodyAsText() } catch (_: Throwable) { "" }
            throw BackendError(response.status.value, body)
        }
        return response.body()
    }

    /**
     * Возвращает список коннекторов пользователя.
     * GET /api/v1/connectors с заголовком Authorization: Bearer &lt;token&gt;.
     * Токен получается в сервисе из Firebase Auth.
     */
    suspend fun connectors(): GetConnectorsDto {
        console.log("[BackendApi] connectors() - start")
        val token = requireToken()
        val response = client.get("api/v1/connectors") {
            header("Authorization", "Bearer $token")
        }
        if (!response.status.isSuccess()) {
            val body = try { response.bodyAsText() } catch (_: Throwable) { "" }
            throw BackendError(response.status.value, body)
        }
        val result = response.body<GetConnectorsDto>()
        console.log("[BackendApi] connectors() - done")
        return result
    }

    /**
     * Обновляет коннектор. PATCH /api/v1/connectors/{id}.
     * Для паузы: status = "PAUSED", для возобновления: status = "ACTIVE".
     */
    suspend fun updateConnectorStatus(id: String, status: String) {
        val token = requireToken()
        val response = client.patch("api/v1/connectors/$id") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(UpdateConnectorRequestDto(status = status))
        }
        if (!response.status.isSuccess()) {
            val body = try { response.bodyAsText() } catch (_: Throwable) { "" }
            throw BackendError(response.status.value, body)
        }
    }

    /**
     * Удаляет коннектор по id.
     * DELETE /api/v1/connectors/{id} с заголовком Authorization: Bearer &lt;token&gt;.
     */
    suspend fun deleteConnector(id: String) {
        val token = requireToken()
        val response = client.delete("api/v1/connectors/$id") {
            header("Authorization", "Bearer $token")
        }
        if (!response.status.isSuccess()) {
            val body = try { response.bodyAsText() } catch (_: Throwable) { "" }
            throw BackendError(response.status.value, body)
        }
    }

    /**
     * Создаёт file-коннектор.
     * POST /api/v1/connectors multipart/form-data: name, files.
     * Ответ: CreateConnectorResponseDto.
     */
    suspend fun createFileConnector(
        name: String,
        files: List<ByteArray>,
        filenames: List<String>,
    ): CreateConnectorResponseDto {
        val token = requireToken()
        val form = formData {
            append("name", name)
            files.forEachIndexed { i, bytes ->
                val filename = filenames.getOrElse(i) { "file$i" }
                append("files", bytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                })
            }
        }
        val response = client.submitFormWithBinaryData(form) {
            url {
                appendPathSegments("api", "v1", "connectors")
            }
            header("Authorization", "Bearer $token")
        }
        if (!response.status.isSuccess()) {
            val body = try { response.bodyAsText() } catch (_: Throwable) { "" }
            throw BackendError(response.status.value, body)
        }
        return response.body()
    }

    /**
     * Создаёт URL-коннектор.
     * POST /api/v1/connectors/url с Content-Type: application/json.
     * Ответ: CreateConnectorResponseDto.
     */
    suspend fun createUrlConnector(
        name: String,
        url: String,
        recursive: Boolean = true,
    ): CreateConnectorResponseDto {
        val token = requireToken()
        val requestBody = CreateUrlConnectorRequestDto(name = name, url = url, recursive = recursive)
        val response = client.post("api/v1/connectors/url") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }
        if (!response.status.isSuccess()) {
            val errorBody = try { response.bodyAsText() } catch (_: Throwable) { "" }
            throw BackendError(response.status.value, errorBody)
        }
        return response.body()
    }

    /**
     * Инициализация платежа через Точка Банк.
     * POST /api/v1/pay/tochka/createPayment. Возвращает URL для редиректа пользователя.
     */
    suspend fun createTochkaPayment(planCode: String): TochkaPaymentResponseDto {
        val token = requireToken()
        val response = client.post("api/v1/pay/tochka/createPayment") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(TochkaPaymentRequestDto(planCode = planCode))
        }
        if (!response.status.isSuccess()) {
            val body = try { response.bodyAsText() } catch (_: Throwable) { "" }
            throw BackendError(response.status.value, body)
        }
        return response.body()
    }

    /**
     * Отписаться от подписки Точка Банк.
     * POST /api/v1/pay/tochka/unsubscribe.
     */
    suspend fun unsubscribeTochka() {
        val token = requireToken()
        val response = client.post("api/v1/pay/tochka/unsubscribe") {
            header("Authorization", "Bearer $token")
        }
        if (!response.status.isSuccess()) {
            val body = try { response.bodyAsText() } catch (_: Throwable) { "" }
            throw BackendError(response.status.value, body)
        }
    }
}

/** Ошибка бэкенда с кодом и телом ответа. */
class BackendError(val code: Int, val body: String) : Exception("$code: ${body.ifBlank { "no body" }}")
