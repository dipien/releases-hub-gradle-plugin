package com.releaseshub.gradle.plugin.artifacts

enum class Packaging(val mavenPackaging: String) {
    AAR("aar"),
    APK("apk"),
    JAR("jar"),
    GRADLE_PLUGIN("jar"),
    POM("pom"),
    WAR("war");

    companion object {
        fun findByMavenPackaging(mavenPackaging: String): Packaging? {
            return values().find { it.mavenPackaging.equals(mavenPackaging, true) }
        }
    }
}