package com.releaseshub.gradle.plugin.artifacts.api

import com.google.gson.reflect.TypeToken
import com.jdroid.java.http.BasicHttpResponseValidator
import com.jdroid.java.http.HttpServiceProcessor
import com.jdroid.java.http.Server
import com.jdroid.java.http.api.AbstractApiService
import com.jdroid.java.http.mock.AbstractMockHttpService
import com.jdroid.java.http.parser.json.GsonParser
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgradeBody
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgradeStatus

class AppService(private val server: Server, private val appVersion: String, private val userToken: String) : AbstractApiService() {

    fun getArtifactsToUpgrade(artifactsToCheck: List<ArtifactUpgrade>): List<ArtifactUpgrade> {
        val httpService = newPostService("artifacts", "upgrade")
        httpService.addQueryParameter("returnNotUpgraded", true)
        val body = ArtifactUpgradeBody()
        body.artifactsToCheck = artifactsToCheck
        autoMarshall(httpService, body)
        val artifactsToUpgrade: List<ArtifactUpgrade> = httpService.execute(GsonParser(object : TypeToken<Collection<ArtifactUpgrade>>() {}.type))

        // TODO Do this on server side?
        artifactsToUpgrade.onEach {
            if (it.fromVersion != it.toVersion) {
                it.artifactUpgradeStatus = ArtifactUpgradeStatus.PENDING_UPGRADE
            } else {
                it.artifactUpgradeStatus = ArtifactUpgradeStatus.ALREADY_UPGRADED
            }
        }
        return artifactsToUpgrade
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
