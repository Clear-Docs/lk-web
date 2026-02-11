import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import kotlin.js.asDynamic
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
                // SPA fallback: /login, /sign-up, etc. resolve to index.html
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    // property not exposed in typed API on some plugin versions
                    asDynamic().historyApiFallback = true
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.compose.web.core)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                implementation(libs.kobwebCoreJs)
                implementation(libs.kotlinx.coroutines.core)
                implementation(npm("firebase", "10.11.0"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}