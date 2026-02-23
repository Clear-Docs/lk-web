package ru.cleardocs.lkweb.components.sections

import androidx.compose.runtime.Composable
import ru.cleardocs.lkweb.api.dto.MeDto
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.cssRem

@Composable
fun ProfileBlock(meDto: MeDto?) {
    if (meDto == null) return
    Column(
        Modifier.fillMaxWidth().gap(1.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpanText("Вы вошли", Modifier.fontSize(1.1.cssRem))
        SpanText(meDto.name ?: "Без имени")
        meDto.email?.let { SpanText(it, Modifier.color(Colors.Gray)) }
        if (meDto.id.isNotEmpty()) SpanText("ID: ${meDto.id}", Modifier.fontSize(0.9.cssRem).color(Colors.Gray))
        meDto.plan?.let { plan ->
            SpanText("Тариф: ${plan.title}", Modifier.fontSize(0.9.cssRem).color(Colors.Gray))
        }
    }
}
