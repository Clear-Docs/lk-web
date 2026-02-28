package ru.cleardocs.lkweb.connectors

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import com.varabyte.kobweb.silk.components.text.SpanText
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
    Triple("https://cdn.simpleicons.org/confluence/172B4D", "Confluence", null),
    Triple("/sharepoint-icon.png", "Sharepoint", null),
    Triple("https://cdn.simpleicons.org/googledrive/4285F4", "Google Drive", null),
    Triple("https://cdn.simpleicons.org/jira/0052CC", "Jira", null),
    Triple("https://cdn.simpleicons.org/zendesk/03363D", "Zendesk", null),
    Triple("/slack-icon.png", "Slack", null),
    Triple("/notion-icon.png", "Notion", null),
    Triple("/salesforce-icon.png", "Salesforce", null),
    Triple("https://cdn.simpleicons.org/hubspot/FF7A59", "HubSpot", null),
    Triple("/github-icon.png", "Github", null),
    Triple("/googlesites-icon.png", "Google Sites", null),
    Triple("/fireflies-icon.png", "Fireflies", null),
    Triple("/highspot-icon.png", "Highspot", null),
    Triple("/loopio-icon.png", "Loopio", null),
    Triple("/zulip-icon.png", "Zulip", null),
    Triple("/teams-icon.png", "Microsoft Teams", null),
    Triple("/discord-icon.png", "Discord", null),
    Triple("/gmail-icon.png", "Gmail", null),
    Triple("https://cdn.simpleicons.org/bitbucket/0052CC", "Bitbucket", null),
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
) {
    val textColor = ColorMode.current.toPalette().color
    val cardBg = ColorMode.current.cardItemBackground(palette).toString()
    Div(
        attrs = {
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
                property("box-shadow", "2px 2px 8px ${palette.brand.primary.toRgb().copyf(alpha = 0.12f).toString()}")
                property("transition", "box-shadow 0.2s ease")
                if (!enabled) property("opacity", "0.6")
            }
            onClick {
                if (enabled) onClick()
                else showToast(disabledMessage)
            }
        }
    ) {
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
        SpanText(label, Modifier.color(textColor.toRgb()).fontSize(1.cssRem))
    }
}

@Composable
fun ConnectorTypeCardsRow(
    palette: SitePalette,
    canAdd: Boolean,
    selectedType: ConnectorType?,
    onSelectType: (ConnectorType?) -> Unit,
    connectorsViewModel: ConnectorsViewModel,
) {
    val showToast = rememberTimedToast()
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
                            showToast = showToast
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
