package com.releaseshub.gradle.plugin.artifacts.api

import com.google.gson.reflect.TypeToken
import com.jdroid.java.http.BasicHttpResponseValidator
import com.jdroid.java.http.HttpServiceProcessor
import com.jdroid.java.http.Server
import com.jdroid.java.http.api.AbstractApiService
import com.jdroid.java.http.mock.AbstractMockHttpService
import com.jdroid.java.http.parser.json.GsonParser
import com.releaseshub.gradle.plugin.artifacts.Artifact
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgradeBody
import com.releaseshub.gradle.plugin.artifacts.MavenArtifactRepository

class AppService(private val server: Server, private val appVersion: String, private val userToken: String) : AbstractApiService() {

    fun getArtifactsToUpgrade(artifacts: List<Artifact>, repositories: List<MavenArtifactRepository>): List<Artifact> {
        val httpService = newPostService("artifacts", "upgrade")
        val body = ArtifactUpgradeBody()
        body.artifactsToCheck = artifacts
        body.repositories = repositories
        autoMarshall(httpService, body)
        return httpService.execute(GsonParser(object : TypeToken<Collection<Artifact>>() {}.type))
    }

    override fun getServer(): Server {
        return server
    }

    override fun getHttpServiceProcessors(): MutableList<HttpServiceProcessor> {
        val processors = super.getHttpServiceProcessors().toMutableList()
        processors.add(BasicHttpResponseValidator())
        processors.add(HeadersAppender(appVersion, userToken))
        return processors
    }

    override fun getAbstractMockHttpServiceInstance(vararg urlSegments: Any?): AbstractMockHttpService {
        throw IllegalAccessException()
    }

    override fun isHttpMockEnabled(): Boolean {
        return false
    }
}
