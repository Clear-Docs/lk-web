package ru.cleardocs.lkweb.components.sections

import androidx.compose.runtime.Composable
import ru.cleardocs.lkweb.firebase.FirebaseProfile
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text

@Composable
fun ProfileBlock(profile: FirebaseProfile?, onSignOut: () -> Unit) {
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
        profile?.email?.let { SpanText(it, Modifier.color(Colors.Gray)) }
        Spacer()
        Button(onClick = { _ -> onSignOut() }) { Text("Выйти") }
    }
}
