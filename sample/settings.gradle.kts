plugins {
    id("com.gradle.enterprise").version("3.7")
}

include(":app")

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            alias("firebase-core").to("com.google.firebase:firebase-core:1.0.0")
        }
    }
}
