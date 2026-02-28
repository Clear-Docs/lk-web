package ru.cleardocs.lkweb.utils

import ru.cleardocs.lkweb.api.BackendError

/**
 * Преобразует [Throwable] в понятное пользователю сообщение.
 * Учитывает BackendError (401, 403), недоступность сервера и т.п.
 */
fun Throwable.toUserFriendlyMessage(defaultMessage: String): String = when {
    this is BackendError -> when (code) {
        401 -> "Сессия истекла"
        403 -> "Доступ запрещён"
        else -> message ?: "Ошибка $code"
    }
    message?.contains("401") == true -> "Сессия истекла"
    message?.contains("403") == true -> "Доступ запрещён"
    message?.contains("Backend unreachable") == true -> "Сервер недоступен"
    message?.contains("unreachable") == true -> "Сервер недоступен"
    message?.contains("User not authenticated", ignoreCase = true) == true -> "Пользователь не авторизован"
    else -> message ?: defaultMessage
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
