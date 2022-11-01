package com.sample.dependencies

object Libs {
    const val INVALID_DEPENDENCY = "aaa:bbb:1.0.0"
    const val DUPLICATED_DEPENDENCY = "aaa:bbb:1.0.0"
    const val EXCLUDED_DEPENDENCY_1 = "ccc:bbb:1.0.0"
    const val SNAPSHOT_DEPENDENCY = "ddd:bbb:1.0.0-SNAPSHOT"
    const val DYNAMIC_DEPENDENCY = "eee:bbb:1.0.+"
    const val FIREBASE_JOB_DISPATCHER = "com.firebase:firebase-jobdispatcher:0.1.0"
    const val FIREBASE_ANALYTICS = "com.google.firebase:firebase-analytics:1.0.0"
    const val FIREBASE_CORE = "com.google.firebase:firebase-core:1.0.0"
    const val CRASHLYTICS = "com.crashlytics.sdk.android:crashlytics:2.5.0"
    const val FIREBASE_MESSAGING = "com.google.firebase:firebase-messaging:16.0.0"
    const val FIREBASE_PERF = "com.google.firebase:firebase-perf:1.0.0"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.7.10"
    const val GLIDE = "com.github.bumptech.glide:glide:3.8.0"
    const val OTTO = "com.squareup:otto:1.0.0"

    @Deprecated("")
    const val ANDROID_SUPPORT = "com.android.support:support-v4:25.3.1"
}
