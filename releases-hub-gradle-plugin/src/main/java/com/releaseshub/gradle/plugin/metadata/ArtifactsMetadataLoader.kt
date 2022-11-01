package com.releaseshub.gradle.plugin.metadata

import com.releaseshub.gradle.plugin.metadata.domain.ArtifactMetadata

class ArtifactsMetadataLoader {

    fun load(): Map<String, ArtifactMetadata> {
        val artifactsMap = mutableMapOf<String, ArtifactMetadata>()
        try {
            val values: List<List<Any>> = GoogleSheetsHelper.getValues("1I3z0M8kTFsweGHvuKyUEaLHh3_W_GmNAGk1qqm1_6lU", "Android!A1:K500")
            val headersMap = mutableMapOf<String, Int>()
            for (row in values) {
                var id: String? = null
                try {
                    if (headersMap.isEmpty()) {
                        for (i in row.indices) {
                            headersMap[row[i].toString()] = i
                        }
                    } else {
                        id = getValue("id", headersMap, row)
                        val groupId = getValue("groupId", headersMap, row)
                        val artifactId = getValue("artifactId", headersMap, row)
                        if (id == null) {
                            id = groupId + "_" + artifactId
                        }
                        val artifact = ArtifactMetadata()
                        artifactsMap[id] = artifact
                        artifact.sourceCodeUrl = getValue("sourceCodeUrl", headersMap, row)
                        artifact.issueTrackerUrl = getValue("issueTrackerUrl", headersMap, row)
                        artifact.releaseNotesUrl = getValue("releaseNotesUrl", headersMap, row)
                        artifact.documentationLinks = getListValue("documentationLinks", headersMap, row)
                    }
                } catch (e: Exception) {
                    println("Error when loading data for artifact [$id]: " + (e.cause?.message ?: e.message))
                }
            }
        } catch (e: Exception) {
            println("Error loading artifacts metadata: " + (e.cause?.message ?: e.message))
        }
        return artifactsMap
    }

    private fun getListValue(headerName: String, headersMap: Map<String, Int>, row: List<Any>): List<String> {
        val value = getValue(headerName, headersMap, row)
        return value?.split(";")?.toList() ?: listOf()
    }

    private fun getValue(headerName: String, headersMap: Map<String, Int>, row: List<Any>): String? {
        val index = headersMap[headerName]
        return if (index != null && index < row.size) {
            val value = row[index]
            if (value.toString().isNotBlank()) value.toString() else null
        } else {
            null
        }
    }
}
