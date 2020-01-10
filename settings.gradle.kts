plugins {
    id("com.gradle.enterprise").version("3.1.1")
}

include(":releases-hub-gradle-plugin")

apply(from = java.io.File(settingsDir, "buildCacheSettings.gradle"))
