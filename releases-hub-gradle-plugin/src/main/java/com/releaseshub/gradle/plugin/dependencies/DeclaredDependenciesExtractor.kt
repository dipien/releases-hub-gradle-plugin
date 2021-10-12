package com.releaseshub.gradle.plugin.dependencies

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency

object DeclaredDependenciesExtractor {

    // TODO Improve this to ignore all the configs we don't want
    private val IGNORED_CONFIGURATIONS = listOf("kotlinCompilerClasspath", "kotlinCompilerPluginClasspath",
        "kotlinNativeCompilerPluginClasspath", "releaseUnitTestRuntimeClasspath", "kotlinScriptDef", "kotlinKaptWorkerDependencies", "testAnnotationProcessor",
        "annotationProcessor", "lintClassPath")

    @Suppress("SENSELESS_COMPARISON", "UNNECESSARY_NOT_NULL_ASSERTION")
    fun getDeclaredDependencies(rootProject: Project): List<ArtifactUpgrade> {
        val artifactsUpgrades = mutableListOf<ArtifactUpgrade>()
        rootProject.allprojects.forEach { project ->
            project.configurations.forEach { config ->
                if (!config.name.startsWith("_") && !config.name.startsWith("-") && !IGNORED_CONFIGURATIONS.contains(config.name)) {
                    config.dependencies.filterIsInstance(DefaultExternalModuleDependency::class.java).forEach { dependency ->
                        if (dependency.group != null) {
                            val artifactUpgrade = ArtifactUpgrade(dependency.group!!, dependency.name, dependency.version)
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
}
