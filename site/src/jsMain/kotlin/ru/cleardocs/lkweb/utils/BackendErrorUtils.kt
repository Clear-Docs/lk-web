package ru.cleardocs.lkweb.utils

import ru.cleardocs.lkweb.api.BackendError

/**
 * Преобразует [Throwable] в понятное пользователю сообщение.
 * Учитывает BackendError (401, 403), недоступность сервера, ошибки Точка Банк и т.п.
 */
fun Throwable.toUserFriendlyMessage(defaultMessage: String): String = when {
    this is BackendError -> when (code) {
        401 -> "Сессия истекла"
        403 -> "Доступ запрещён"
        400 -> parseServerErrorBody(code, body) ?: "Неверный запрос"
        else -> parseServerErrorBody(code, body) ?: "Ошибка сервера. Попробуйте позже."
    }
    message?.contains("401") == true -> "Сессия истекла"
    message?.contains("403") == true -> "Доступ запрещён"
    message?.contains("Backend unreachable") == true -> "Сервер недоступен"
    message?.contains("unreachable") == true -> "Сервер недоступен"
    message?.contains("User not authenticated", ignoreCase = true) == true -> "Пользователь не авторизован"
    message?.contains("Subscription not found", ignoreCase = true) == true -> "Подписка не найдена или уже отменена"
    else -> message ?: defaultMessage
}

/**
 * Разбирает тело ответа при 500/424 от бэкенда (в т.ч. обёртки ошибок Точка Банк)
 * и возвращает короткое сообщение для пользователя без сырого JSON.
 */
private fun parseServerErrorBody(code: Int, body: String): String? {
    if (body.isBlank()) return null
    return when {
        body.contains("Document set is still syncing", ignoreCase = true) ||
            body.contains("still syncing from a previous upload", ignoreCase = true) ->
            "Идёт обработка предыдущей загрузки. Подождите 1–2 минуты и попробуйте снова."
        body.contains("duplicate name", ignoreCase = true) ||
            body.contains("already exists", ignoreCase = true) ||
            body.contains("duplicate naming not allowed", ignoreCase = true) ->
            "Коннектор с таким названием уже есть. Укажите другое название."
        body.contains("Subscription not found", ignoreCase = true) ->
            "Подписка не найдена или уже отменена"
        body.contains("Что-то пошло не так") ->
            "Ошибка при отмене подписки. Попробуйте позже."
        code == 400 -> null
        code in 500..599 ->
            "Ошибка сервера. Попробуйте позже."
        else -> null
    }
}

/**
 * Определяет, является ли сообщение об ошибке признаком неавторизованного пользователя.
 */
fun String?.isUnauthError(): Boolean {
    if (this == null) return false
    return contains("401") || contains("403") ||
        contains("Сессия истекла") || contains("Доступ запрещён") ||
        contains("User not authenticated", ignoreCase = true) ||
        contains("не авторизован", ignoreCase = true)
}
