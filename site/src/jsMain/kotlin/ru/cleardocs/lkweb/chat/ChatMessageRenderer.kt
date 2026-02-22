package ru.cleardocs.lkweb.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.dom.Div
import ru.cleardocs.lkweb.SitePalette
import ru.cleardocs.lkweb.components.layouts.MarkdownStyle
import com.varabyte.kobweb.silk.components.style.toModifier

private val citationRegex = Regex("""\[\[(\d+)\]\]\(\)""")

private sealed class ContentSegment {
    data class Text(val value: String) : ContentSegment()
    data class Citation(val number: Int) : ContentSegment()
}

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

private const val MAX_CITATION_LABEL = 40

private fun escapeHtml(s: String): String = s
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
    .replace("'", "&#39;")

private fun buildCitationBadgeHtml(displayName: String, palette: SitePalette, isDark: Boolean): String {
    val raw = displayName.ifBlank { "File" }
    val label = if (raw.length > MAX_CITATION_LABEL) raw.take(MAX_CITATION_LABEL) + "..." else raw
    val fullTitle = escapeHtml(raw)
    val textColor = if (isDark) "#E2E8F0" else "#334155"
    val bgColor = palette.cobweb.toString()
    return """<span class="chat-citation-badge" style="display:inline-flex;align-items:center;margin-left:0.25rem;border-radius:0.4rem;padding:0.2rem 0.5rem;font-size:0.8rem;color:$textColor;background:$bgColor" title="$fullTitle"><img src="/citation.svg" alt="" style="width:0.9rem;height:0.9rem;flex-shrink:0;margin-right:0.25rem"><span>${escapeHtml(label)}</span></span>"""
}

private fun buildFullHtml(content: String, citations: Map<Int, String>, palette: SitePalette, isDark: Boolean): String {
    val segments = parseContent(content)
    return segments.joinToString("") { segment ->
        when (segment) {
            is ContentSegment.Text -> parseSimpleMarkdown(segment.value)
            is ContentSegment.Citation -> buildCitationBadgeHtml(citations[segment.number] ?: "File", palette, isDark)
        }
    }
}

/**
 * Рендерит содержимое сообщения чата: markdown + инлайн-цитаты как бейджи.
 */
@Composable
fun ChatMessageRenderer(
    content: String,
    citations: Map<Int, String>,
    palette: SitePalette,
    modifier: Modifier = Modifier,
) {
    val isDark = com.varabyte.kobweb.silk.theme.colors.ColorMode.current == com.varabyte.kobweb.silk.theme.colors.ColorMode.DARK
    val fullHtml = remember(content, citations, palette, isDark) {
        buildFullHtml(content, citations, palette, isDark)
    }
    val ref = remember { mutableStateOf<org.w3c.dom.HTMLDivElement?>(null) }
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
