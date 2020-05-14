package com.releaseshub.gradle.plugin.artifacts

import org.junit.Assert
import org.junit.Test

class ArtifactUpgradeTest {

    @Test
    fun noIncludesExcludesTest() {
        var artifact = ArtifactUpgrade("group1", "artifact1", "1.0.0")
        Assert.assertTrue(artifact.match(listOf(), listOf()))

        artifact = ArtifactUpgrade("id1", "1.0.0")
        Assert.assertTrue(artifact.match(listOf(), listOf()))
    }

    @Test
    fun groupExcludeTest() {
        val artifact = ArtifactUpgrade("group1", "artifact1", "1.0.0")
        Assert.assertFalse(artifact.match(listOf(), listOf("group1")))
        Assert.assertTrue(artifact.match(listOf(), listOf("group2")))
    }

    @Test
    fun idExcludeTest() {
        val artifact = ArtifactUpgrade("id1", "1.0.0")
        Assert.assertFalse(artifact.match(listOf(), listOf("id1")))
        Assert.assertTrue(artifact.match(listOf(), listOf("id2")))
    }

    @Test
    fun groupAndArtifactExcludeTest() {
        val artifact = ArtifactUpgrade("group1", "artifact1", "1.0.0")
        Assert.assertFalse(artifact.match(listOf(), listOf("group1:artifact1")))
        Assert.assertTrue(artifact.match(listOf(), listOf("group1:artifact2")))
        Assert.assertTrue(artifact.match(listOf(), listOf("group2:artifact1")))
    }

    @Test
    fun groupIncludeTest() {
        val artifact = ArtifactUpgrade("group1", "artifact1", "1.0.0")
        Assert.assertTrue(artifact.match(listOf("group1"), listOf()))
        Assert.assertFalse(artifact.match(listOf("group2"), listOf()))
    }

    @Test
    fun idIncludeTest() {
        val artifact = ArtifactUpgrade("id1", "1.0.0")
        Assert.assertTrue(artifact.match(listOf("id1"), listOf()))
        Assert.assertFalse(artifact.match(listOf("id2"), listOf()))
    }

    @Test
    fun groupAndArtifactIncludeTest() {
        val artifact = ArtifactUpgrade("group1", "artifact1", "1.0.0")
        Assert.assertTrue(artifact.match(listOf("group1:artifact1"), listOf()))
        Assert.assertFalse(artifact.match(listOf("group1:artifact2"), listOf()))
        Assert.assertFalse(artifact.match(listOf("group2:artifact1"), listOf()))
    }

    @Test
    fun mixTest() {
        val artifact = ArtifactUpgrade("group1", "artifact1", "1.0.0")
        Assert.assertTrue(artifact.match(listOf("group1:artifact1"), listOf("group1:artifact2")))
        Assert.assertFalse(artifact.match(listOf("group1:artifact2"), listOf("group1:artifact1")))

        Assert.assertTrue(artifact.match(listOf("group1:artifact1"), listOf("group2")))
        Assert.assertFalse(artifact.match(listOf("group1:artifact2"), listOf("group2")))
        Assert.assertFalse(artifact.match(listOf("group3:artifact1"), listOf("group1")))

        Assert.assertTrue(artifact.match(listOf("group1"), listOf("group2")))
        Assert.assertFalse(artifact.match(listOf("group2"), listOf("group1")))
        Assert.assertFalse(artifact.match(listOf("group2"), listOf("group3")))

        Assert.assertTrue(artifact.match(listOf("group1"), listOf("group1:artifact2")))
        Assert.assertFalse(artifact.match(listOf("group1"), listOf("group1:artifact1")))
        Assert.assertFalse(artifact.match(listOf("group2"), listOf("group1:artifact1")))
        Assert.assertFalse(artifact.match(listOf("group2"), listOf("group1:artifact3")))

        val artifact2 = ArtifactUpgrade("id1", "1.0.0")
        Assert.assertTrue(artifact2.match(listOf("id1"), listOf("id2")))
        Assert.assertFalse(artifact2.match(listOf("id2"), listOf("id1")))
        Assert.assertFalse(artifact2.match(listOf("id2"), listOf("id3")))
    }

    @Test
    fun isSnapshotTest() {
        Assert.assertTrue(createArtifactUpgrade("1.0.0-SNAPSHOT").isSnapshotVersion())
        Assert.assertFalse(createArtifactUpgrade("1.0.0").isSnapshotVersion())
    }

    @Test
    fun isDynamicVersionTest() {
        Assert.assertFalse(createArtifactUpgrade("1.0.0").isDynamicVersion())
        Assert.assertTrue(createArtifactUpgrade("[1.0, 2.0)").isDynamicVersion())
        Assert.assertTrue(createArtifactUpgrade("[1.0, 2.0[").isDynamicVersion())
        Assert.assertTrue(createArtifactUpgrade("[1.0,)").isDynamicVersion())
        Assert.assertTrue(createArtifactUpgrade("1.1+").isDynamicVersion())
        Assert.assertTrue(createArtifactUpgrade("1.1.+").isDynamicVersion())
        Assert.assertTrue(createArtifactUpgrade("latest.release").isDynamicVersion())
        Assert.assertTrue(createArtifactUpgrade("+").isDynamicVersion())
    }

    @Test
    fun equalsTest() {
        Assert.assertTrue(createArtifactUpgrade("1.0.0") == createArtifactUpgrade("2.0.0"))
    }

    private fun createArtifactUpgrade(fromVersion: String): ArtifactUpgrade {
        return ArtifactUpgrade("group1", "artifact1", fromVersion)
    }
}
