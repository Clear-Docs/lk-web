package ru.cleardocs.lkweb.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.TextArea
import org.w3c.dom.HTMLTextAreaElement
import ru.cleardocs.lkweb.AuthToggleButtonVariant

private fun resizeTextarea(ta: HTMLTextAreaElement) {
    ta.style.height = "0px"
    ta.style.height = (ta.scrollHeight.coerceAtLeast(40)).toString() + "px"
}

/**
 * Shared input style for auth forms (border, padding, background, color).
 */
internal fun authInputStyle(
    inputBg: String,
    inputFg: String,
    inputBorder: String,
    marginBottom: String? = null
): org.jetbrains.compose.web.css.StyleScope.() -> Unit = {
    property("width", "100%")
    property("padding", "0.7rem")
    marginBottom?.let { property("margin-bottom", it) }
    property("border-radius", "0.6rem")
    property("border", "1px solid $inputBorder")
    property("outline", "none")
    property("background", inputBg)
    property("color", inputFg)
}

internal fun expandableInputStyle(
    inputBg: String,
    inputFg: String,
    inputBorder: String,
    marginBottom: String? = null
): org.jetbrains.compose.web.css.StyleScope.() -> Unit = {
    property("width", "100%")
    property("min-height", "2.5rem")
    property("max-height", "12rem")
    property("overflow", "hidden")
    property("padding", "0.7rem")
    property("resize", "none")
    marginBottom?.let { property("margin-bottom", it) }
    property("border-radius", "0.6rem")
    property("border", "1px solid $inputBorder")
    property("outline", "none")
    property("background", inputBg)
    property("color", inputFg)
}


@Composable
fun ExpandableChatInput(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    inputBg: String,
    inputFg: String,
    inputBorder: String,
    enabled: Boolean = true,
    marginBottom: String? = null,
) {
    val elementRef = remember { mutableStateOf<HTMLTextAreaElement?>(null) }
    SideEffect {
        elementRef.value?.let { resizeTextarea(it) }
    }
    TextArea(
        attrs = {
            value(value)
            placeholder(placeholder)
            if (!enabled) disabled()
            rows(1)
            ref { el ->
                elementRef.value = el.unsafeCast<HTMLTextAreaElement>()
                resizeTextarea(el.unsafeCast<HTMLTextAreaElement>())
                onDispose { elementRef.value = null }
            }
            onInput { e ->
                val target = e.target.unsafeCast<HTMLTextAreaElement>()
                onValueChange(target.value)
                resizeTextarea(target)
            }
            style(expandableInputStyle(inputBg, inputFg, inputBorder, marginBottom))
        }
    )
}

@Composable
fun AuthInput(
    type: InputType<*>,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    inputBg: String,
    inputFg: String,
    inputBorder: String,
    enabled: Boolean = true,
    marginBottom: String? = "0.75rem",
) {
    Input(
        type = type,
        attrs = {
            value(value)
            placeholder(placeholder)
            if (!enabled) disabled()
            onInput { onValueChange((it.value as? String) ?: "") }
            style(authInputStyle(inputBg, inputFg, inputBorder, marginBottom))
        }
    )
}

@Composable
fun PasswordFieldWithToggle(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onToggleVisibility: () -> Unit,
    inputBg: String,
    inputFg: String,
    inputBorder: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .gap(0.5.cssRem)
            .margin(bottom = 0.75.cssRem),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.fillMaxWidth().flexGrow(1)) {
            AuthInput(
                type = if (isPasswordVisible) InputType.Text else InputType.Password,
                value = value,
                placeholder = placeholder,
                onValueChange = onValueChange,
                inputBg = inputBg,
                inputFg = inputFg,
                inputBorder = inputBorder,
                enabled = enabled,
                marginBottom = null
            )
        }
        Button(
            onClick = { _ -> onToggleVisibility() },
            modifier = Modifier.padding(0.7.cssRem, 0.85.cssRem).borderRadius(0.6.cssRem),
            variant = AuthToggleButtonVariant,
            enabled = enabled
        ) {
            SpanText(if (isPasswordVisible) "Скрыть" else "Показать")
        }
    }
}
