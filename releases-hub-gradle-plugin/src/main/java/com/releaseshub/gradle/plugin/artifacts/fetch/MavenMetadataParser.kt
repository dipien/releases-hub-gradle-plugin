package com.releaseshub.gradle.plugin.artifacts.fetch

import com.jdroid.java.date.DateUtils
import com.jdroid.java.http.parser.Parser
import com.jdroid.java.utils.StreamUtils
import java.io.InputStream

class MavenMetadataParser : Parser {

    companion object {
        const val DATE_PATTERN = "yyyyMMddHHmmss"
    }

    override fun parse(inputStream: InputStream): Any {
        val versioningMetadata = VersioningMetadata()
        val versions = mutableListOf<Version>()
        for (line in StreamUtils.readLines(inputStream)) {
            if (line.trim().startsWith("<version>")) {
                var version = line.trim().replace("<version>", "")
                version = version.replace("</version>", "")
                versions.add(Version(version))
            } else if (line.trim().startsWith("<lastUpdated>")) {
                var lastUpdated = line.trim().replace("<lastUpdated>", "")
                lastUpdated = lastUpdated.replace("</lastUpdated>", "")
                versioningMetadata.lastUpdated = DateUtils.parse(lastUpdated, DATE_PATTERN)
            }
        }
        versioningMetadata.versions = versions
        return versioningMetadata
    }

    override fun parse(input: String): Any? {
        return null
    }
}
