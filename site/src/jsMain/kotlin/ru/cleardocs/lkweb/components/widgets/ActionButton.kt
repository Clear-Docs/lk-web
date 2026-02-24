package ru.cleardocs.lkweb.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.text.SpanText
import ru.cleardocs.lkweb.ActionButtonVariant

/**
 * Кнопка действия в стиле «Выйти»: переиспользуемый компонент с текстом и обработчиком клика.
 * Текст выровнен по вертикали, добавлены паддинги start/end.
 */
@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = { onClick() },
        modifier = modifier,
        enabled = enabled,
        variant = ActionButtonVariant
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpanText(text)
        }
    }
}
