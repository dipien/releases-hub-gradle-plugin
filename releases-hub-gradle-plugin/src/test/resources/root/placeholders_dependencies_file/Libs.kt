package com.sample.dependencies

object Kotlin {
    const val GROUP_ID = "org.jetbrains.kotlin"
    const val VERSION = "1.3.40"
    const val KOTLIN_JDK = "$GROUP_ID:kotlin-stdlib-jdk7:$VERSION"
    const val KOTLIN_PLUGIN = "$GROUP_ID:kotlin-gradle-plugin:$VERSION"
}

object Firebase {
    const val GROUP_ID = "com.google.firebase"
    const val ANALYTICS = "$GROUP_ID:firebase-analytics:1.0.0"
    const val MESSAGING = "$GROUP_ID:firebase-messaging:16.0.0"
}

object ReleasesHub {
    const val VERSION = "1.4.0"
    const val RELEASES_HUB_PLUGIN = "com.releaseshub:releases-hub-gradle-plugin:$VERSION"
}
