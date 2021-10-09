package com.releaseshub.gradle.plugin.artifacts.api

import com.google.gson.reflect.TypeToken
import com.jdroid.java.exception.UnexpectedException
import com.jdroid.java.http.BasicHttpResponseValidator
import com.jdroid.java.http.HttpServiceProcessor
import com.jdroid.java.http.Server
import com.jdroid.java.http.api.AbstractApiService
import com.jdroid.java.http.mock.AbstractMockHttpService
import com.jdroid.java.http.parser.json.GsonParser
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgrade
import com.releaseshub.gradle.plugin.artifacts.ArtifactUpgradeBody
import javax.net.ssl.SSLPeerUnverifiedException

class AppService(private val server: Server, private val userToken: String) : AbstractApiService() {

    fun getArtifactsToUpgrade(artifactsToCheck: List<ArtifactUpgrade>): List<ArtifactUpgrade> {
        try {
            return innerGetArtifactsToUpgrade(artifactsToCheck)
        } catch (e: UnexpectedException) {
            // TODO Retry to reduce the chance of this error. We should investigate it
            // javax.net.ssl.SSLPeerUnverifiedException: Hostname cloud.dipien.com not verified (no certificates)
            if (e.cause is SSLPeerUnverifiedException) {
                Thread.sleep(5000)
                return innerGetArtifactsToUpgrade(artifactsToCheck)
            }
            throw e
        }
    }

    private fun innerGetArtifactsToUpgrade(artifactsToCheck: List<ArtifactUpgrade>): List<ArtifactUpgrade> {
        val httpService = newPostService("artifacts", "upgrade")
        httpService.addQueryParameter("returnNotUpgraded", true)
        val body = ArtifactUpgradeBody()
        body.artifactsToCheck = artifactsToCheck
        autoMarshall(httpService, body)
        return httpService.execute(GsonParser(object : TypeToken<Collection<ArtifactUpgrade>>() {}.type))
    }



    override fun getServer(): Server {
        return server
    }

    override fun getHttpServiceProcessors(): MutableList<HttpServiceProcessor> {
        val processors = super.getHttpServiceProcessors().toMutableList()
        processors.add(BasicHttpResponseValidator())
        processors.add(HeadersAppender(userToken))
        return processors
    }

    override fun getAbstractMockHttpServiceInstance(vararg urlSegments: Any?): AbstractMockHttpService {
        throw IllegalAccessException()
    }

    override fun isHttpMockEnabled(): Boolean {
        return false
    }
}
