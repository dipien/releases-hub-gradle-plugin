package com.releaseshub.gradle.plugin.dependencies

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.artifacts.Packaging
import java.io.File

class DependencyUsageSearcher(private val sourcesDir: List<File>, private val unusedExtensionsToSearch: List<String>) {

    fun isAnyPackageUsed(artifactUpgrade: ArtifactUpgrade): Boolean {
        if (!artifactUpgrade.packages.isNullOrEmpty() && (artifactUpgrade.packaging == Packaging.JAR || artifactUpgrade.packaging == Packaging.AAR)) {
            return sourcesDir.any anyDir@{
                return@anyDir it.walk().any { file ->
                    var packageUsedOnFile = false
                    if (unusedExtensionsToSearch.any { extension -> file.name.endsWith(extension) }) {
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
