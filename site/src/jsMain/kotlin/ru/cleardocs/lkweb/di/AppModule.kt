package ru.cleardocs.lkweb.di

import org.kodein.di.DI
import org.kodein.di.bindSingleton
import ru.cleardocs.lkweb.pages.MenuViewModel

val appModule = DI.Module("app") {
    bindSingleton { MenuViewModel() }
}

/** Глобальный контейнер Kodein-DI. */
val kodein = DI {
    import(appModule)
}
