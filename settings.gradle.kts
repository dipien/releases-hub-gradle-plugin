plugins {
    id("com.gradle.enterprise").version("3.11.2")
}

if (System.getenv("CI") == "true") {
    buildCache {
        local {
            directory = File(System.getProperty("user.home"), "/gradle-build-cache")
        }
    }
}

include(":releases-hub-gradle-plugin")
