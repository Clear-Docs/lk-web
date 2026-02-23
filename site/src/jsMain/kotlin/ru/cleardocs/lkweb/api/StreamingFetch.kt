package ru.cleardocs.lkweb.api

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.json

/**
 * Fetch с потоковой отдачей тела ответа (ReadableStream).
 * Используется для SSE/NDJSON ответов Onyx Chat API.
 */
internal fun fetchStream(
    url: String,
    method: String = "POST",
    headers: Map<String, String>,
    body: String,
): Flow<String> = callbackFlow {
    val headersObj = headers.entries.map { it.key to it.value }
        .let { pairs -> if (pairs.isEmpty()) json() else json(*pairs.toTypedArray()) }
    val init = json(
        "method" to method,
        "headers" to headersObj,
        "body" to body
    )
    val fetchPromise = js("fetch")(url, init)
    val response = suspendCoroutine { cont ->
        fetchPromise.unsafeCast<dynamic>().then(
            { r: dynamic -> cont.resume(r) },
            { e: dynamic -> cont.resumeWithException((e ?: js("new Error('Fetch failed')")).unsafeCast<Throwable>()) }
        )
    }
    if (!response.ok) {
        val errText = suspendCoroutine { c ->
            response.text().unsafeCast<dynamic>().then(
                { result: dynamic -> c.resume(result.unsafeCast<String>()) },
                { e: dynamic -> c.resumeWithException((e ?: js("new Error()")).unsafeCast<Throwable>()) }
            )
        }
        close(Throwable("HTTP ${response.status}: $errText"))
        awaitClose { }
        return@callbackFlow
    }
    val reader = response.body.getReader()
    val decoder = js("new TextDecoder()")
    var buffer = ""
    while (true) {
        val readResult = suspendCoroutine { cont ->
            reader.read().unsafeCast<dynamic>().then(
                { r: dynamic -> cont.resume(r) },
                { e: dynamic -> cont.resumeWithException((e ?: js("new Error('Read failed')")).unsafeCast<Throwable>()) }
            )
        }
        if (readResult.done.unsafeCast<Boolean>()) break
        val chunk = decoder.decode(readResult.value).unsafeCast<String>()
        buffer += chunk
        val lines = buffer.split("\n")
        buffer = lines.last()
        for (i in 0 until lines.size - 1) {
            val line = lines[i].trim()
            if (line.isNotEmpty()) trySend(line)
        }
    }
    if (buffer.isNotBlank()) trySend(buffer)
    close()
    awaitClose { }
}
