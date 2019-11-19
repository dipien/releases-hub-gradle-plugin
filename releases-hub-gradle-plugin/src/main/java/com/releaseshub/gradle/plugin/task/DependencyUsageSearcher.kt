package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.artifacts.Packaging
import java.io.File

class DependencyUsageSearcher(private val sourcesDir: List<File>) {

    fun isDependencyDeclared(artifactUpgrade: ArtifactUpgrade): Boolean {
        // TODO Implement me
        return true
    }

    fun isAnyPackageUsed(artifactUpgrade: ArtifactUpgrade): Boolean {
        // TODO We should also check if a plugin is not applied
        if (!artifactUpgrade.packages.isNullOrEmpty() && (artifactUpgrade.packaging == Packaging.JAR || artifactUpgrade.packaging == Packaging.AAR)) {
            return sourcesDir.any anyDir@{
                return@anyDir it.walk().any { file ->
                    var packageUsedOnFile = false
                    if (file.name.endsWith(".kt") || file.name.endsWith(".java") || file.name.endsWith(".xml")) {
                        file.forEachLine { line ->
                            if (!packageUsedOnFile) {
                                packageUsedOnFile = artifactUpgrade.packages!!.any { eachPackage -> line.contains(eachPackage, false) }
                            }
                        }
                    }
                    return@any packageUsedOnFile
                }
            }
        }
        return true
    }
}