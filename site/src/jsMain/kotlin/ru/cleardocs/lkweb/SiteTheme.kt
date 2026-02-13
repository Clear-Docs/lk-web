package ru.cleardocs.lkweb

import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color

class SitePalette(
    val nearBackground: Color,
    val cobweb: Color,
    val brand: Brand,
) {
    class Brand(val primary: Color, val accent: Color)
}

object SitePalettes {
    val light = SitePalette(
        nearBackground = Color.rgb(0xF8FAFC),
        cobweb = Color.rgb(0xE2E8F0),
        // Primary brand blue (slightly more "sky/голубой" than before)
        brand = SitePalette.Brand(Color.rgb(0x3B82F6), Color.rgb(0x60A5FA)),
    )
    val dark = SitePalette(
        nearBackground = Color.rgb(0x13171F),
        cobweb = Colors.LightGray.inverted(),
        // Keep primary consistent between light/dark for brand recognition
        brand = SitePalette.Brand(Color.rgb(0x3B82F6), Color.rgb(0xF3DB5B)),
    )
}

fun ColorMode.toSitePalette(): SitePalette = when (this) {
    ColorMode.LIGHT -> SitePalettes.light
    ColorMode.DARK -> SitePalettes.dark
}

@InitSilk
fun initTheme(ctx: InitSilkContext) {
    with(ctx.theme.palettes) {
        light.apply {
            background = Color.rgb(0xFFFFFF)
            color = Color.rgb(0x0F172A)
        }
        dark.apply {
            background = Color.rgb(0x0B1120)
            color = Colors.White
        }
    }
}
