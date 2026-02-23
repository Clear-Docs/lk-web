package ru.cleardocs.lkweb.chat

/**
 * Преобразует простой markdown в HTML для отображения в чате.
 * Поддерживает: **bold**, нумерованные списки, маркированные списки, переносы строк.
 */
fun parseSimpleMarkdown(text: String): String {
    if (text.isEmpty()) return ""

    var result = text

    // Экранируем HTML-сущности чтобы избежать XSS
    result = result
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")

    // **bold** -> <strong>bold</strong>
    result = Regex("""\*\*(.+?)\*\*""").replace(result) { match ->
        "<strong>${match.groupValues[1]}</strong>"
    }

    // Обрабатываем списки построчно
    val lines = result.split("\n")
    val sb = StringBuilder()
    var inOrderedList = false
    var inUnorderedList = false

    val orderedItemRegex = Regex("""^(\d+)[.)]\s+(.+)$""")
    val unorderedItemRegex = Regex("""^-\s+(.+)$""")

    for (line in lines) {
        val orderedMatch = orderedItemRegex.matchEntire(line)
        val unorderedMatch = unorderedItemRegex.matchEntire(line)

        when {
            orderedMatch != null -> {
                if (!inOrderedList) {
                    if (inUnorderedList) {
                        sb.append("</ul>")
                        inUnorderedList = false
                    }
                    if (sb.isNotEmpty()) sb.append("<br/>")
                    sb.append("<ol>")
                    inOrderedList = true
                }
                sb.append("<li>").append(orderedMatch.groupValues[2]).append("</li>")
            }
            unorderedMatch != null -> {
                if (!inUnorderedList) {
                    if (inOrderedList) {
                        sb.append("</ol>")
                        inOrderedList = false
                    }
                    if (sb.isNotEmpty()) sb.append("<br/>")
                    sb.append("<ul>")
                    inUnorderedList = true
                }
                sb.append("<li>").append(unorderedMatch.groupValues[1]).append("</li>")
            }
            else -> {
                if (inOrderedList) {
                    sb.append("</ol>")
                    inOrderedList = false
                }
                if (inUnorderedList) {
                    sb.append("</ul>")
                    inUnorderedList = false
                }
                if (sb.isNotEmpty()) sb.append("<br/>")
                sb.append(line)
            }
        }
    }

    if (inOrderedList) sb.append("</ol>")
    if (inUnorderedList) sb.append("</ul>")

    result = sb.toString()
    // Компактные отступы для чата — inline (без ComponentStyle, который ломал клики)
    result = result
        .replace("<ol>", "<ol style=\"margin:0.15rem 0 0.2rem 0\">")
        .replace("<ul>", "<ul style=\"margin:0.15rem 0 0.2rem 0\">")

    return "<p style=\"margin:0.15rem 0 0.3rem 0\">$result</p>"
}
