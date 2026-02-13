package com.example.testKotlin.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ru.cleardocs.lkweb.firebase.AuthState
import ru.cleardocs.lkweb.firebase.FirebaseProvider
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext

@Page
@Composable
fun HomePage() {
    val repository = FirebaseProvider.repository
    val authState by repository.authStateFlow.collectAsState()
    val ctx = rememberPageContext()

    when (authState) {
        AuthState.Loading -> Unit
        AuthState.Authenticated -> ctx.router.tryRoutingTo("/profile")
        AuthState.Unauthenticated -> ctx.router.tryRoutingTo("/auth")
    }
}