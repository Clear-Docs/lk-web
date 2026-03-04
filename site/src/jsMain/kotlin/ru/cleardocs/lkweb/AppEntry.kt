package ru.cleardocs.lkweb

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ru.cleardocs.lkweb.firebase.firebaseLog
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.scrollBehavior
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.components.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import kotlinx.browser.document
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.css.vh
import org.w3c.dom.HTMLScriptElement

private const val COLOR_MODE_KEY = "clearDocs:colorMode"
private const val JIVO_WIDGET_ID = "jivo-widget"

@Composable
private fun RoutePathLogger() {
    var lastPath by remember { mutableStateOf<String?>(null) }
    SideEffect {
        val path = js("window.location.pathname") as String
        if (path != lastPath) {
            firebaseLog("Route", "path changed", "from=", lastPath, "to=", path)
            lastPath = path
        }
    }
}

@InitSilk
fun initColorMode(ctx: InitSilkContext) {
    ctx.config.initialColorMode = localStorage.getItem(COLOR_MODE_KEY)?.let { ColorMode.valueOf(it) } ?: ColorMode.LIGHT
}

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SideEffect {
        if (document.getElementById(JIVO_WIDGET_ID) == null) {
            val script = document.createElement("script").unsafeCast<HTMLScriptElement>()
            script.id = JIVO_WIDGET_ID
            script.src = "https://code.jivo.ru/widget/pRFQhqJ7tP"
            script.async = true
            document.head?.appendChild(script)
        }
    }

    SilkApp {
        DisposableEffect(Unit) {
            firebaseLog("App", "mounted", "path=", js("window.location.pathname"))
            val handler: (dynamic) -> Unit = {
                firebaseLog("App", "visibilityChange", "hidden=", document.asDynamic().hidden)
            }
            document.addEventListener("visibilitychange", handler)
            onDispose {
                document.removeEventListener("visibilitychange", handler)
                firebaseLog("App", "disposed")
            }
        }

        val colorMode = ColorMode.current
        SideEffect {
            localStorage.setItem(COLOR_MODE_KEY, colorMode.name)
        }

        Surface(
            SmoothColorStyle.toModifier()
                .fillMaxWidth()
                .minHeight(100.vh)
                .scrollBehavior(ScrollBehavior.Smooth)
        ) {
            RoutePathLogger()
            content()
        }
    }
}