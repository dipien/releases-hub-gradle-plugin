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
    fun gradleUpgradeCommentTest() {
        val line = "// this is a comment"
        val upgradeResult = UpgradeResult(false, null, line)

        val artifact = ArtifactUpgrade()
        artifact.id = ArtifactUpgrade.GRADLE_ID
        artifact.toVersion = "1.0.0"
        Assert.assertEquals(upgradeResult, DependenciesParser.upgradeGradle(line, artifact))
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
    fun gradleNotUpgradeTest() {
        val line = """distributionUrl=https\://services.gradle.org/distributions/gradle-5.5-all.zip""""
        val upgradeResult = UpgradeResult(false, null, line)

        val artifact = ArtifactUpgrade()
        artifact.id = "a"
        artifact.toVersion = "5.0"
        Assert.assertEquals(upgradeResult, DependenciesParser.upgradeGradle(line, artifact))
    }

    @Test
    fun notUpgradeTest2() {
        val line = """libs.jdroid_java_core = "com.jdroidtools:jdroid-java-core:2.0.0""""
        val upgradeResult = UpgradeResult(false, null, line)

        val artifact = ArtifactUpgrade()
        artifact.groupId = "com.jdroidtools"
        artifact.artifactId = "jdroid-java-core"
        artifact.toVersion = "2.0.0"
        Assert.assertEquals(upgradeResult, DependenciesParser.upgradeDependency(line, artifact))
    }

    @Test
    fun gradleNotUpgradeTest2() {
        val line = """distributionUrl=https\://services.gradle.org/distributions/gradle-5.5-all.zip""""
        val upgradeResult = UpgradeResult(false, null, line)

        val artifact = ArtifactUpgrade()
        artifact.id = ArtifactUpgrade.GRADLE_ID
        artifact.toVersion = "5.5"
        Assert.assertEquals(upgradeResult, DependenciesParser.upgradeGradle(line, artifact))
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
    fun gradleUpgradeTest() {
        val artifact = ArtifactUpgrade()
        artifact.id = ArtifactUpgrade.GRADLE_ID
        artifact.fromVersion = "5.0"
        artifact.toVersion = "5.5"
        val oldLine = """distributionUrl=https\://services.gradle.org/distributions/gradle-5.0-all.zip""""
        val newLine = """distributionUrl=https\://services.gradle.org/distributions/gradle-5.5-all.zip""""
        val upgradeResult = UpgradeResult(true, artifact, newLine)
        Assert.assertEquals(upgradeResult, DependenciesParser.upgradeGradle(oldLine, artifact))
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

    @Test
    fun gradleUpgradeTest2() {
        val artifact = ArtifactUpgrade()
        artifact.id = ArtifactUpgrade.GRADLE_ID
        artifact.fromVersion = "1.0.0"
        artifact.toVersion = "5.5"

        val artifactResult = ArtifactUpgrade()
        artifactResult.id = ArtifactUpgrade.GRADLE_ID
        artifactResult.fromVersion = "5.0"
        artifactResult.toVersion = "5.5"

        val oldLine = """distributionUrl=https\://services.gradle.org/distributions/gradle-5.0-all.zip""""
        val newLine = """distributionUrl=https\://services.gradle.org/distributions/gradle-5.5-all.zip""""
        val upgradeResult = UpgradeResult(true, artifactResult, newLine)
        Assert.assertEquals(upgradeResult, DependenciesParser.upgradeGradle(oldLine, artifact))
    }

    @Test
    fun extractGradleArtifactTest() {
        Assert.assertNull(DependenciesParser.extractGradleArtifact(""))
        Assert.assertNull(DependenciesParser.extractGradleArtifact("distributionBase=GRADLE_USER_HOME"))
        val artifact = ArtifactUpgrade(ArtifactUpgrade.GRADLE_ID, "5.5.1")
        Assert.assertEquals(artifact, DependenciesParser.extractGradleArtifact("distributionUrl=https\\://services.gradle.org/distributions/gradle-5.5.1-all.zip"))
    }
}
