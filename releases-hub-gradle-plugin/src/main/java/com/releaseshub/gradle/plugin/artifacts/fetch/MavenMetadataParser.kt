package com.releaseshub.gradle.plugin.artifacts.fetch

import com.jdroid.java.http.parser.Parser
import com.jdroid.java.utils.StreamUtils
import java.io.InputStream

class MavenMetadataParser : Parser {

    override fun parse(inputStream: InputStream): Any {
        val versions = mutableListOf<String>()
        for (line in StreamUtils.readLines(inputStream)) {
            if (line.trim().startsWith("<version>")) {
                var version = line.trim { it <= ' ' }.replace("<version>", "")
                version = version.replace("</version>", "")
                versions.add(version)
            }
        }
        return versions
    }

    override fun parse(input: String): Any? {
        return null
    }
}
