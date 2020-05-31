package com.releaseshub.gradle.plugin.artifacts.fetch

import com.jdroid.java.http.BasicHttpResponseValidator
import com.jdroid.java.http.DefaultServer
import com.jdroid.java.http.HttpServiceProcessor
import com.jdroid.java.http.Server
import com.jdroid.java.http.api.AbstractApiService
import com.jdroid.java.http.mock.AbstractMockHttpService

class MavenRepositoryService(private val serverName: String, private val serverUrl: String) : AbstractApiService() {

    fun getVersioningMetadata(artifact: Artifact): VersioningMetadata {
        val httpService = newGetService()
        httpService.setSsl(true)
        httpService.addUrlSegment(artifact.groupId!!.replace(".", "/"))
        httpService.addUrlSegment(artifact.artifactId)
        httpService.addUrlSegment("maven-metadata.xml")
        return httpService.execute(MavenMetadataParser())
    }

    override fun getServer(): Server {
        var url = serverUrl
        var ssl = true
        if (serverUrl.startsWith("http://")) {
            url = serverUrl.removePrefix("http://")
            ssl = false
        } else if (serverUrl.startsWith("https://")) {
            url = serverUrl.removePrefix("https://")
            ssl = true
        }
        return DefaultServer(serverName, url, ssl)
    }

    public override fun getHttpServiceProcessors(): List<HttpServiceProcessor> {
        return listOf<HttpServiceProcessor>(BasicHttpResponseValidator())
    }

    override fun getAbstractMockHttpServiceInstance(vararg urlSegments: Any?): AbstractMockHttpService {
        throw IllegalAccessException()
    }

    override fun isHttpMockEnabled(): Boolean {
        return false
    }
}
