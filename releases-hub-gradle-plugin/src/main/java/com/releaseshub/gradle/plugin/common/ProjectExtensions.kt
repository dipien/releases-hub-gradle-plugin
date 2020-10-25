package com.releaseshub.gradle.plugin.common

import org.gradle.api.Project

// TODO This is causing a memory leak
// private var propertyResolverCache: PropertyResolver? = null
//
// val Project.propertyResolver: PropertyResolver
//     get() {
//         if (propertyResolverCache == null) {
//             propertyResolverCache = PropertyResolver(this.rootProject)
//         }
//         return propertyResolverCache!!
//     }

val Project.propertyResolver: PropertyResolver
    get() = PropertyResolver(this)
