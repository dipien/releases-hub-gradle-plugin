package com.releaseshub.gradle.plugin.common;

import com.releaseshub.gradle.plugin.ReleasesHubGradlePlugin;
import com.releaseshub.gradle.plugin.ReleasesHubGradlePluginExtension;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;


public abstract class AbstractTask extends DefaultTask {
	
	protected PropertyResolver propertyResolver;
	
	@TaskAction
	public final void doExecute() {
		propertyResolver = new PropertyResolver(getProject());
		onExecute();
	}
	
	protected ReleasesHubGradlePluginExtension getExtension() {
		return (ReleasesHubGradlePluginExtension)getProject().getExtensions().findByName(ReleasesHubGradlePlugin.EXTENSION_NAME);
	}
	
	protected abstract void onExecute();
	
}
