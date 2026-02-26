package ru.cleardocs.lkweb.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import ru.cleardocs.lkweb.SitePalette
import ru.cleardocs.lkweb.components.layouts.MarkdownStyle
import com.varabyte.kobweb.silk.components.style.toModifier

private val citationRegex = Regex("""\[\[(\d+)\]\]\(\)""")

private sealed class ContentSegment {
    data class Text(val value: String) : ContentSegment()
    data class Citation(val number: Int) : ContentSegment()
}

private const val CITATION_PLACEHOLDER_PREFIX = "___CIT_"
private const val CITATION_PLACEHOLDER_SUFFIX = "___"

private fun parseContent(content: String): List<ContentSegment> {
    if (content.isEmpty()) return listOf(ContentSegment.Text(""))

    val segments = mutableListOf<ContentSegment>()
    var lastEnd = 0

    for (match in citationRegex.findAll(content)) {
        if (match.range.first > lastEnd) {
            segments.add(ContentSegment.Text(content.substring(lastEnd, match.range.first)))
        }
        segments.add(ContentSegment.Citation(match.groupValues[1].toInt()))
        lastEnd = match.range.last + 1
    }
    if (lastEnd < content.length) {
        segments.add(ContentSegment.Text(content.substring(lastEnd)))
    }

    return segments.ifEmpty { listOf(ContentSegment.Text(content)) }
}

private fun escapeHtml(s: String): String = s
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
    .replace("'", "&#39;")

private fun buildCitationBadgeHtml(
    displayName: String,
    documentId: String?,
    palette: SitePalette,
    isDark: Boolean,
): String {
    // Как в Onyx: для file-источников показываем "File", полное имя — в tooltip
    val label = "File"
    val fullTitle = escapeHtml(displayName.ifBlank { "File" })
    val docIdAttr = documentId?.let { """ data-document-id="${escapeHtml(it)}" data-display-name="${escapeHtml(fullTitle)}" """ } ?: ""
    val cursor = if (documentId != null) "cursor:pointer;" else ""
    val bgColor = palette.brand.primary.toString()
    val textColor = "#FFFFFF"
    val shadow = if (isDark) "0 0 0 1px rgba(255,255,255,0.15)" else "0 0 0 1px rgba(59,130,246,0.25)"
    return """<span class="chat-citation-badge"$docIdAttr style="display:inline-flex;align-items:center;margin-left:0.25rem;margin-right:0.15rem;border-radius:0.5rem;padding:0.15rem 0.5rem;font-size:0.75rem;font-weight:500;color:$textColor;background:$bgColor;box-shadow:$shadow;vertical-align:middle;white-space:nowrap;$cursor" title="$fullTitle">$label</span>"""
}

private fun buildFullHtml(
    content: String,
    citations: Map<Int, String>,
    citationDocumentIds: Map<Int, String>,
    palette: SitePalette,
    isDark: Boolean,
): String {
    val segments = parseContent(content)
    val placeholderToBadge = mutableMapOf<String, String>()
    val sb = StringBuilder()
    for (segment in segments) {
        when (segment) {
            is ContentSegment.Text -> sb.append(segment.value)
            is ContentSegment.Citation -> {
                val placeholder = "$CITATION_PLACEHOLDER_PREFIX${segment.number}$CITATION_PLACEHOLDER_SUFFIX"
                placeholderToBadge[placeholder] = buildCitationBadgeHtml(
                    citations[segment.number] ?: "File",
                    citationDocumentIds[segment.number],
                    palette,
                    isDark,
                )
                sb.append(placeholder)
            }
        }
    }
    var html = parseSimpleMarkdown(sb.toString())
    for ((placeholder, badge) in placeholderToBadge) {
        html = html.replace(placeholder, badge)
    }
    return html
}

/**
 * Рендерит содержимое сообщения чата: markdown + инлайн-цитаты как бейджи.
 * При клике на цитату с documentId вызывается [onCitationClick].
 */
@Composable
fun ChatMessageRenderer(
    content: String,
    citations: Map<Int, String>,
    citationDocumentIds: Map<Int, String>,
    palette: SitePalette,
    modifier: Modifier = Modifier,
    onCitationClick: ((documentId: String, displayName: String) -> Unit)? = null,
) {
    val isDark = com.varabyte.kobweb.silk.theme.colors.ColorMode.current == com.varabyte.kobweb.silk.theme.colors.ColorMode.DARK
    val fullHtml = remember(content, citations, citationDocumentIds, palette, isDark) {
        buildFullHtml(content, citations, citationDocumentIds, palette, isDark)
    }
    val ref = remember { mutableStateOf<org.w3c.dom.HTMLDivElement?>(null) }

    DisposableEffect(ref.value, onCitationClick) {
        val el = ref.value
        val callback = onCitationClick
        if (el != null && callback != null) {
            val handler: (Event) -> Unit = { ev ->
                var target: Element? = ev.target?.unsafeCast<Element>()
                while (target != null && target != el) {
                    val docId = target.getAttribute("data-document-id")
                    if (docId != null) {
                        val displayName = target.getAttribute("data-display-name") ?: "File"
                        ev.preventDefault()
                        ev.stopPropagation()
                        callback(docId, displayName)
                        break
                    }
                    target = target.parentElement
                }
            }
            el.asDynamic().addEventListener("click", handler)
            onDispose {
                el.asDynamic().removeEventListener("click", handler)
            }
        } else {
            onDispose { }
        }
    }

    Div(
        modifier
            .fillMaxWidth()
            .then(MarkdownStyle.toModifier())
            .toAttrs {
                ref { el ->
                    ref.value = el
                    onDispose { ref.value = null }
                }
            }
    ) {}
    SideEffect {
        ref.value?.let { it.innerHTML = fullHtml }
    }
}
