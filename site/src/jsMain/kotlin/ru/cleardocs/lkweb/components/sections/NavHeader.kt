package ru.cleardocs.lkweb.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.dom.ElementTarget
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonVars
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.components.icons.MoonIcon
import com.varabyte.kobweb.silk.components.icons.SunIcon
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.components.layout.breakpoint.displayUntil
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.overlay.PopupPlacement
import com.varabyte.kobweb.silk.components.overlay.Tooltip
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import kotlinx.coroutines.launch
import com.varabyte.kobweb.compose.ui.graphics.Colors
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.css.*
import ru.cleardocs.lkweb.CircleButtonVariant
import ru.cleardocs.lkweb.UncoloredButtonVariant
import ru.cleardocs.lkweb.firebase.FirebaseProvider
import ru.cleardocs.lkweb.firebase.signOut
import ru.cleardocs.lkweb.toSitePalette
import ru.cleardocs.lkweb.components.sections.ProfileMenu

val NavHeaderStyle by ComponentStyle.base {
    val palette = colorMode.toSitePalette()
    Modifier
        .fillMaxWidth()
        .padding(1.cssRem)
        .backgroundColor(palette.nearBackground)
}

val NavLinkStyle by ComponentStyle.base {
    Modifier
}

val DrawerBackdropStyle by ComponentStyle.base {
    Modifier
        .fillMaxSize()
        .position(Position.Fixed)
        .left(0.px)
        .top(0.px)
        .zIndex(100)
}

val DrawerPanelStyle by ComponentStyle.base {
    val palette = colorMode.toSitePalette()
    Modifier
        .position(Position.Fixed)
        .left(0.px)
        .top(0.px)
        .bottom(0.px)
        .width(17.cssRem)
        .zIndex(101)
        .backgroundColor(palette.nearBackground)
        .boxShadow(4.px, 4.px, 8.px, color = Colors.Black.toRgb().copyf(alpha = 0.15f))
        .padding(1.5.cssRem)
}

@Composable
private fun BurgerIcon() {
    val palette = ColorMode.current.toSitePalette()
    Column(
        Modifier
            .width(1.25.cssRem)
            .height(1.cssRem)
            .gap(0.2.cssRem),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(3) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(0.15.cssRem)
                    .backgroundColor(palette.brand.primary)
                    .borderRadius(2.px)
            )
        }
    }
}

val ClearDocsLogoStyle by ComponentStyle.base {
    val palette = colorMode.toSitePalette()
    Modifier
        .fontSize(1.6.cssRem)
        .fontWeight(500)
        .setVariable(com.varabyte.kobweb.silk.components.style.vars.color.ColorVar, palette.brand.primary)
}

@Composable
private fun ColorModeButton() {
    var colorMode by ColorMode.currentState
    Button(
        onClick = { colorMode = colorMode.opposite },
        Modifier.setVariable(ButtonVars.FontSize, 1.em),
        variant = CircleButtonVariant.then(UncoloredButtonVariant)
    ) {
        if (colorMode.isLight) MoonIcon() else SunIcon()
    }
    Tooltip(ElementTarget.PreviousSibling, "Toggle color mode", placement = PopupPlacement.BottomRight)
}

@Composable
fun NavHeader() {
    var drawerOpen by remember { mutableStateOf(false) }
    val ctx = rememberPageContext()
    val scope = rememberCoroutineScope()
    val repository = FirebaseProvider.repository

    val onSignOut: () -> Unit = {
        scope.launch {
            signOut(repository.auth)
            ctx.router.tryRoutingTo("/auth")
        }
        Unit
    }

    Box(Modifier.fillMaxWidth()) {
        Row(NavHeaderStyle.toModifier(), verticalAlignment = Alignment.CenterVertically) {
            Spacer()
            Link("/", modifier = ClearDocsLogoStyle.toModifier(), variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)) {
                SpanText("ClearDocs")
            }
            Spacer()
            Row(Modifier.gap(1.5.cssRem).displayIfAtLeast(Breakpoint.MD), verticalAlignment = Alignment.CenterVertically) {
                Link(
                    "/profile",
                    modifier = Modifier,
                    variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
                ) {
                    SpanText("Профиль")
                }
                Button(
                    onClick = { onSignOut() },
                    modifier = Modifier,
                    variant = UncoloredButtonVariant
                ) {
                    Text("Выйти")
                }
                ColorModeButton()
            }

            Row(
                Modifier.gap(1.cssRem).displayUntil(Breakpoint.MD),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { drawerOpen = true },
                    Modifier.setVariable(ButtonVars.FontSize, 1.em),
                    variant = CircleButtonVariant.then(UncoloredButtonVariant)
                ) {
                    BurgerIcon()
                }
                Tooltip(ElementTarget.PreviousSibling, "Меню", placement = PopupPlacement.BottomRight)
                ColorModeButton()
            }
        }

        if (drawerOpen) {
            Box(
                DrawerBackdropStyle.toModifier()
                    .displayUntil(Breakpoint.MD)
                    .backgroundColor(Colors.Black.toRgb().copyf(alpha = 0.4f))
                    .onClick { drawerOpen = false },
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    DrawerPanelStyle.toModifier()
                        .displayUntil(Breakpoint.MD)
                        .gap(0.5.cssRem)
                ) {
                    Button(
                        onClick = { drawerOpen = false },
                        Modifier.setVariable(ButtonVars.FontSize, 1.em),
                        variant = CircleButtonVariant.then(UncoloredButtonVariant)
                    ) {
                        SpanText("×", Modifier.fontSize(1.5.cssRem))
                    }
                    ProfileMenu(
                        onSignOut = onSignOut,
                        onEntrySelected = {
                            drawerOpen = false
                            ctx.router.tryRoutingTo("/profile")
                        }
                    )
                }
            }
        }
    }
}
