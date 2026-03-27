plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "playground.app.tobeylin.weather.core.testing"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
}
