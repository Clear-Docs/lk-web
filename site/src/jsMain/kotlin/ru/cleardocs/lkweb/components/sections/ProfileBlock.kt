package ru.cleardocs.lkweb.components.sections

import androidx.compose.runtime.Composable
import ru.cleardocs.lkweb.api.dto.MeDto
import ru.cleardocs.lkweb.firebase.FirebaseProfile
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Img

@Composable
fun ProfileBlock(
    profile: FirebaseProfile?,
    meDto: MeDto? = null,
) {
    val displayEmail = meDto?.email ?: profile?.email
    Column(
        Modifier.fillMaxWidth().gap(1.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpanText("Вы вошли", Modifier.fontSize(1.1.cssRem))
        profile?.photoUrl?.let { photoUrl ->
            Img(src = photoUrl, alt = "avatar") {
                style {
                    width(4.cssRem)
                    height(4.cssRem)
                    borderRadius(50.percent)
                    property("object-fit", "cover")
                }
            }
        }
        SpanText(profile?.displayName ?: "Без имени")
        displayEmail?.let { SpanText(it, Modifier.color(Colors.Gray)) }
        meDto?.let { dto ->
            if (dto.id.isNotEmpty()) SpanText("ID: ${dto.id}", Modifier.fontSize(0.9.cssRem).color(Colors.Gray))
        }
    }
}
