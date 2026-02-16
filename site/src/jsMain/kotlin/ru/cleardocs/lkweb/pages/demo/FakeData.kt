package ru.cleardocs.lkweb.pages.demo

import ru.cleardocs.lkweb.api.dto.LimitDto
import ru.cleardocs.lkweb.api.dto.PlanDto
import ru.cleardocs.lkweb.firebase.FirebaseProfile
import ru.cleardocs.lkweb.pages.MainViewState

object FakeData {
    val profileMenuEntries = listOf(
        MainViewState.Profile to "Профиль"
    )

    val profileWithPhoto = FirebaseProfile(
        displayName = "Иван Петров",
        email = "ivan.petrov@example.com",
        photoUrl = "https://ui-avatars.com/api/?name=Ivan+Petrov&size=128"
    )

    val profileWithoutPhoto = FirebaseProfile(
        displayName = "Мария Сидорова",
        email = "maria@example.com",
        photoUrl = null
    )

    val profileMinimal = FirebaseProfile(
        displayName = null,
        email = "user@example.com",
        photoUrl = null
    )

    val plans = listOf(
        PlanDto(
            code = "starter",
            title = "Стартовый",
            priceRub = 990,
            periodDays = 30,
            limit = LimitDto(maxConnectors = 5),
        ),
        PlanDto(
            code = "pro",
            title = "Профессиональный",
            priceRub = 2990,
            periodDays = 30,
            limit = LimitDto(maxConnectors = 25),
        ),
        PlanDto(
            code = "enterprise",
            title = "Корпоративный",
            priceRub = 9990,
            periodDays = 365,
            limit = LimitDto(maxConnectors = 100),
        ),
    )
}
