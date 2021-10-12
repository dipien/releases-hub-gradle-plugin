package com.releaseshub.gradle.plugin.dependencies

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency

object DeclaredDependenciesExtractor {

    // TODO Improve this to ignore all the configs we don't want
    private val IGNORED_CONFIGURATIONS = listOf(
        "kotlinScriptDef",
        "debugAndroidTestCompileClasspath",
        "releaseUnitTestRuntimeClasspath"
    )
    private val IGNORED_CONFIGURATIONS_PREFIXES = listOf(
        "_",
        "-",
        "kotlinCompiler",
        "kotlinKapt",
        "kotlinNativeCompiler",
        "lint"
    )
    private val IGNORED_CONFIGURATIONS_SUFFIXES = listOf(
        "nnotationProcessor"
    )

    @Suppress("SENSELESS_COMPARISON", "UNNECESSARY_NOT_NULL_ASSERTION")
    fun getDeclaredDependencies(rootProject: Project): List<ArtifactUpgrade> {
        val artifactsUpgrades = mutableListOf<ArtifactUpgrade>()
        rootProject.allprojects.forEach { project ->
            project.configurations.forEach { config ->
                if (!ignoreConfiguration(config.name)) {
                    config.dependencies.filterIsInstance(DefaultExternalModuleDependency::class.java).forEach { dependency ->
                        if (dependency.group != null) {
                            val artifactUpgrade = ArtifactUpgrade(dependency.group!!, dependency.name, dependency.version)
                            artifactUpgrade.project = project.name
                            artifactUpgrade.configuration = config.name
                            if (!artifactsUpgrades.contains(artifactUpgrade)) {
                                artifactsUpgrades.add(artifactUpgrade)
                            }
                        }
                    }
                }
            }
        }
        return artifactsUpgrades.toList()
    }

    private fun ignoreConfiguration(config: String): Boolean {
        return IGNORED_CONFIGURATIONS_PREFIXES.any { config.startsWith(it) } || IGNORED_CONFIGURATIONS_SUFFIXES.any { config.endsWith(it) || IGNORED_CONFIGURATIONS.contains(config) }
    }
}
