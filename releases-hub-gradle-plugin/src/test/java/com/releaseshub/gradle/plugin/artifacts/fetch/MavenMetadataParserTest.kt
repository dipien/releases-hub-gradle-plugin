package com.releaseshub.gradle.plugin.artifacts.fetch

import com.google.common.truth.Truth
import com.releaseshub.gradle.plugin.common.ResourceUtils
import org.junit.Test

class MavenMetadataParserTest {

    @Test
    fun parseTest() {

        val parser = MavenMetadataParser()

        val versioningMetadata = parser.parse(ResourceUtils.getRequiredResourceAsStream("maven_metadata/maven_metadata.xml")) as VersioningMetadata
        Truth.assertThat(versioningMetadata.versions).isEqualTo(listOf(
            Version("2.0.0"),
            Version("2.0.1"),
            Version("1.0.0"),
            Version("1.0.1"),
            Version("1.2.0"),
            Version("1.2.1")
        ))
    }
}
