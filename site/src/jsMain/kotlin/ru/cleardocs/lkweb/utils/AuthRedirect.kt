package ru.cleardocs.lkweb.utils

import ru.cleardocs.lkweb.firebase.AuthState

/**
 * Redirect to [redirectTo] when user is unauthenticated.
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
 * Redirect to [redirectTo] when user is already authenticated.
 * Call in composable body when the page is for guests only (e.g. login).
 */
fun requireGuestRedirect(
    authState: AuthState,
    navigate: (path: String) -> Unit,
    redirectTo: String = "/profile",
) {
    if (authState == AuthState.Authenticated) {
        navigate(redirectTo)
    }
}
