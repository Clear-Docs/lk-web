package com.cleardocs.web

import com.cleardocs.web.pages.IndexPage
import org.jetbrains.compose.web.renderComposable

/**
 * Точка входа для Compose for Web.
 * Пока проект не настроен как полноценный Kobweb-приложение,
 * самостоятельно монтируем корневую страницу.
 */
fun main() {
    renderComposable(rootElementId = "root") {
        IndexPage()
    }
}
