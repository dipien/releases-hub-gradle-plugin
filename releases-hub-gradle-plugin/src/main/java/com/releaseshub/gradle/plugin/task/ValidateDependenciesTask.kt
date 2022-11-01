package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.artifacts.fetch.ArtifactUpgradeHelper
import com.releaseshub.gradle.plugin.common.AbstractTask
import com.releaseshub.gradle.plugin.dependencies.BasicDependenciesExtractor
import com.releaseshub.gradle.plugin.dependencies.DependencyUsageSearcher
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import java.io.File

open class ValidateDependenciesTask : AbstractTask() {

    companion object {
        const val TASK_NAME = "validateDependencies"
    }

    @get:Input
    lateinit var unusedExcludes: List<String>

    @get:Input
    lateinit var unusedExtensionsToSearch: List<String>

    init {
        description = "Validate all dependencies"
    }

    override fun onExecute() {

        var fail = false
        val extractor = BasicDependenciesExtractor(getAllDependenciesPaths())
        val dependenciesParserResult = extractor.extractArtifacts(project.rootProject.projectDir)
        dependenciesParserResult.artifactsMap.forEach { (path, artifacts) ->
            var failOnFile = false
            log(path)
            artifacts.forEach { artifact ->
                if (artifact.isSnapshotVersion()) {
                    fail = true
                    failOnFile = true
                    log("- The dependency ${artifact.toFromVersionedString()} is a snapshot")
                }

                if (artifact.isDynamicVersion()) {
                    fail = true
                    failOnFile = true
                    log("- The dependency ${artifact.toFromVersionedString()} is using a dynamic version")
                }

                if (path.endsWith(".gradle") || path.endsWith(".gradle.kts")) {
                    fail = true
                    failOnFile = true
                    log("- The dependency $artifact is explicitly declared on a .gradle(.kts) file instead of using buildSrc or Version Catalog.")
                }
            }
            if (!failOnFile) {
                log("- No errors found")
            }
            log("")
        }

        // Find duplicates
        log("")
        val notDuplicatedArtifacts = mutableListOf<ArtifactUpgrade>()
        val artifacts = dependenciesParserResult.getAllArtifacts()
        artifacts.forEach { artifact ->
            if (notDuplicatedArtifacts.contains(artifact)) {
                fail = true
                log("- The dependency $artifact is duplicated")
            } else {
                notDuplicatedArtifacts.add(artifact)
            }
        }

        val artifactsUpgrades = ArtifactUpgradeHelper.getArtifactsUpgrades(notDuplicatedArtifacts, getRepositories(), false)

        val sourcesDir = mutableListOf<File>()
        project.rootProject.allprojects.forEach {
            sourcesDir.addAll(getSourceSets(it))
        }

        val excludes = unusedExcludes.plus("org.jetbrains.kotlin:kotlin-stdlib-jdk7").plus("com.pinterest:ktlint")
        val dependencyUsageSearcher = DependencyUsageSearcher(sourcesDir, unusedExtensionsToSearch)
        artifactsUpgrades.filter { it.match(null, excludes) }.forEach { artifactUpgrade ->
            if (!dependencyUsageSearcher.isAnyPackageUsed(artifactUpgrade)) {
                log("- The dependency $artifactUpgrade seems to be unused on your project. See if you can safely remove it.")
                fail = true
            }
        }

        if (fail) {
            throw RuntimeException("Some errors were found on your dependencies")
        }
    }

    // TODO We should automatically search for projects source sets
    private fun getSourceSets(project: Project): List<File> {
        val paths = mutableListOf<String>()
        paths.add("src" + File.separator + "main" + File.separator + "java")
        paths.add("src" + File.separator + "main" + File.separator + "kotlin")
        paths.add("src" + File.separator + "main" + File.separator + "resources")
        paths.add("src" + File.separator + "release" + File.separator + "java")
        paths.add("src" + File.separator + "release" + File.separator + "kotlin")
        paths.add("src" + File.separator + "release" + File.separator + "resources")
        paths.add("src" + File.separator + "debug" + File.separator + "java")
        paths.add("src" + File.separator + "debug" + File.separator + "kotlin")
        paths.add("src" + File.separator + "debug" + File.separator + "resources")
        paths.add("src" + File.separator + "test" + File.separator + "java")
        paths.add("src" + File.separator + "test" + File.separator + "kotlin")
        paths.add("src" + File.separator + "test" + File.separator + "resources")
        paths.add("src" + File.separator + "androidTest" + File.separator + "java")
        paths.add("src" + File.separator + "androidTest" + File.separator + "kotlin")
        paths.add("src" + File.separator + "androidTest" + File.separator + "resources")

        val sourceSets = mutableListOf<File>()
        paths.forEach {
            val dir = File(project.projectDir, it)
            if (dir.exists()) {
                sourceSets.add(dir)
            }
        }
        return sourceSets.toList()
    }
}
