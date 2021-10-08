package com.releaseshub.gradle.plugin.dependencies

import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.common.ResourceUtils
import org.junit.Assert
import org.junit.Test
import java.io.File

class BuildSrcDependenciesExtractorTest {

    @Test
    fun extractFromEmptyTest() {
        val dependenciesParserResult = extractArtifacts("empty_dependencies_file")
        Assert.assertTrue(dependenciesParserResult.getAllArtifacts().isEmpty())
        Assert.assertTrue(dependenciesParserResult.excludedArtifacts.isEmpty())
    }

    @Test
    fun extractFromCommentTest() {
        val dependenciesParserResult = extractArtifacts("comments_file")
        Assert.assertTrue("Found these artifacts: ${dependenciesParserResult.getAllArtifacts()}", dependenciesParserResult.getAllArtifacts().isEmpty())
        Assert.assertTrue(dependenciesParserResult.excludedArtifacts.isEmpty())
    }

    @Test
    fun extractWithVersionTest() {
        val dependenciesParserResult = extractArtifacts("dependencies_versions_file")
        Assert.assertEquals(2, dependenciesParserResult.getAllArtifacts().size)

        val firstArtifact = dependenciesParserResult.getAllArtifacts()[0]
        Assert.assertEquals("com.dipien", firstArtifact.groupId)
        Assert.assertEquals("sample", firstArtifact.artifactId)
        Assert.assertEquals("3.0.0", firstArtifact.fromVersion)

        val secondArtifact = dependenciesParserResult.getAllArtifacts()[1]
        Assert.assertEquals("junit", secondArtifact.groupId)
        Assert.assertEquals("junit", secondArtifact.artifactId)
        Assert.assertEquals("4.13", secondArtifact.fromVersion)

        Assert.assertTrue(dependenciesParserResult.excludedArtifacts.isEmpty())
    }

    @Test
    fun extractGradleArtifactTest() {

        val dependenciesParserResult = extractGradleArtifacts()
        Assert.assertEquals(1, dependenciesParserResult.getAllArtifacts().size)

        val firstArtifact = dependenciesParserResult.getAllArtifacts()[0]
        Assert.assertEquals(ArtifactUpgrade.GRADLE_ID, firstArtifact.id)
        Assert.assertEquals("6.0.1", firstArtifact.fromVersion)

        Assert.assertTrue(dependenciesParserResult.excludedArtifacts.isEmpty())
    }

    private fun extractArtifacts(basePath: String): DependenciesExtractorResult {
        val extractor = BuildSrcDependenciesExtractor(listOf("$basePath/Libs.kt"))
        return extractor.extractArtifacts(File(ResourceUtils.getRequiredResourcePath("root")))
    }

    private fun extractGradleArtifacts(): DependenciesExtractorResult {
        val extractor = BuildSrcDependenciesExtractor(emptyList())
        return extractor.extractArtifacts(File(ResourceUtils.getRequiredResourcePath("root_gradle")))
    }
}
