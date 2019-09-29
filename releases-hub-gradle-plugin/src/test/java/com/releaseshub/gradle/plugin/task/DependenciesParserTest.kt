package com.releaseshub.gradle.plugin.task

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import org.junit.Assert
import org.junit.Test

class DependenciesParserTest {

    @Test
    fun extractFromEmptyTest() {
        Assert.assertNull(DependenciesParser.extractArtifact(""))
        Assert.assertNull(DependenciesParser.extractArtifact(" "))
    }

    @Test
    fun extractFromCommentTest() {
        Assert.assertNull(DependenciesParser.extractArtifact("// this is a comment"))
        Assert.assertNull(DependenciesParser.extractArtifact("  // this is a comment"))
        Assert.assertNull(DependenciesParser.extractArtifact("""// this is a comment "com.jdroidtools:jdroid-java-core:2.0.0""""))
    }

    @Test
    fun extractWithoutVersionTest() {
        Assert.assertNull(DependenciesParser.extractArtifact("def libs = [:]"))
        Assert.assertNull(DependenciesParser.extractArtifact("  // rootProject.ext['libs'] = libs"))
    }

    @Test
    fun extractWithVersionTest() {
        val artifact = ArtifactUpgrade("com.jdroidtools", "jdroid-java-core", "2.0.0")
        Assert.assertEquals(artifact, DependenciesParser.extractArtifact("""libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:2.0.0""""))
    }

    @Test
    fun upgradeCommentTest() {
        val line = "// this is a comment"
        val upgradeResult = UpgradeResult(false, null, line)

        val artifact = ArtifactUpgrade()
        artifact.groupId = "a"
        artifact.artifactId = "b"
        artifact.toVersion = "1.0.0"
        Assert.assertEquals(upgradeResult, DependenciesParser.upgradeDependency(line, artifact))
    }

    @Test
    fun notUpgradeTest() {
        val line = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:2.0.0""""
        val upgradeResult = UpgradeResult(false, null, line)

        val artifact = ArtifactUpgrade()
        artifact.groupId = "a"
        artifact.artifactId = "b"
        artifact.toVersion = "1.0.0"
        Assert.assertEquals(upgradeResult, DependenciesParser.upgradeDependency(line, artifact))
    }

    @Test
    fun notUpgradeTest2() {
        val line = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:2.0.0""""
        val upgradeResult = UpgradeResult(false, null, line)

        val artifact = ArtifactUpgrade()
        artifact.groupId = "com.jdroidtools"
        artifact.artifactId = "jdroid-java-core"
        artifact.fromVersion = "1.0.0"
        artifact.toVersion = "2.0.0"
        Assert.assertEquals(upgradeResult, DependenciesParser.upgradeDependency(line, artifact))
    }

    @Test
    fun upgradeTest() {
        val artifact = ArtifactUpgrade()
        artifact.groupId = "com.jdroidtools"
        artifact.artifactId = "jdroid-java-core"
        artifact.fromVersion = "2.0.0"
        artifact.toVersion = "3.0.0"
        val oldLine = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:2.0.0""""
        val newLine = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:3.0.0""""
        val upgradeResult = UpgradeResult(true, artifact, newLine)
        Assert.assertEquals(upgradeResult, DependenciesParser.upgradeDependency(oldLine, artifact))
    }

    @Test
    fun upgradeTest2() {
        val artifact = ArtifactUpgrade()
        artifact.groupId = "com.jdroidtools"
        artifact.artifactId = "jdroid-java-core"
        artifact.fromVersion = "1.0.0"
        artifact.toVersion = "3.0.0"

        val artifactResult = ArtifactUpgrade()
        artifactResult.groupId = "com.jdroidtools"
        artifactResult.artifactId = "jdroid-java-core"
        artifactResult.fromVersion = "2.0.0"
        artifactResult.toVersion = "3.0.0"

        val oldLine = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:2.0.0""""
        val newLine = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:3.0.0""""
        val upgradeResult = UpgradeResult(true, artifactResult, newLine)
        Assert.assertEquals(upgradeResult, DependenciesParser.upgradeDependency(oldLine, artifact))
    }
}