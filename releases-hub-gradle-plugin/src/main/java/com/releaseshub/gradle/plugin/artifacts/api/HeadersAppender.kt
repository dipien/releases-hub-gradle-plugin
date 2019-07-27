package com.releaseshub.gradle.plugin.artifacts.api

import com.jdroid.java.http.HttpResponseWrapper
import com.jdroid.java.http.HttpService
import com.jdroid.java.http.HttpServiceProcessor
import com.jdroid.java.http.MimeType
import com.jdroid.java.utils.LocaleUtils

class HeadersAppender : HttpServiceProcessor {

    companion object {
        private const val USER_AGENT_HEADER_VALUE = "gradle"
        const val USER_TOKEN_HEADER = "x-user-token"
        // TODO
        const val DEFAULT_USER_TOKEN_HEADER = "default"
    }

    override fun onInit(httpService: HttpService) {
        // Do Nothing
    }

    override fun beforeExecute(httpService: HttpService) {
        httpService.setSsl(true)

        // User Agent header
        httpService.setUserAgent(USER_AGENT_HEADER_VALUE)

        addLanguageHeader(httpService)
        addUserTokenHeader(httpService)

        httpService.addHeader(HttpService.CONTENT_TYPE_HEADER, MimeType.JSON_UTF8)
        httpService.addHeader(HttpService.ACCEPT_HEADER, MimeType.JSON)
        httpService.addHeader(HttpService.ACCEPT_ENCODING_HEADER, HttpService.GZIP_ENCODING)
    }

    private fun addLanguageHeader(httpService: HttpService) {
        httpService.addHeader(HttpService.ACCEPT_LANGUAGE_HEADER, LocaleUtils.getAcceptLanguage())
    }

    private fun addUserTokenHeader(httpService: HttpService) {
        httpService.addHeader(USER_TOKEN_HEADER, DEFAULT_USER_TOKEN_HEADER)
    }

    override fun afterExecute(httpService: HttpService, httpResponse: HttpResponseWrapper) {
        // Do Nothing
    }
}