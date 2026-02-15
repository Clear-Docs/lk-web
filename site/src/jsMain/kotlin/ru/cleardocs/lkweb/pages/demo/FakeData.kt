package ru.cleardocs.lkweb.pages.demo

import ru.cleardocs.lkweb.firebase.FirebaseProfile

object FakeData {
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
}
