package com.releaseshub.gradle.plugin.artifacts.api

import com.jdroid.java.http.HttpResponseWrapper
import com.jdroid.java.http.HttpService
import com.jdroid.java.http.HttpServiceProcessor
import com.jdroid.java.http.MimeType
import com.jdroid.java.utils.LocaleUtils
import com.releaseshub.gradle.plugin.context.BuildConfig
import java.util.concurrent.TimeUnit

class HeadersAppender(private val userToken: String) : HttpServiceProcessor {

    companion object {
        const val USER_AGENT_HEADER_VALUE = "ReleasesHubGradlePlugin/${BuildConfig.VERSION}"
        const val USER_TOKEN_HEADER = "x-user-token"
        const val DEFAULT_USER_TOKEN_HEADER = "default"
    }

    override fun onInit(httpService: HttpService) {
        // Do Nothing
    }

    override fun beforeExecute(httpService: HttpService) {
        httpService.setSsl(true)
        httpService.setConnectionTimeout(TimeUnit.SECONDS.toMillis(30))

        httpService.setUserAgent(USER_AGENT_HEADER_VALUE)
        httpService.addHeader(HttpService.ACCEPT_LANGUAGE_HEADER, LocaleUtils.getAcceptLanguage())
        httpService.addHeader(HttpService.CONTENT_TYPE_HEADER, MimeType.JSON_UTF8)
        httpService.addHeader(HttpService.ACCEPT_HEADER, MimeType.JSON)
        httpService.addHeader(HttpService.ACCEPT_ENCODING_HEADER, HttpService.GZIP_ENCODING)
        httpService.addHeader(USER_TOKEN_HEADER, userToken)
    }

    override fun afterExecute(httpService: HttpService, httpResponse: HttpResponseWrapper) {
        // Do Nothing
    }
}
