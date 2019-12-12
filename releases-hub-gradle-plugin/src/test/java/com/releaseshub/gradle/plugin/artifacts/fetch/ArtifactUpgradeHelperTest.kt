package com.releaseshub.gradle.plugin.artifacts.fetch

import org.junit.Assert
import org.junit.Test

class ArtifactUpgradeHelperTest {

    @Test
    fun getLatestVersions() {
        assert("1.0.0", listOf("1.0.0"))
        assert("2.0.0", listOf("1.0.0", "2.0.0"))
        assert("2.0.0", listOf("1.0.0", "2.0.0", "1.0.1"))
        assert("2.0", listOf("1.0.0", "2.0", "1.0.1"))
        assert("2.0", listOf("1.0.0", "2.0", "1.0.1", "1.0"))
        assert("2.0.0", listOf("1.0.0-SNAPSHOT", "2.0.0"))
        assert("2.0.0", listOf("2.0.0-SNAPSHOT", "2.0.0"))
        assert("2.0.0", listOf("3.0.0-SNAPSHOT", "2.0.0"))
        assert("2.0.0", listOf("2.0.0", "3.0.0-SNAPSHOT"))
        assert("3.0.0-SNAPSHOT", listOf("2.0.0-SNAPSHOT", "3.0.0-SNAPSHOT"))
        assert("3.0.1-SNAPSHOT", listOf("3.0.1-SNAPSHOT", "2.0.0-SNAPSHOT", "3.0.0-SNAPSHOT"))
    }

    private fun assert(expectedVersion: String, versions: List<String>) {
        Assert.assertEquals(expectedVersion, ArtifactUpgradeHelper.getLatestVersion(versions.map { Version(it) }).toString())
    }
}