package com.releaseshub.gradle.plugin.artifacts

import org.junit.Assert
import org.junit.Test

class ArtifactTest {

    @Test
    fun noIncludesExcludesTest() {
        val artifact = Artifact("group1", "artifact1", "1.0.0")
        Assert.assertTrue(artifact.match(listOf(), listOf()))
    }

    @Test
    fun groupExcludeTest() {
        val artifact = Artifact("group1", "artifact1", "1.0.0")
        Assert.assertFalse(artifact.match(listOf(), listOf("group1")))
        Assert.assertTrue(artifact.match(listOf(), listOf("group2")))
    }

    @Test
    fun artifactExcludeTest() {
        val artifact = Artifact("group1", "artifact1", "1.0.0")
        Assert.assertFalse(artifact.match(listOf(), listOf("group1:artifact1")))
        Assert.assertTrue(artifact.match(listOf(), listOf("group1:artifact2")))
        Assert.assertTrue(artifact.match(listOf(), listOf("group2:artifact1")))
    }

    @Test
    fun groupIncludeTest() {
        val artifact = Artifact("group1", "artifact1", "1.0.0")
        Assert.assertTrue(artifact.match(listOf("group1"), listOf()))
        Assert.assertFalse(artifact.match(listOf("group2"), listOf()))
    }

    @Test
    fun artifactIncludeTest() {
        val artifact = Artifact("group1", "artifact1", "1.0.0")
        Assert.assertTrue(artifact.match(listOf("group1:artifact1"), listOf()))
        Assert.assertFalse(artifact.match(listOf("group1:artifact2"), listOf()))
        Assert.assertFalse(artifact.match(listOf("group2:artifact1"), listOf()))
    }

    @Test
    fun mixTest() {
        val artifact = Artifact("group1", "artifact1", "1.0.0")
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
    }
}