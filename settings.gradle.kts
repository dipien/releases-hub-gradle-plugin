plugins {
    id("com.gradle.enterprise").version("3.3.4")
}

include(":releases-hub-gradle-plugin")

apply(from = java.io.File(settingsDir, "buildCacheSettings.gradle"))
