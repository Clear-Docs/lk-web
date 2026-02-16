package ru.cleardocs.lkweb.demo.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import org.jetbrains.compose.web.css.cssRem

@Composable
fun DemoNavLink(path: String, text: String) {
    Link(
        path,
        text,
        modifier = Modifier.padding(0.5.cssRem),
        variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
    )
}

@Composable
fun DemoMenuItems() {
    DemoNavLink("/demo", "Demo")
    DemoNavLink("/demo/profile", "ProfileBlock")
    DemoNavLink("/demo/profile-menu", "ProfileMenu")
    DemoNavLink("/demo/layout", "Layout")
    DemoNavLink("/demo/plans", "PlansList")
}
