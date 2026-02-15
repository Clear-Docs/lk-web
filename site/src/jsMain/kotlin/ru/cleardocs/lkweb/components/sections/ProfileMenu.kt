package ru.cleardocs.lkweb.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.varabyte.kobweb.compose.ui.Modifier
import ru.cleardocs.lkweb.di.kodein
import ru.cleardocs.lkweb.pages.MainViewState
import ru.cleardocs.lkweb.pages.MenuViewModel
import org.kodein.di.instance

/**
 * Боковое меню: пункты и текущее состояние берутся из MenuViewModel (DI singleton); внизу кнопка «Выйти».
 *
 * @param onEntrySelected Опционально вызывается после выбора пункта (например, закрыть drawer и перейти на страницу)
 */
@Composable
fun ProfileMenu(
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    onEntrySelected: ((MainViewState) -> Unit)? = null,
) {
    val menuViewModel by kodein.instance<MenuViewModel>()
    val menuEntries = menuViewModel.menuEntries
    val currentState by menuViewModel.stateFlow.collectAsState()

    ProfileMenuCard(
        menuEntries = menuEntries,
        currentState = currentState,
        onEntrySelected = { state ->
            menuViewModel.selectState(state)
            onEntrySelected?.invoke(state)
        },
        onSignOut = onSignOut,
        modifier = modifier,
    )
}
