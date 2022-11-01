package com.releaseshub.gradle.plugin.metadata.api

import com.google.common.reflect.TypeToken
import com.jdroid.java.http.BasicHttpResponseValidator
import com.jdroid.java.http.DefaultServer
import com.jdroid.java.http.HttpServiceProcessor
import com.jdroid.java.http.Server
import com.jdroid.java.http.api.AbstractApiService
import com.jdroid.java.http.mock.AbstractMockHttpService
import com.jdroid.java.http.parser.json.GsonParser
import com.releaseshub.gradle.plugin.metadata.domain.ArtifactMetadata

class SheetService() : AbstractApiService() {

    private val server = DefaultServer("opensheet", "opensheet.elk.sh/", true)

    fun getArtifactsMetadata(): List<ArtifactMetadata> {
        val httpService = newGetService("1I3z0M8kTFsweGHvuKyUEaLHh3_W_GmNAGk1qqm1_6lU", "Android")
        return httpService.execute(GsonParser(object : TypeToken<Collection<ArtifactMetadata>>() {}.type))
    }

    override fun getServer(): Server {
        return server
    }

    override fun getHttpServiceProcessors(): MutableList<HttpServiceProcessor> {
        val processors = super.getHttpServiceProcessors().toMutableList()
        processors.add(BasicHttpResponseValidator())
        return processors
    }

    override fun getAbstractMockHttpServiceInstance(vararg urlSegments: Any?): AbstractMockHttpService {
        throw IllegalAccessException()
    }

    override fun isHttpMockEnabled(): Boolean {
        return false
    }
}
