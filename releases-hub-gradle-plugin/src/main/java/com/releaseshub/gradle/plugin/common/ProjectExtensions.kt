package com.releaseshub.gradle.plugin.common

import org.gradle.api.Project

private var propertyResolverCache: PropertyResolver? = null

val Project.propertyResolver: PropertyResolver
    get() {
        if (propertyResolverCache == null) {
            propertyResolverCache = PropertyResolver(this)
        }
        return propertyResolverCache!!
    }
