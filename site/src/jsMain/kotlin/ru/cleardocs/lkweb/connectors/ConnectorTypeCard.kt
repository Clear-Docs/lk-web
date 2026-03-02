package ru.cleardocs.lkweb.connectors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.silk.components.overlay.PopupPlacement
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import kotlinx.browser.document
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import ru.cleardocs.lkweb.components.widgets.ActionButton
import ru.cleardocs.lkweb.components.widgets.rememberTimedToast
import ru.cleardocs.lkweb.cardItemBackground
import ru.cleardocs.lkweb.SitePalette
import ru.cleardocs.lkweb.SiteTokens

private val ALL_CONNECTOR_TYPE_CARDS = listOf(
    Triple("/globe-icon.svg", "Web", ConnectorType.Url),
    Triple("/file-icon.svg", "Файл", ConnectorType.File),
    Triple("https://storage.yandexcloud.net/cloud-www-assets/region-assets/ru/light/mobile/logo.svg", "Yandex Cloud", null),
    Triple("/1c.png", "1С", null),
    Triple("/confluence-icon.svg", "Confluence", null),
    Triple("/sharepoint-icon.png", "Sharepoint", null),
    Triple("/googledrive-icon.svg", "Google Drive", null),
    Triple("/jira-icon.svg", "Jira", null),
    Triple("/zendesk-icon.svg", "Zendesk", null),
    Triple("/slack-icon.png", "Slack", null),
    Triple("/notion-icon.png", "Notion", null),
    Triple("/salesforce-icon.png", "Salesforce", null),
    Triple("/hubspot-icon.svg", "HubSpot", null),
    Triple("/github-icon.png", "Github", null),
    Triple("/googlesites-icon.png", "Google Sites", null),
    Triple("/fireflies-icon.png", "Fireflies", null),
    Triple("/highspot-icon.png", "Highspot", null),
    Triple("/loopio-icon.png", "Loopio", null),
    Triple("/zulip-icon.png", "Zulip", null),
    Triple("/teams-icon.png", "Microsoft Teams", null),
    Triple("/discord-icon.png", "Discord", null),
    Triple("/gmail-icon.png", "Gmail", null),
    Triple("/bitbucket-icon.svg", "Bitbucket", null),
    Triple("/oci-icon.svg", "OCI", null),
    Triple("/dropbox-icon.svg", "Dropbox", null),
    Triple("/s3-icon.png", "S3", null),
    Triple("/r2-icon.png", "R2", null),
    Triple("/xenforo-icon.svg", "XenForo", null),
    Triple("/wikipedia-icon.png", "Wikipedia", null),
)

@Composable
fun ConnectorTypeCard(
    iconSrc: String,
    label: String,
    palette: SitePalette,
    onClick: () -> Unit,
    enabled: Boolean = true,
    disabledMessage: String = "В разработке",
    showToast: (String) -> Unit = {},
    hint: String? = null,
) {
    val textColor = ColorMode.current.toPalette().color
    val cardBg = ColorMode.current.cardItemBackground(palette).toString()
    val isHighlighted = iconSrc == "/globe-icon.svg" || iconSrc == "/file-icon.svg"
    val brandColor = palette.brand.primary.toString()
    val shadowColor = palette.brand.primary.toRgb().copyf(alpha = 0.12f).toString()
    Box {
    Div(
        attrs = {
            if (isHighlighted && enabled) classes("connector-featured-card")
            style {
                property("cursor", if (enabled) "pointer" else "default")
                property("display", "flex")
                property("flex-direction", "column")
                property("align-items", "center")
                property("justify-content", "center")
                property("gap", "0.75rem")
                property("padding", "1.25rem")
                property("width", "10rem")
                property("min-height", "6rem")
                property("border-radius", "0.75rem")
                property("background", cardBg)
                property("box-shadow", "2px 2px 8px $shadowColor")
                property("transition", "transform 0.2s ease, box-shadow 0.2s ease")
                if (isHighlighted && enabled) {
                    property("--connector-glow-color", palette.brand.primary.toRgb().copyf(alpha = 0.4f).toString())
                    property("--connector-base-shadow", "2px 2px 8px $shadowColor")
                    property("animation", "connector-card-glow 2.5s ease-in-out infinite")
                    if (iconSrc == "/file-icon.svg") property("animation-delay", "0.6s")
                }
                if (!enabled) property("opacity", "0.6")
            }
            onClick {
                if (enabled) onClick()
                else showToast(disabledMessage)
            }
        }
    ) {
        if (isHighlighted) {
            Div(
                attrs = {
                    style {
                        property("width", "2rem")
                        property("height", "2rem")
                        property("background-color", brandColor)
                        property("mask", "url($iconSrc) no-repeat center")
                        property("mask-size", "contain")
                        property("-webkit-mask", "url($iconSrc) no-repeat center")
                        property("-webkit-mask-size", "contain")
                        property("--connector-highlight-color", brandColor)
                        property("animation", "connector-icon-bounce 2s ease-in-out infinite")
                        if (iconSrc == "/file-icon.svg") property("animation-delay", "0.6s")
                    }
                }
            )
        } else {
            Img(
                src = iconSrc,
                alt = label,
                attrs = {
                    style {
                        property("width", "2rem")
                        property("height", "2rem")
                        property("object-fit", "contain")
                    }
                }
            )
        }
        SpanText(label, Modifier.color(textColor.toRgb()).fontSize(1.cssRem))
    }
        if (hint != null) {
            Tooltip(ElementTarget.PreviousSibling, hint, placement = PopupPlacement.Bottom)
        }
    }
}

