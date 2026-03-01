package ru.cleardocs.lkweb.utils

import ru.cleardocs.lkweb.firebase.AuthState
import ru.cleardocs.lkweb.profile.ProfileAuthState

/**
 * Redirect to [redirectTo] when user is unauthenticated (по Firebase).
 * Call in composable body when the page requires auth.
 */
fun requireAuthRedirect(
    authState: AuthState,
    navigate: (path: String) -> Unit,
    redirectTo: String = "/auth",
) {
    if (authState == AuthState.Unauthenticated) {
        navigate(redirectTo)
    }
}

/**
 * Redirect to [redirectTo] when user is unauthenticated (по ответу бэкенда me: 401/403).
 * Бэкенд валидирует Firebase-токен, поэтому это фактическая проверка авторизации.
 */
fun requireProfileAuthRedirect(
    authState: ProfileAuthState,
    navigate: (path: String) -> Unit,
    redirectTo: String = "/auth",
) {
    if (authState == ProfileAuthState.Unauthenticated) {
        navigate(redirectTo)
    }
}

/**
 * Redirect to [redirectTo] when user is already authenticated.
 * Call in composable body when the page is for guests only (e.g. login).
 */
fun requireGuestRedirect(
    authState: AuthState,
    navigate: (path: String) -> Unit,
    redirectTo: String = "/index",
) {
    if (authState == AuthState.Authenticated) {
        navigate(redirectTo)
    }
}
