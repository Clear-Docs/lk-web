package ru.cleardocs.lkweb.connectors

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import org.w3c.files.File

/**
 * Конвертирует [File] в [ByteArray] через arrayBuffer().
 * Для использования при загрузке файлов в createFileConnector.
 */
suspend fun File.toByteArray(): ByteArray {
    val promise = asDynamic().arrayBuffer().unsafeCast<kotlin.js.Promise<Any?>>()
    val ab = suspendCoroutine { cont ->
        promise.then(
            { result: Any? -> cont.resume(result!!) },
            { err: Any? -> cont.resumeWithException((err ?: js("new Error('Unknown')")).unsafeCast<Throwable>()) }
        )
    }
    val toByteArray = js("(function(buf) { return new Int8Array(buf); })").unsafeCast<(dynamic) -> ByteArray>()
    return toByteArray(ab)
}
