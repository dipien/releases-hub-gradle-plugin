package com.releaseshub.gradle.plugin.common

import org.gradle.api.Project
import java.util.WeakHashMap

private val propertyResolverCache = WeakHashMap<Project, PropertyResolver>()

val Project.propertyResolver: PropertyResolver
    get() = propertyResolverCache.getOrPut(this) { PropertyResolver(this) }
