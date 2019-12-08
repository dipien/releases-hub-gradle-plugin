package com.releaseshub.gradle.plugin.artifacts.api

import com.jdroid.java.http.HttpResponseWrapper
import com.jdroid.java.http.HttpService
import com.jdroid.java.http.HttpServiceProcessor
import com.jdroid.java.http.MimeType
import com.jdroid.java.utils.LocaleUtils

class HeadersAppender(private val appVersion: String, private val userToken: String) : HttpServiceProcessor {

    companion object {
        private const val USER_AGENT_HEADER_VALUE = "gradle"
        const val USER_TOKEN_HEADER = "x-user-token"
        const val DEFAULT_USER_TOKEN_HEADER = "default"
        const val CLIENT_APP_VERSION_HEADER = "clientAppVersion"
    }

    override fun onInit(httpService: HttpService) {
        // Do Nothing
    }

    override fun beforeExecute(httpService: HttpService) {
        httpService.setSsl(true)

        // User Agent header
        httpService.setUserAgent(USER_AGENT_HEADER_VALUE)

        addLanguageHeader(httpService)

        httpService.addHeader(HttpService.CONTENT_TYPE_HEADER, MimeType.JSON_UTF8)
        httpService.addHeader(HttpService.ACCEPT_HEADER, MimeType.JSON)
        httpService.addHeader(HttpService.ACCEPT_ENCODING_HEADER, HttpService.GZIP_ENCODING)

        httpService.addHeader(USER_TOKEN_HEADER, userToken)
        httpService.addHeader(CLIENT_APP_VERSION_HEADER, appVersion)
    }

    private fun addLanguageHeader(httpService: HttpService) {
        httpService.addHeader(HttpService.ACCEPT_LANGUAGE_HEADER, LocaleUtils.getAcceptLanguage())
    }

    override fun afterExecute(httpService: HttpService, httpResponse: HttpResponseWrapper) {
        // Do Nothing
    }
}
