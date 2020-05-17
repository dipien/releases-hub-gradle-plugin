package com.releaseshub.gradle.plugin.common

import org.gradle.api.Project

private val propertyResolverCache = mutableMapOf<Project, PropertyResolver>()

val Project.propertyResolver: PropertyResolver
    get() = propertyResolverCache.getOrPut(this) { PropertyResolver(this) }
