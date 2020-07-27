package com.sample.dependencies

object Libs {
    const val INVALID_DEPENDENCY = "aaa:bbb:1.0.0"
    const val DUPLICATED_DEPENDENCY = "aaa:bbb:1.0.1"
    const val EXCLUDED_DEPENDENCY_1 = "ccc:bbb:1.0.0"
    const val SNAPSHOT_DEPENDENCY = "ddd:bbb:1.0.0-SNAPSHOT"
    const val DYNAMIC_DEPENDENCY = "eee:bbb:1.0.+"
}

object Kotlin {
    const val GROUP_ID = "org.jetbrains.kotlin"
    const val VERSION = "1.3.40"
    const val KOTLIN_JDK = "$GROUP_ID:kotlin-stdlib-jdk7:$VERSION"
    const val KOTLIN_PLUGIN = "$GROUP_ID:kotlin-gradle-plugin:$VERSION"
}

object OkHttp {
    const val GROUP_ID = "com.squareup.okhttp3"
    const val VERSION = "4.2.0"
    const val OKHTTP = "$GROUP_ID:okhttp:$VERSION"
    const val OKHTTP_URL_CONNECTION = "$GROUP_ID:okhttp-urlconnection:$VERSION"
}

object Firebase {
    const val GROUP_ID = "com.google.firebase"
    const val ANALYTICS = "$GROUP_ID:firebase-analytics:1.0.0"
    const val MESSAGING = "$GROUP_ID:firebase-messaging:16.0.0"
    const val PERF = "$GROUP_ID:firebase-perf:1.0.0"
}

object ReleasesHub {
    const val VERSION = "1.4.0-SNAPSHOT"
    const val RELEASES_HUB_PLUGIN = "com.releaseshub:releases-hub-gradle-plugin:$VERSION"
}
