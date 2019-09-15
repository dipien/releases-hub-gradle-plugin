package com.releaseshub.gradle.plugin.artifacts.api

import com.jdroid.java.http.HttpServiceProcessor
import com.jdroid.java.http.Server

enum class AppServer(
    private val segment: String?,
    private val supportsSsl: Boolean,
    private val isProduction: Boolean
) : Server {

    PROD(null, true, true) {
        override fun getDomain(): String? {
            return "api.releaseshub.com"
        }
    },
    DEV("/app/api", false, false) {
        override fun getDomain(): String? {
            return "localhost:8080"
        }
    };

    open fun getDomain(): String? {
        return null
    }

    override fun getServerName(): String {
        return name
    }

    override fun getBaseUrl(): String {
        val urlBuilder = StringBuilder()
        urlBuilder.append(getDomain())
        if (segment != null) {
            urlBuilder.append(segment)
        }
        return urlBuilder.toString()
    }

    override fun supportsSsl(): Boolean {
        return supportsSsl
    }

    override fun isProduction(): Boolean {
        return isProduction
    }

    override fun getHttpServiceProcessors(): List<HttpServiceProcessor> {
        return listOf()
    }

    override fun instance(name: String): Server {
        return valueOf(name)
    }
}