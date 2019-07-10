package com.releaseshub.gradle.plugin;

import com.releaseshub.gradle.plugin.common.PropertyResolver;

import org.gradle.api.Project;


public class ReleasesHubGradlePluginExtension {

	private PropertyResolver propertyResolver;

	public ReleasesHubGradlePluginExtension(Project project) {
		propertyResolver = new PropertyResolver(project);
	}

}
