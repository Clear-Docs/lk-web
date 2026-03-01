package ru.cleardocs.lkweb.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.cssRem
import ru.cleardocs.lkweb.profile.MeViewModel
import ru.cleardocs.lkweb.profile.ProfileAuthState

@Composable
fun ProfileBlock() {
    val meViewModel = remember { MeViewModel() }
    val authState by meViewModel.authState.collectAsState()
    val me by meViewModel.me.collectAsState()
    val meLoading by meViewModel.loading.collectAsState()
    val meError by meViewModel.error.collectAsState()
    val ctx = rememberPageContext()

    if (authState == ProfileAuthState.Unauthenticated) {
        ctx.router.tryRoutingTo("/auth")
    }

    when (authState) {
        ProfileAuthState.Loading -> SpanText("Проверяем авторизацию...")
        ProfileAuthState.Unauthenticated -> SpanText("Перенаправляем на авторизацию...")
        ProfileAuthState.Authenticated -> when {
            meLoading -> SpanText("Загрузка профиля...")
            meError != null -> SpanText("Ошибка: $meError")
            me != null -> ProfileBlockContent(me!!)
            else -> SpanText("Загрузка профиля...")
        }
    }
}

/**
 * Вариант для демо/тестов — отображает переданные данные без загрузки.
 */
@Composable
fun ProfileBlock(meDto: ru.cleardocs.lkweb.api.dto.MeDto) {
    ProfileBlockContent(meDto)
}

@Composable
private fun ProfileBlockContent(meDto: ru.cleardocs.lkweb.api.dto.MeDto) {
    Column(
        Modifier.fillMaxWidth().gap(1.cssRem),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpanText(if (meDto.name.isEmpty()) "Без имени" else meDto.name)
        SpanText(meDto.email, Modifier.color(Colors.Gray))
        SpanText("Тариф: ${meDto.plan.title}", Modifier.fontSize(0.9.cssRem).color(Colors.Gray))
    }
}