private const val CONNECTOR_HIGHLIGHT_KEYFRAMES_ID = "connector-highlight-icon-keyframes"

@Composable
fun ConnectorTypeCardsRow(
    palette: SitePalette,
    canAdd: Boolean,
    selectedType: ConnectorType?,
    onSelectType: (ConnectorType?) -> Unit,
    connectorsViewModel: ConnectorsViewModel,
) {
    val showToast = rememberTimedToast()

    DisposableEffect(Unit) {
        if (document.getElementById(CONNECTOR_HIGHLIGHT_KEYFRAMES_ID) == null) {
            val style = document.createElement("style").unsafeCast<org.w3c.dom.HTMLStyleElement>()
            style.id = CONNECTOR_HIGHLIGHT_KEYFRAMES_ID
            style.appendChild(
                document.createTextNode(
                    """
                    @keyframes connector-icon-bounce {
                        0%, 100% {
                            transform: scale(1) translateY(0);
                            filter: drop-shadow(0 0 6px var(--connector-highlight-color));
                        }
                        25% {
                            transform: scale(1.1) translateY(-4px);
                            filter: drop-shadow(0 0 14px var(--connector-highlight-color)) drop-shadow(0 0 20px var(--connector-highlight-color));
                        }
                        50% {
                            transform: scale(1.05) translateY(-2px);
                            filter: drop-shadow(0 0 18px var(--connector-highlight-color));
                        }
                        75% {
                            transform: scale(1.08) translateY(-3px);
                            filter: drop-shadow(0 0 12px var(--connector-highlight-color));
                        }
                    }
                    .connector-featured-card:hover {
                        transform: scale(1.05);
                        box-shadow: var(--connector-base-shadow), 0 0 35px var(--connector-glow-color) !important;
                    }
                    @keyframes connector-card-glow {
                        0%, 100% {
                            box-shadow: var(--connector-base-shadow), 0 0 18px var(--connector-glow-color);
                        }
                        50% {
                            box-shadow: var(--connector-base-shadow), 0 0 32px var(--connector-glow-color);
                        }
                    }
                    """.trimIndent()
                )
            )
            document.head?.appendChild(style)
        }
        onDispose { }
    }

    Column(Modifier.fillMaxWidth().gap(SiteTokens.Spacing.lg)) {
        when (selectedType) {
            null -> {
                Div(
                    attrs = {
                        style {
                            property("display", "flex")
                            property("flex-wrap", "wrap")
                            property("gap", "1rem")
                        }
                    }
                ) {
                    ALL_CONNECTOR_TYPE_CARDS.forEach { (iconSrc, label, type) ->
                        ConnectorTypeCard(
                            iconSrc = iconSrc,
                            label = label,
                            palette = palette,
                            onClick = {
                                if (type != null && canAdd) onSelectType(type)
                            },
                            enabled = type != null && canAdd,
                            disabledMessage = when {
                                type == null -> "В разработке"
                                !canAdd -> "Измените тариф"
                                else -> "В разработке"
                            },
                            showToast = showToast,
                            hint = when (type) {
                                ConnectorType.Url -> "Подключите любой сайт рекурсивно, укажите url сайта."
                                ConnectorType.File -> "Загрузите файлы, выберите путь."
                                else -> null
                            }
                        )
                    }
                }
            }
            ConnectorType.File -> {
                ActionButton(text = "Назад", onClick = { onSelectType(null) }, enabled = true)
                AddFileConnectorForm(
                    connectorsViewModel = connectorsViewModel,
                    palette = palette
                )
            }
            ConnectorType.Url -> {
                ActionButton(text = "Назад", onClick = { onSelectType(null) }, enabled = true)
                AddUrlConnectorForm(
                    connectorsViewModel = connectorsViewModel,
                    palette = palette
                )
            }
            ConnectorType.OneC -> { /* неактивен */ }
        }
    }
}
