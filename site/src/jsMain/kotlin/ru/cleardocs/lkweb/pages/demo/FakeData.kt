package ru.cleardocs.lkweb.pages.demo

import ru.cleardocs.lkweb.api.dto.LimitDto
import ru.cleardocs.lkweb.api.dto.MeDto
import ru.cleardocs.lkweb.api.dto.PlanDto
import ru.cleardocs.lkweb.pages.MainViewState
import ru.cleardocs.lkweb.plans.Plan

object FakeData {
    val profileMenuEntries = listOf(
        MainViewState.Profile to "Профиль"
    )

    val meFull = MeDto(
        id = "usr_001",
        email = "ivan.petrov@example.com",
        name = "Иван Петров",
        plan = PlanDto(
            code = "pro",
            title = "Профессиональный",
            priceRub = 2990,
            periodDays = 30,
            limit = LimitDto(maxConnectors = 25),
        ),
    )

    val defaultPlan = PlanDto(
        code = "free",
        title = "Бесплатный",
        priceRub = 0,
        periodDays = 0,
        limit = LimitDto(maxConnectors = 0),
    )

    val meWithoutPlan = MeDto(
        id = "usr_002",
        email = "maria@example.com",
        name = "Мария Сидорова",
        plan = defaultPlan,
    )

    val meMinimal = MeDto(
        id = "usr_003",
        email = "user@example.com",
        name = "",
        plan = defaultPlan,
    )

    val plans = listOf(
        Plan(
            code = "starter",
            title = "Стартовый",
            isActive = false,
            priceRub = 990,
            periodDays = 30,
            limit = LimitDto(maxConnectors = 5),
        ),
        Plan(
            code = "pro",
            title = "Профессиональный",
            isActive = true,
            priceRub = 2990,
            periodDays = 30,
            limit = LimitDto(maxConnectors = 25),
        ),
        Plan(
            code = "enterprise",
            title = "Корпоративный",
            isActive = false,
            priceRub = 9990,
            periodDays = 365,
            limit = LimitDto(maxConnectors = 100),
        ),
    )
}
