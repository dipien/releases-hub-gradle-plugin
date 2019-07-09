package com.releaseshub.gradle.plugin;

import com.releaseshub.gradle.plugin.task.ListDependenciesTask;

import org.gradle.api.Plugin;
import org.gradle.api.Project;


public class ReleasesHubGradlePlugin implements Plugin<Project> {
	
	public static final String EXTENSION_NAME = "releasesHub";
	
	@Override
	public void apply(Project project) {
		project.getExtensions().create(EXTENSION_NAME, ReleasesHubGradlePlugin.class, project);
		project.getTasks().create("listDependencies", ListDependenciesTask.class);
	}

}
