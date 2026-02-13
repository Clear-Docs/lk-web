package ru.cleardocs.lkweb

import org.jetbrains.compose.web.css.LineStyle
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.forms.ButtonVars
import com.varabyte.kobweb.silk.components.layout.HorizontalDividerStyle
import com.varabyte.kobweb.silk.components.style.ComponentStyle
import com.varabyte.kobweb.silk.components.style.addVariantBase
import com.varabyte.kobweb.silk.components.style.base
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color
import com.varabyte.kobweb.silk.theme.colors.palette.toPalette
import com.example.testKotlin.toSitePalette
import com.varabyte.kobweb.silk.theme.modifyComponentStyleBase
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

@InitSilk
fun initSiteStyles(ctx: InitSilkContext) {
    ctx.stylesheet.registerStyleBase("body") {
        Modifier
            .fontFamily(
                "Inter", "system-ui", "-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto",
                "Oxygen", "Ubuntu", "Cantarell", "sans-serif"
            )
            .fontSize(18.px)
            .lineHeight(1.5)
    }

    // Silk dividers only extend 90% by default; we want full width dividers in our site
    ctx.theme.modifyComponentStyleBase(HorizontalDividerStyle) {
        Modifier.fillMaxWidth()
    }
}

val HeadlineTextStyle by ComponentStyle.base {
    Modifier
        .fontSize(2.75.cssRem)
        .fontWeight(700)
        .textAlign(TextAlign.Start)
        .lineHeight(1.2)
}

val SubheadlineTextStyle by ComponentStyle.base {
    Modifier
        .fontSize(1.1.cssRem)
        .textAlign(TextAlign.Start)
        .color(colorMode.toPalette().color.toRgb().copyf(alpha = 0.7f))
}

val CircleButtonVariant by ButtonStyle.addVariantBase {
    Modifier.padding(0.px).borderRadius(50.percent)
}

val UncoloredButtonVariant by ButtonStyle.addVariantBase {
    Modifier.setVariable(ButtonVars.BackgroundDefaultColor, Colors.Transparent)
}

// Auth page button variants (must be public for KSP auto-registration)
val AuthTabActiveVariant by ButtonStyle.addVariantBase {
    val sitePalette = colorMode.toSitePalette()
    val palette = colorMode.toPalette()
    Modifier
        .setVariable(ButtonVars.BackgroundDefaultColor, sitePalette.brand.primary)
        .color(palette.background)
        .borderRadius(0.75.cssRem)
}

val AuthTabInactiveVariant by ButtonStyle.addVariantBase {
    val sitePalette = colorMode.toSitePalette()
    val palette = colorMode.toPalette()
    Modifier
        .setVariable(ButtonVars.BackgroundDefaultColor, sitePalette.nearBackground)
        .color(palette.color)
        .borderRadius(0.75.cssRem)
}

val AuthPrimaryButtonVariant by ButtonStyle.addVariantBase {
    val sitePalette = colorMode.toSitePalette()
    val palette = colorMode.toPalette()
    Modifier
        .setVariable(ButtonVars.BackgroundDefaultColor, sitePalette.brand.primary)
        .color(palette.background)
        .fontWeight(600)
        .borderRadius(0.75.cssRem)
}

val AuthGoogleButtonVariant by ButtonStyle.addVariantBase {
    val palette = colorMode.toPalette()
    Modifier
        .setVariable(ButtonVars.BackgroundDefaultColor, Colors.Transparent)
        .color(palette.color)
        .fontWeight(600)
        .borderRadius(0.75.cssRem)
        .border(1.px, LineStyle.Solid, palette.color)
}

val AuthToggleButtonVariant by ButtonStyle.addVariantBase {
    val palette = colorMode.toPalette()
    Modifier
        .setVariable(ButtonVars.BackgroundDefaultColor, palette.background)
        .color(palette.color)
}